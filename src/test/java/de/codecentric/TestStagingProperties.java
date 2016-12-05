package de.codecentric;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestStagingProperties {
    private static Properties getProperties(String value) throws Exception {
        InputStream stream = new ByteArrayInputStream(value.getBytes("UTF-8"));
        Properties props = new Properties();
        props.load(stream);
        return props;
    }

    private static ArrayList<Properties> getPropertiesList(String p1) throws Exception {
        ArrayList<Properties> props = new ArrayList<Properties>();
        props.add(getProperties(p1));
        return props;
    }

    private static ArrayList<Properties> getPropertiesList(String p1, String p2) throws Exception {
        ArrayList<Properties> props = new ArrayList<Properties>();
        props.add(getProperties(p1));
        props.add(getProperties(p2));
        return props;
    }

    private static ArrayList<Properties> getPropertiesList(String p1, String p2, String p3) throws Exception {
        ArrayList<Properties> props = new ArrayList<Properties>();
        props.add(getProperties(p1));
        props.add(getProperties(p2));
        props.add(getProperties(p3));
        return props;
    }

    @BeforeClass
    public static void setUp() {
        new StagingProperties(); // for coverage
    }

    @Test
    public void testSizesEqual() throws Exception {
        assertFalse(StagingProperties.sizesEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two="
        )));
        assertTrue(StagingProperties.sizesEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.four"
        )));
        assertTrue(StagingProperties.sizesEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.three"
        )));
    }

    @Test
    public void testValuesAreEmpty() throws Exception {
        assertFalse(StagingProperties.valuesAreEmpty(getPropertiesList(
                "test.one = one\ntest.two=\ntest.three"
        )));
        assertTrue(StagingProperties.valuesAreEmpty(getPropertiesList(
                "test.one =\ntest.two=\ntest.three"
        )));
    }

    @Test
    public void testKeysEqual() throws Exception {
        assertFalse(StagingProperties.keysEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.four"
        )));
        assertFalse(StagingProperties.keysEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.four"
        )));

        assertFalse(StagingProperties.keysEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two="
        )));
        assertTrue(StagingProperties.keysEqual(getPropertiesList(
                "test.one =\ntest.two=\ntest.three",
                "test.one =\ntest.two=\ntest.three"
        )));
    }
}
