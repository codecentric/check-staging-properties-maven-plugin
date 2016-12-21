# check-staging-properties-maven-plugin

[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/codecentric/check-staging-properties-maven-plugin/master/LICENSE)
[![Build Status](https://travis-ci.org/codecentric/check-staging-properties-maven-plugin.svg?branch=master)](https://travis-ci.org/codecentric/check-staging-properties-maven-plugin)
[![Codacy grade](https://img.shields.io/codacy/grade/8fd7bac6edac417a8451387286fe6917.svg)](https://www.codacy.com/app/britter/check-staging-properties-maven-plugin/dashboard)
[![Coveralls](https://img.shields.io/coveralls/codecentric/check-staging-properties-maven-plugin.svg)](https://coveralls.io/github/codecentric/check-staging-properties-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/de.codecentric/check-staging-properties-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/de.codecentric/check-staging-properties-maven-plugin/)

Maven-plugin for checking the equality of several `.properties`-files for Mule applications (at least mostly used with, but not necessary). This plugin is perfect for you, if you have multiple `.properties`-files for different staging environments like `app-DEV.properties` or `app-PRD.properties` and you want to check if they correlate. It will check if:

- sizes (number of keys) are equal,
- name of the keys are equal and
- all values are present.

## Usage

Add the following lines within `build > plugins` in your `pom.xml`.

```xml
<plugin>
  <groupId>de.codecentric</groupId>
  <artifactId>check-staging-properties-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <phase>verify</phase>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <directory>src/main/resources</directory>
    <breakBuild>true</breakBuild>
    <groups>
      <group>credentials-(.*)\.properties</group>
      <group>settings-(.*)\.properties</group>
    </groups>
  </configuration>
</plugin>
```

**Note:** The `configuration` section is optional. By default the plugin will search for properties in the `src/main/resources` directory and will break the build if the properties are not equal. You can optionally pass a list of `groups` to group the checking of properties by filename.
