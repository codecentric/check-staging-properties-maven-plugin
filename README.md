# check-staging-properties-maven-plugin
<a href="https://www.apache.org/licenses/LICENSE-2.0.txt"><img src="https://camo.githubusercontent.com/5897a599003da6eaeea8955598349abe548600ae/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6c6963656e73652d415346322d626c75652e737667" alt="Apache License 2" data-canonical-src="https://img.shields.io/badge/license-ASF2-blue.svg" style="max-width:100%;"></a>
<a href="https://travis-ci.org/codecentric/spring-boot-admin"><img src="https://camo.githubusercontent.com/79d4e0f1b30a87b9c5d4397a325f76bce1bc00c2/68747470733a2f2f7472617669732d63692e6f72672f636f646563656e747269632f737072696e672d626f6f742d61646d696e2e7376673f6272616e63683d6d6173746572" alt="Build Status" data-canonical-src="https://travis-ci.org/codecentric/spring-boot-admin.svg?branch=master" style="max-width:100%;"></a>
<a href="https://www.codacy.com/app/britter/check-staging-properties-maven-plugin"><img src="https://camo.githubusercontent.com/4a417d5e767212793ec8592cbfe0c86558b2d23a/68747470733a2f2f6170692e636f646163792e636f6d2f70726f6a6563742f62616467652f67726164652f3866643762616336656461633431376138343531333837323836666536393137" alt="Codacy Badge" data-canonical-src="https://api.codacy.com/project/badge/grade/8fd7bac6edac417a8451387286fe6917" style="max-width:100%;"></a>
<a href="https://coveralls.io/github/codecentric/check-staging-properties-maven-plugin"><img alt="Badge" class="notice-badge" src="https://coveralls.io/repos/github/codecentric/check-staging-properties-maven-plugin/badge.svg?branch=master"></a>

Maven-plugin for checking the equality of several `.properties`-files for Mule applications (at least mostly used with, but not necessary). This plugin is perfect for you, if you have multiple `.properties`-files for different staging environments like `app-DEV.properties` or `app-PRD.properties` and you want to check if they correlate. It will check if:

- sizes (number of keys) are equal,
- name of the keys are equal and
- values are empty.

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
    <groups>
      <group>credentials-(.*)\.properties</group>
      <group>settings-(.*)\.properties</group>
    </groups>
  </configuration>
</plugin>
```

**Note:** The `configuration` section is optional. By default the plugin will search for properties in the `src/main/resources` directory and will break the build if the properties are not equal. You can optionally pass a list of `groups` to group the checking of properties by filename.
