package io.quarkus.pnc.importer;

import io.quarkus.pnc.importer.rest.Artifact;
import io.quarkus.pnc.importer.rest.ArtifactEndpoint;
import io.quarkus.pnc.importer.rest.PageParameters;
import io.quarkus.pnc.importer.rest.SwaggerConstants;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@CommandLine.Command(name = "import")
public class Import implements Runnable {

    /**
     * TODO: this should be read from the file
     */
    private static final String DEFAULT_SYSTEM_IMAGE = "builder-rhel-7-j11.0.11-9-mvn3.6.3-gradle7.0.2:1.0.6";

    public static final String GREEN = "\u001b[32m";
    public static final String RESET = "\u001b[39m";
    public static final String REFS_TAGS = "refs/tags/";
    public static final String REFS_HEADS = "refs/heads/";

    @CommandLine.Option(names = "-p", description = "Path to the CPAAS config checkout (usually a checkout of Gitlab application-services/quarkus-components)")
    Path path;

    @CommandLine.Parameters(description = "Artifact identifier to search for")
    String artifact;

    @Inject
    @RestClient
    ArtifactEndpoint artifactEndpoint;

    @Override
    public void run() {
        print("Searching for artifacts...");
        PageParameters pageParams = new PageParameters();
        pageParams.setPageSize(SwaggerConstants.MAX_PAGE_SIZE);
        pageParams.setQ("identifier=like=\"%" + artifact + "%\"");
        pageParams.setSort("sort=desc=build.startTime");
        var results = artifactEndpoint.getAll(pageParams, null, null, null);
        if (results.getContent().isEmpty()) {
            System.out.println("No existing builds found");
            System.exit(1);
        }

        print("Please select the artifact to use as the base for the build:");
        Map<Integer, Artifact> ids = new TreeMap<>();
        int count = 1;
        for (var artifact : results.getContent()) {
            if (artifact.getBuild() == null) {
                continue;
            }
            //we only display the pom artifacts, so there is not heaps of double ups
            if (!artifact.getIdentifier().contains(":pom:")) {
                continue;
            }
            ids.put(count, artifact);
            if (!Objects.equals(artifact.getBuild().getScmRepository().getPreBuildSyncEnabled(), true)) {
                System.out.println("[" + GREEN + count + RESET + "] " + artifact.getIdentifier() + " [no pre build sync]");
            } else {
                System.out.println("[" + GREEN + count + RESET + "] " + artifact.getIdentifier());
            }
            count++;
        }

        int selection = Integer.parseInt(System.console().readLine());
        Artifact selectedArtifact = ids.get(selection);
        print("Selected: " + selectedArtifact.getIdentifier());
        if (selectedArtifact.getBuild() == null) {
            System.err.println("Could not proceed: no build information for selected artifact");
            System.exit(1);
        }
        if (selectedArtifact.getBuild().getScmRevision() == null) {
            System.err.println("Could not proceed: no SCM information for selected build");
            System.exit(1);
        }
        if (!Objects.equals(selectedArtifact.getBuild().getScmRepository().getPreBuildSyncEnabled(), true)) {
            System.err.println("Pre build sync not enabled for " + selectedArtifact.getBuild().getScmRepository().getExternalUrl() + " in PNC, this is required");
            System.exit(1);
        }
        String tag = generateUpstreamSourcesYaml(selectedArtifact);
        generateBuildYaml(selectedArtifact, tag);

    }

    private void generateBuildYaml(Artifact selectedArtifact, String upstreamTag) {
        try {
            String scm = selectedArtifact.getBuild().getScmRepository().getExternalUrl();

            String projectName = selectedArtifact.getBuild().getProject().getName();
            String outName = projectName.replaceAll("/", "-");
            Path output = path.resolve("build-config.yaml");
            var existing = new ArrayList<>(Files.readAllLines(output, StandardCharsets.UTF_8));
            boolean foundHashBang = false;
            boolean versionAdded = false;
            for (var iter = existing.listIterator(); iter.hasNext(); ) {
                String line = iter.next();
                if (!foundHashBang) {
                    if (line.startsWith("#!")) {
                        foundHashBang = true;
                    }
                } else if (!versionAdded) {
                    if (!line.startsWith("#!")) {
                        iter.previous();
                        iter.add("#!" + outName + "-version=" + upstreamTag);
                        versionAdded = true;
                    }
                } else {
                    if (line.equals("builds:")) {

                        iter.add("  - name: \"{{ " + outName + "-version }}\"");
                        iter.add("    project: " + projectName);
                        iter.add("    scmUrl: " + scm);
                        iter.add("    scmRevision: \"{{ " + outName + " }}\"");
                        iter.add("    buildScript: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildScript());
                        iter.add("    buildType: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildType());
                        if (!Objects.equals(DEFAULT_SYSTEM_IMAGE, selectedArtifact.getBuild().getEnvironment().getSystemImageId())) {
                            iter.add("    systemImageId: " + selectedArtifact.getBuild().getEnvironment().getSystemImageId());
                        }
                        break;
                    }
                }
            }
            Files.write(output, existing, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUpstreamSourcesYaml(Artifact selectedArtifact) {
        try {
            String scm = selectedArtifact.getBuild().getScmRepository().getExternalUrl();
            String inferredTag = selectedArtifact.getBuild().getScmTag().replaceAll("\\.redhat.*", "").replaceAll("-redhat.*", "");

            String selectedCommit = "<< FIX ME >>";
            String branch = "<< FIX ME >>";
            String actualTag;

            Path publicCheckout = Files.createTempDirectory("public-checkout");
            List<Ref> possibleTags = new ArrayList<>();
            List<String> possibleBranches = new ArrayList<>();
            var publicGit = Git.cloneRepository().setDirectory(publicCheckout.toFile())
                    .setURI(selectedArtifact.getBuild().getScmRepository().getExternalUrl())
                    .call();
            List<Ref> allTags = publicGit.tagList().call();
            for (var ref : allTags) {
                if (ref.getName().contains(inferredTag)) {
                    possibleTags.add(ref);
                }
            }
            if (possibleTags.size() == 1) {
                Ref ref = possibleTags.get(0);
                if (ref.getPeeledObjectId() != null) {
                    selectedCommit = ref.getPeeledObjectId().name();
                } else  {
                    selectedCommit = ref.getObjectId().name();
                }
                actualTag = ref.getName().replaceAll(REFS_TAGS, "");
            } else {
                print("Multiple potential tags found, please select the appropriate one: ");
                Map<Integer, Ref> ids = new TreeMap<>();
                int count = 1;
                for (var ref : possibleTags) {
                    ids.put(count, ref);
                    System.out.println("[" + GREEN + count + RESET + "] " + ref.getName());
                    count++;
                }
                int selection = Integer.parseInt(System.console().readLine());
                var selectedRef = ids.get(selection);
                selectedCommit = selectedRef.getPeeledObjectId().name();
                actualTag = selectedRef.getName().replaceAll(REFS_TAGS, "");
            }

            for (var b : publicGit.branchList().call()) {
                if (publicGit.log().add(b.getObjectId()).addRange(ObjectId.fromString(selectedCommit), b.getObjectId())
                        .call().iterator().hasNext()) {
                    possibleBranches.add(b.getName());
                }
            }
            if (possibleBranches.size() == 1) {
                branch = possibleBranches.get(0);
            } else {
                print("Multiple potential tags found, please select the appropriate one: ");
                Map<Integer, String> ids = new TreeMap<>();
                int count = 1;
                for (var ref : possibleBranches) {
                    ids.put(count, ref);
                    System.out.println("[" + GREEN + count + RESET + "] " + ref);
                    count++;
                }
                int selection = Integer.parseInt(System.console().readLine());
                branch = ids.get(selection);
            }
            branch = branch.replaceAll(REFS_HEADS, "");

            Path output = path.resolve("upstream_sources.yml");
            Files.writeString(output, "- automerge: 'yes'\n" +
                    "  branch: " + branch + "\n" +
                    "  commit: " + selectedCommit + "\n" +
                    "  dest_formats:\n" +
                    "    branch:\n" +
                    "      gen_source_repos: true\n" +
                    "  update_policy:\n" +
                    "  - tagged\n" +
                    "  url: " + scm + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            return actualTag;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void print(String s) {
        System.out.println(GREEN + s + RESET);
    }
}
