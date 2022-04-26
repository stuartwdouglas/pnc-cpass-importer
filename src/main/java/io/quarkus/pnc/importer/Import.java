package io.quarkus.pnc.importer;

import io.quarkus.pnc.importer.rest.Artifact;
import io.quarkus.pnc.importer.rest.ArtifactEndpoint;
import io.quarkus.pnc.importer.rest.PageParameters;
import io.quarkus.pnc.importer.rest.SwaggerConstants;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@CommandLine.Command(name = "import")
public class Import implements Runnable {
    public static final String GREEN = "\u001b[32m";
    public static final String RESET = "\u001b[39m";

    @CommandLine.Option(names = "-p", description = "Path to the CPAAS config checkout (usually a checkout of Gitlab application-services/quarkus-components)")
    String uri;


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
        generateBuildYaml(selectedArtifact);
        generateUpstreamSourcesYaml(selectedArtifact);

    }

    private void generateBuildYaml(Artifact selectedArtifact) {
        String scm = selectedArtifact.getBuild().getScmRepository().getExternalUrl();
        System.out.println("build-config.yaml fragment: \n\n\n");

        String projectName = selectedArtifact.getBuild().getProject().getName();
        String outName = projectName.replaceAll("/", "-");
        System.out.println("  - name: \"{{ " + outName + "-version }}\"\n" +
                "    project: " + projectName + "\n" +
                "    scmUrl: " + scm + "\n" +
                "    scmRevision: \"{{ " + outName + " }}\"\n" +
                "    buildScript: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildScript() + "\n" +
                "    buildType: " + selectedArtifact.getBuild().getBuildConfigRevision().getBuildType() + "\n");

        System.out.println("\n\n\n");
    }

    private void generateUpstreamSourcesYaml(Artifact selectedArtifact) {
        try {
            String scm = selectedArtifact.getBuild().getScmRepository().getExternalUrl();
            System.out.println("upstream_sources.yml fragment: \n\n\n");
            String inferredTag = selectedArtifact.getBuild().getScmTag().replaceAll("\\.redhat.*", "").replaceAll("-redhat.*", "");

            String selectedCommit = "<< FIX ME >>";
            String branch = "<< FIX ME >>";

            Path publicCheckout = Files.createTempDirectory("public-checkout");
            List<Ref> possibleTags = new ArrayList<>();
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
                selectedCommit = possibleTags.get(0).getPeeledObjectId().name();
            } else {
                System.out.println("Multiple potential tags found, please select the appropriate one: ");
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
            }

            String projectName = selectedArtifact.getBuild().getProject().getName();
            System.out.println(
                    "- automerge: 'yes'\n" +
                            "  branch: " + branch + "\n" +
                            "  commit: " + selectedCommit + "\n" +
                            "  dest_formats:\n" +
                            "    branch:\n" +
                            "      gen_source_repos: true\n" +
                            "  update_policy:\n" +
                            "  - tagged\n" +
                            "  url: " + scm + "\n");

            System.out.println("\n\n\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void print(String s) {
        System.out.println(GREEN + s + RESET);
    }
}
