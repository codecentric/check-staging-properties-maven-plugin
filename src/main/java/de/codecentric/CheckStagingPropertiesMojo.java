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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
class CheckStagingPropertiesMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/resources")
    File directory;

    @Parameter
    List<String> groups;

    private List<String> fileNames = new LinkedList<>();

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isGroupingEnabled()) {
            for (String group : groups) {
                doChecks(group, getProperties(group));
            }
        } else {
            doChecks("", getProperties());
        }
    }

    private ArrayList<Properties> getPropertiesRecursively(File directory, String pattern)
            throws MojoExecutionException {
        ArrayList<Properties> propertyFiles = new ArrayList<>(20);
        if (directory == null || !directory.exists()) {
            getLog().warn("Directory `" +
                    (directory == null ? "" : directory.getAbsolutePath()) +
                    "` does not exist. Skipping.");
            return propertyFiles;
        }

        final File[] files = directory.listFiles();
        if (files == null) {
            getLog().warn("Directory `" +
                    directory.getAbsolutePath() +
                    "` does not denote a directory. Skipping.");
            return propertyFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                propertyFiles.addAll(getPropertiesRecursively(file, pattern));
                continue;
            }
            if (!isPropertiesFile(file) || !matchesGroupPattern(pattern, file)) {
                continue;
            }

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(file.getAbsolutePath()));
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot read file `" + file.getName() + "`", e);
            }
            propertyFiles.add(props);
            fileNames.add(file.getName());
        }
        return propertyFiles;
    }

    ArrayList<Properties> getProperties() throws MojoExecutionException {
        return getPropertiesRecursively(directory, null);
    }

    private ArrayList<Properties> getProperties(String pattern) throws MojoExecutionException {
        return getPropertiesRecursively(directory, pattern);
    }

    private void doChecks(String group, ArrayList<Properties> props)
            throws MojoExecutionException, MojoFailureException {
        if (props.size() > 1) {
            if (!StagingProperties.sizesEqual(props)) {
                throw new MojoFailureException("In group `" + group + "`: Sizes (number of keys) are not equal");
            }

            if (!StagingProperties.keysEqual(props)) {
                throw new MojoFailureException("In group `" + group + "`: Keys are not equal");
            }

            if (!StagingProperties.valuesPresent(props)) {
                List<String> errors = new LinkedList<>();
                for (int i = 0; i < fileNames.size(); i++) {
                    String missingValues = "";
                    for (Object key : props.get(i).keySet()) {
                        String value = (String) props.get(i).get(key);
                        if (value == null || "".equals(value)) {
                            missingValues += key + "\n";
                        }
                    }
                    errors.add("file: " + fileNames.get(i) + ", keys: \n" + missingValues);
                }
                if (group == null || "".equals(group)) {
                    throw new MojoFailureException("There are some empty values in: " + errors + "`");
                } else {
                    throw new MojoFailureException("There are some empty values in group `" + group + "` and `" + errors + "`");
                }
            }
        }
    }

    private boolean isGroupingEnabled() {
        return groups != null && groups.size() > 0;
    }

    private boolean matchesGroupPattern(String pattern, File file) {
        return Files.matchesGroup(file, pattern);
    }

    private boolean isPropertiesFile(File file) {
        return Files.isPropertiesFile(file);
    }
}
