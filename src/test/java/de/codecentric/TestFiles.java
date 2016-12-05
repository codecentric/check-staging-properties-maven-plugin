package de.codecentric;

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
        new Files(); // for coverage
    }

    @Test
    public void testGetExtension() throws Exception {
        assertEquals("pdf", getExtensionHelper("test.pdf"));
        assertEquals("gz", getExtensionHelper("bla.tar.gz"));
        assertEquals("properties", getExtensionHelper("app.properties"));
        assertNull(getExtensionHelper(".DS_Store"));
    }

    @Test
    public void testIsPropertiesFile() throws Exception {
        assertFalse(isPropertiesFileHelper("test.pdf"));
        assertFalse(isPropertiesFileHelper("bla.tar.gz"));
        assertFalse(isPropertiesFileHelper(".DS_Store"));
        assertTrue(isPropertiesFileHelper("app.properties"));
    }
}
