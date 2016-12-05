# check-staging-properties-maven-plugin

Maven-plugin for checking the equality of several `.properties`-files for Mule applications (at least mostly used with, but not necessary). This plugin is perfect for you, if you have multiple `.properties`-files for different staging environments like `app-DEV.properties` or `app-PRD.properties` and you want to check if they correlate. It will check if:

- sizes (number of keys) are equal,
- name of the keys are equal and
- values are empty.

## Installation

Add the dependency within `dependencies` in your `pom.xml`.

```xml
<dependency>
  <groupId>de.codecentric</groupId>
  <artifactId>check-staging-properties-maven-plugin</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

Add the following lines within `build > plugins` in your `pom.xml`.

```xml
<plugin>
  <groupId>de.codecentric</groupId>
  <artifactId>check-staging-properties-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <phase>validate</phase>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <directory>src/main/resources</directory>
    <breakBuild>true</breakBuild>
  </configuration>
</plugin>
```
__Note:__
The `configuration` section is optional. By default the plugin will search for properties in the `src/main/resources` directory and will break the build if the properties are not equal.