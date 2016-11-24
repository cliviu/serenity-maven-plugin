package net.serenitybdd.maven.plugins;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;


public class WhenImportingExternalTestResults {

    SerenityAdaptorMojo plugin;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;

    @Before
    public void setupPlugin() throws IOException {

        outputDirectory = temporaryFolder.newFolder("sample-output");

        plugin = new SerenityAdaptorMojo();
        plugin.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_data_from_specified_test_results() throws Exception {
        plugin.setFormat("xunit");
        plugin.setSource(getResourcesAt("/xunit-sample-output"));

        plugin.execute();

        assertThat(outputDirectory.list(jsonFiles())).hasSize(5);
    }

    @Test
    public void source_should_not_be_mandatory() throws MojoFailureException, MojoExecutionException, IOException {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("thucydides.adaptors.fileless","net.serenitybdd.maven.plugins.FilelessAdaptor");

        SerenityAdaptorMojo plugin = new SerenityAdaptorMojo(environmentVariables);
        plugin.setFormat("fileless");
        plugin.setOutputDirectory(outputDirectory);

        plugin.execute();

        assertThat(outputDirectory.list(jsonFiles())).hasSize(2);
    }

    private FilenameFilter jsonFiles() {
        return new FilenameFilter() {

            public boolean accept(File file, String filename) {
                return filename.endsWith(".json");
            }
        };
    }

    private File getResourcesAt(String path) {
        return new File(getClass().getResource(path).getFile());
    }
}
