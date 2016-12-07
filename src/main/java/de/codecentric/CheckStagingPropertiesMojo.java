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

@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
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
                doChecks(getProperties(group));
            }
        } else {
            doChecks(getProperties(null));
        }
    }

    private ArrayList<Properties> getPropertiesRecursively(File directory, String pattern) {
        ArrayList<Properties> propertyFiles = new ArrayList<Properties>();
        if (!directory.exists()) {
            this.getLog().warn("Directory `" + directory.getName() + "` does not exist. Skipping.");
            return propertyFiles;
        }

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                propertyFiles.addAll(this.getPropertiesRecursively(file, pattern));
                continue;
            }
            if (!Files.isPropertiesFile(file) || !matchesGroup(pattern,file)) {
                continue;
            }

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(file.getAbsolutePath()));
            } catch (IOException e) {
                this.getLog().warn("Cannot read file `" + file.getName() + "`: " + e.getMessage());
                continue;
            }
            propertyFiles.add(props);
        }
        return propertyFiles;
    }

    ArrayList<Properties> getProperties() {
        return this.getPropertiesRecursively(directory, null);
    }

    private ArrayList<Properties> getProperties(String pattern) {
        return this.getPropertiesRecursively(directory, pattern);
    }

    private void error(String msg) throws MojoExecutionException, MojoFailureException {
        if (breakBuild) {
            throw new MojoExecutionException(msg);
        } else {
            throw new MojoFailureException(msg);
        }
    }

    private void doChecks(ArrayList<Properties> props) throws MojoExecutionException, MojoFailureException {
        if (props.size() > 1) {
            if (!StagingProperties.sizesEqual(props)) {
                this.error("Sizes (number of keys) do not equal");
            }

            if (!StagingProperties.keysEqual(props)) {
                this.error("Keys do not equal");
            }

            if (!StagingProperties.valuesAreEmpty(props)) {
                this.error("Values are not empty");
            }
        }
    }

    private boolean matchesGroup(String pattern, File f) {
        return pattern == null || !f.getName().matches(pattern);
    }
}
