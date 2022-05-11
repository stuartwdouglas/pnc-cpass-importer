package io.quarkus.pnc.importer;

import io.quarkus.pnc.importer.rest.Artifact;
import io.quarkus.pnc.importer.rest.ArtifactEndpoint;
import io.quarkus.pnc.importer.rest.ArtifactRef;
import io.quarkus.pnc.importer.rest.BuildConfiguration;
import io.quarkus.pnc.importer.rest.BuildConfigurationEndpoint;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Inject
    @RestClient
    BuildConfigurationEndpoint buildConfigurationEndpoint;

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
        List<Artifact> possible = new ArrayList<>();

        for (var artifact : results.getContent()) {
            if (artifact.getBuild() == null) {
                continue;
            }
            //we only display the pom artifacts, so there is not heaps of double ups
            if (!artifact.getIdentifier().contains(":pom:")) {
                continue;
            }
            possible.add(artifact);
        }
        Artifact selectedArtifact = selectFromList(possible, s -> s.getIdentifier() + (s.getBuild().getScmRepository().getPreBuildSyncEnabled() ? "" : " [no pre build sync]"));
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


        var buildconfigId = selectedArtifact.getBuild().getBuildConfigRevision().getId();
        var buildConfig = buildConfigurationEndpoint.getSpecific(buildconfigId);

        String tag = generateUpstreamSourcesYaml(selectedArtifact);
        generateBuildYaml(selectedArtifact, tag, buildConfig);

    }

    private void generateBuildYaml(Artifact selectedArtifact, String upstreamTag, BuildConfiguration buildConfiguration) {
        try {
            String scm = selectedArtifact.getBuild().getScmRepository().getExternalUrl();

            String projectName = selectedArtifact.getBuild().getProject().getName();
            Path output = path.resolve("build-config.yaml");
            var existing = new ArrayList<>(Files.readAllLines(output, StandardCharsets.UTF_8));
            boolean foundHashBang = false;
            boolean versionAdded = false;
            String versionedName = versionName(buildConfiguration.getName());
            for (var iter = existing.listIterator(); iter.hasNext(); ) {
                String line = iter.next();
                if (!foundHashBang) {
                    if (line.startsWith("#!")) {
                        foundHashBang = true;
                    }
                } else if (!versionAdded) {
                    if (!line.startsWith("#!")) {
                        iter.previous();
                        iter.add("#!" + versionedName + "=" + upstreamTag);
                        versionAdded = true;
                    }
                } else {
                    if (line.equals("builds:")) {

                        iter.add("  - name: \"{{ " + versionedName + " }}\"");
                        iter.add("    project: " + buildConfiguration.getProject().getName());
                        iter.add("    scmUrl: " + scm);
                        iter.add("    scmRevision: \"{{ " + versionedName + " }}\"");
                        iter.add("    buildScript: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildScript());
                        iter.add("    buildType: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildType());
                        if (!Objects.equals(DEFAULT_SYSTEM_IMAGE, selectedArtifact.getBuild().getEnvironment().getSystemImageId())) {
                            iter.add("    systemImageId: " + selectedArtifact.getBuild().getEnvironment().getSystemImageId());
                        }
                        if (!buildConfiguration.getDependencies().isEmpty()) {
                            iter.add("    dependencies:");
                            for (var entry : buildConfiguration.getDependencies().values()) {
                                iter.add("    - {{ \"" + versionName(entry.getName()) + "\" }}");
                            }
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

    private String versionName(String name) {
        int pos = name.lastIndexOf('-');
        if (pos == -1) {
            return name + "-version";
        }
        return name.substring(0, pos) + "-version";
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
            if (possibleTags.size() == 0) {
                possibleTags.addAll(allTags);
            }
            possibleTags.sort(Comparator.comparing(Object::toString));
            if (possibleTags.size() == 1) {
                Ref ref = possibleTags.get(0);
                if (ref.getPeeledObjectId() != null) {
                    selectedCommit = ref.getPeeledObjectId().name();
                } else {
                    selectedCommit = ref.getObjectId().name();
                }
                actualTag = ref.getName().replaceAll(REFS_TAGS, "");
            } else {
                print("Multiple potential tags found, please select the appropriate one: ");
                var selectedRef = selectFromList(possibleTags, Ref::getName);
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
                branch = selectFromList(possibleBranches, Object::toString);
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

    private static void print(String s) {
        System.out.println(GREEN + s + RESET);
    }

    static <T> T selectFromList(List<T> list, Function<T, String> mapping) {
        if (list.isEmpty()) {
            throw new RuntimeException("No results to choose from");
        }
        Map<Integer, T> ids = new TreeMap<>();
        int count = 1;
        for (var obj : list) {
            ids.put(count, obj);
            System.out.println("[" + GREEN + count + RESET + "] " + mapping.apply(obj));
            count++;
        }
        for (; ; ) {
            try {
                int selection = Integer.parseInt(System.console().readLine());
                T selected = ids.get(selection);
                print("Selected: " + mapping.apply(selected));
                return selected;
            } catch (Exception e) {
                System.out.println("Invalid selection");
            }
        }
    }
}
