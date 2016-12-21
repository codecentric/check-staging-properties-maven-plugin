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

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TestFiles {

    private static String getExtensionHelper(String filename) {
        return Files.getExtension(new File(filename));
    }

    private static Boolean isPropertiesFileHelper(String filename) {
        return Files.isPropertiesFile(new File(filename));
    }

    @BeforeClass
    public static void setUp() {
        new Files(); // for coverage.. sorry ;-)
    }

    @Test
    public void shouldExtractPdfAsFileExtension() {
        assertEquals("pdf", getExtensionHelper("test.pdf"));
    }

    @Test
    public void shouldExtractGzAsFileExtension() {
        assertEquals("gz", getExtensionHelper("bla.tar.gz"));
    }

    @Test
    public void shouldExtractPropertiesAsFileExtension() {
        assertEquals("properties", getExtensionHelper("app.properties"));
    }

    @Test
    public void shouldNotExtractFileExtensionOfTemporaryFiles() throws Exception {
        assertNull(getExtensionHelper(".DS_Store"));
    }

    @Test
    public void shouldNotDetectPdfAsPropertiesFile() {
        assertFalse(isPropertiesFileHelper("test.pdf"));
    }

    @Test
    public void shouldNotDetectTarAsPropertiesFile() {
        assertFalse(isPropertiesFileHelper("bla.tar.gz"));
    }

    @Test
    public void shouldDetectPropertiesFileCorrectly() {
        assertTrue(isPropertiesFileHelper("app.properties"));
    }

    @Test
    public void shouldNotDetectMacstoreFilesAsProperties() {
        assertFalse(isPropertiesFileHelper(".DS_Store"));
    }

    @Test
    public void matchesGroupShouldMatchWithoutPattern() {
        assertTrue(Files.matchesGroup(null, null));
    }

    @Test
    public void matchesGroupShouldMatch() {
        assertTrue(Files.matchesGroup(new File("abc.properties"), "ab.\\.properties"));
    }

    @Test
    public void matchesGroupShouldNotMatch() {
        assertFalse(Files.matchesGroup(new File("abc.properties"), "de.\\.properties"));
    }
}
