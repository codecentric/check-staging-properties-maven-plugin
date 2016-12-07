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
    public void testValuesPresent() throws Exception {
        assertFalse(StagingProperties.valuesPresent(getPropertiesList(
                "test.one =\ntest.two=\ntest.three"
        )));
        assertTrue(StagingProperties.valuesPresent(getPropertiesList(
                "test.one = one\ntest.two = two\ntest.three = three"
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
