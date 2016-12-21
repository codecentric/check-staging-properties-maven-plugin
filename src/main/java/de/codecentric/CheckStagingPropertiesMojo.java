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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
class CheckStagingPropertiesMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/resources")
    File directory;

    @Parameter(defaultValue = "true")
    boolean breakBuild;

    @Parameter
    List<String> groups;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (groups != null && groups.size() > 0) {
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
            if (!Files.isPropertiesFile(file) || !Files.matchesGroup(file, pattern)) {
                continue;
            }

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(file.getAbsolutePath()));
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot read file `" + file.getName() + "`", e);
            }
            propertyFiles.add(props);
        }
        return propertyFiles;
    }

    ArrayList<Properties> getProperties() throws MojoExecutionException {
        return getPropertiesRecursively(directory, null);
    }

    private ArrayList<Properties> getProperties(String pattern) throws MojoExecutionException {
        return getPropertiesRecursively(directory, pattern);
    }

    private void error(String msg) throws MojoExecutionException, MojoFailureException {
        if (breakBuild) {
            throw new MojoExecutionException(msg);
        } else {
            throw new MojoFailureException(msg);
        }
    }

    private void doChecks(String group, ArrayList<Properties> props)
            throws MojoExecutionException, MojoFailureException {
        if (props.size() > 1) {
            if (!StagingProperties.sizesEqual(props)) {
                error("In group `" + group + "`: Sizes (number of keys) are not equal");
            }

            if (!StagingProperties.keysEqual(props)) {
                error("In group `" + group + "`: Keys are not equal");
            }

            if (!StagingProperties.valuesPresent(props)) {
                error("In group `" + group + "`: Some values are empty");
            }
        }
    }

}
