package de.codecentric;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CheckStagingPropertiesMojoTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldContainEmptyList() {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        ArrayList<Properties> properties = mojo.getProperties();
        assertEquals(0, properties.size());
    }

    @Test
    public void shouldNotUseNonPropertiesFiles() throws IOException {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();

        folder.newFolder("child");
        folder.newFile("app-DEV.properties");
        folder.newFile("app-PRD.properties");
        folder.newFile(".DS_Store");
        folder.newFolder("test.pdf");

        assertEquals(2, mojo.getProperties().size());
    }

    @Test
    public void testGetPropertiesRecursively() throws Exception {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();

        folder.newFile("app-DEV.properties");
        folder.newFile("app-PRD.properties");
        folder.newFolder("child");
        folder.newFile("child/app-STG.properties");

        assertEquals(3, mojo.getProperties().size());
    }

    @Test
    public void shouldContainEmptyPropertiesListWhenInputFileDoesNotExist() {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(new File("/does/not/exist"), true);
        assertEquals(0, mojo.getProperties().size());
    }

    @Test
    public void shouldExecutePluginWhenDirectoryEntryIsNotPreconfigured() throws Exception {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        mojo.execute(); // should work
    }

    @Test
    public void shouldBreakBuildWhenPropertiesSizesAreNotEqual() throws Exception {
        createTestPropertiesFile("app-DEV.properties", "test.one =");
        createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);

        mojo.execute();
    }

    @Test
    public void shouldNotBreakBuildWhenPropertiesSizesAreNotEqual() throws Exception {
        this.createTestPropertiesFile("app-DEV.properties", "test.one =");
        this.createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);
        mojo2.execute();
    }

    @Test
    public void shouldBreakBuildWhenPropertiesKeysAreNotEqual() throws Exception {
        this.createTestPropertiesFile("app-DEV.properties", "test.one =\ntest.three =");
        this.createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);

        mojo.execute();
    }

    @Test
    public void shouldNotBreakBuildWhenPropertiesKeysAreNotEqual() throws Exception {
        this.createTestPropertiesFile("app-DEV.properties", "test.one =\ntest.three =");
        this.createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);

        mojo2.execute();
    }

    @Test
    public void shouldBreakBuildWhenPropertiesValuesAreNotEqual() throws Exception {
        this.createTestPropertiesFile("app-DEV.properties", "test.one = one\ntest.two =");
        this.createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);

        mojo.execute();
    }

    @Test
    public void shouldNotBreakBuildWhenValuesAreNotEqual() throws Exception {
        this.createTestPropertiesFile("app-DEV.properties", "test.one = one\ntest.two =");
        this.createTestPropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);

        mojo2.execute();
    }

    private void createTestPropertiesFile(String filename, String content) throws Exception {
        File f = new File(folder.getRoot().toString() + "/" + filename);
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write(content);
        w.close();
    }

    private class TestCheckStagingPropertiesMojo extends CheckStagingPropertiesMojo {
        TestCheckStagingPropertiesMojo() {
            this.directory = folder.getRoot();
            this.breakBuild = true;
        }

        TestCheckStagingPropertiesMojo(File directory, Boolean breakBuild) {
            this.directory = directory;
            this.breakBuild = breakBuild;
        }
    }
}
