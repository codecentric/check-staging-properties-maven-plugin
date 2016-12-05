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
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CheckStagingPropertiesMojoTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private void writePropertiesFile(String filename, String content) throws Exception {
        File f = new File(folder.getRoot().toString() + "/" + filename);
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write(content);
        w.close();
    }

    @Test
    public void testGetProperties() throws Exception {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        ArrayList<Properties> properties = mojo.getProperties();
        assertEquals(0, properties.size());

        folder.newFolder("child");
        folder.newFile("app-DEV.properties");
        folder.newFile("app-PRD.properties");
        folder.newFile(".DS_Store");
        folder.newFolder("test.pdf");

        properties = mojo.getProperties();
        assertEquals(2, properties.size());
    }

    @Test
    public void testGetPropertiesRecursive() throws Exception {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        ArrayList<Properties> properties = mojo.getProperties();
        assertEquals(0, properties.size());

        folder.newFile("app-DEV.properties");
        folder.newFile("app-PRD.properties");
        folder.newFolder("child");
        folder.newFile("child/app-STG.properties");

        properties = mojo.getProperties();
        assertEquals(3, properties.size());
    }

    @Test
    public void testGetPropertiesDirectoryDoesNotExist() {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(new File("/does/not/exist"), true);
        assertEquals(0, mojo.getProperties().size());
    }

    @Test
    public void testExecuteEmptyDirectory() throws Exception {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        mojo.execute(); // should work
    }

    @Test
    public void testExecuteSizesDoNotEqual() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);
        mojo.execute();
    }

    @Test
    public void testExecuteSizesDoNotEqualDoNotBreak() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);
        mojo2.execute();
    }

    @Test
    public void testExecuteKeysDoNotEqual() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one =\ntest.three =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);
        mojo.execute();
    }

    @Test
    public void testExecuteKeysDoNotEqualDoNotBreak() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one =\ntest.three =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);
        mojo2.execute();
    }

    @Test
    public void testExecuteValuesAreNotEqual() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one = one\ntest.two =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        exception.expect(MojoExecutionException.class);
        mojo.execute();
    }

    @Test
    public void testExecuteValuesAreNotEqualDoNotBreak() throws Exception {
        this.writePropertiesFile("app-DEV.properties", "test.one = one\ntest.two =");
        this.writePropertiesFile("app-PRD.properties", "test.one =\ntest.two =");

        TestCheckStagingPropertiesMojo mojo2 = new TestCheckStagingPropertiesMojo(folder.getRoot(), false);
        exception.expect(MojoFailureException.class);
        mojo2.execute();
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
