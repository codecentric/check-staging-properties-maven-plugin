package de.codecentric;

/*
 * #%L
 * check-staging-properties-maven-plugin
 * %%
 * Copyright (C) 2016 codecentric AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckStagingPropertiesMojoTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldContainEmptyList() throws MojoExecutionException {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();
        ArrayList<Properties> properties = mojo.getProperties();
        assertEquals(0, properties.size());
    }

    @Test
    public void shouldNotUseNonPropertiesFiles() throws IOException, MojoExecutionException {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo();

        folder.newFolder("child");
        folder.newFile("app-DEV.properties");
        folder.newFile("app-PRD.properties");
        folder.newFile(".DS_Store");
        folder.newFile("test.pdf");

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
    public void shouldContainEmptyPropertiesListWhenInputFileDoesNotExist() throws MojoExecutionException {
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(new File("/does/not/exist"), true, null);
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

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(folder.getRoot(), false, null);
        exception.expect(MojoFailureException.class);
        mojo.execute();
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

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(folder.getRoot(), false, null);
        exception.expect(MojoFailureException.class);

        mojo.execute();
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

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(folder.getRoot(), false, null);
        exception.expect(MojoFailureException.class);

        mojo.execute();
    }

    @Test
    public void groupPatternMatchingOfFilenames() throws Exception {
        this.createTestPropertiesFile("test-DEV.properties", "test.one=one\ntest.two=two");
        this.createTestPropertiesFile("test-PRD.properties", "test.one=one\ntest.two=two");
        this.createTestPropertiesFile("bla-DEV.properties", "bla.bla=bla");
        this.createTestPropertiesFile("bla-DEV.properties", "bla.bla=bla");

        ArrayList<String> groups = new ArrayList<>();
        groups.add("test-.*");
        groups.add("bla-.*");

        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(folder.getRoot(), false, groups);
        mojo.execute();
    }

    @Test
    public void unreadableFile() throws Exception {
        File f = this.createTestPropertiesFile("test-DEV.properties", "test.one=one\ntest.two=two");
        boolean successful = f.setReadable(false);
        assertTrue(successful);
        ArrayList<String> groups = new ArrayList<>();
        groups.add("test-.*");
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(folder.getRoot(), false, groups);
        exception.expect(MojoExecutionException.class);
        mojo.execute();
    }

    @Test
    public void fileAsDirectory() throws Exception {
        File f = this.createTestPropertiesFile("test.properties", "");
        TestCheckStagingPropertiesMojo mojo = new TestCheckStagingPropertiesMojo(f, false, new ArrayList<String>());
        mojo.getProperties();

    }

    private File createTestPropertiesFile(String filename, String content) throws Exception {
        File f = new File(folder.getRoot().toString() + "/" + filename);
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write(content);
        w.close();
        return f;
    }

    private class TestCheckStagingPropertiesMojo extends CheckStagingPropertiesMojo {
        TestCheckStagingPropertiesMojo() {
            this.directory = folder.getRoot();
            this.breakBuild = true;
            this.groups = null;
        }

        TestCheckStagingPropertiesMojo(File directory, Boolean breakBuild, List<String> groups) {
            this.directory = directory;
            this.breakBuild = breakBuild;
            this.groups = groups;
        }
    }

}
