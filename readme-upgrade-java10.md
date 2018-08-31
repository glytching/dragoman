Upgrading to Java 10 can take three forms:

1. Run the application on Java 10 - no changes necessary
1. Compile the application with Java 10. This makes Java 10 features avaialble to the application but it requires some changes:
  * Tell Maven to build with Java 10 and to target Java 10 output
  * Upgrade some internal dependencies to Java 10 compatible versions
1. Modularize the application to use Java 10's module system


Prerequisites
----

### Install Java 10 JDK

The following instructions all relate to MacOs.

```
$ brew update
$ brew tap caskroom/cask
$ brew tap caskroom/versions
$ brew cask info java
# this is valid as long as the current version of java is 10, if not you may have to specify java10
$ brew cask install java
```

Check the installations:

```
$ ls /Library/Java/JavaVirtualMachines/
jdk-10.0.2.jdk/  jdk1.8.0_51.jdk/
```

For help with running multiple versions of Java on MacOs:

* Install `jenv`: `brew install jenv`
* Add the following lines to ~/.bash_profile to initialize `jenv`:
    ```
    # Init jenv
    if which jenv > /dev/null; then eval "$(jenv init -)"; fi
    ```
* Tell `jenv` about the installed JDKs:
    ```
    jenv add /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/
    jenv add /Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home/
    ```
* Check that the JDKs have been registered: `jenv versions`, the output will be something like the following (the asterix marks the active version):
    ```
    $ jenv versions
      system
      1.8
      1.8.0.51
      10.0
      10.0.2
    * oracle64-1.8.0.51
      oracle64-10.0.2
    ```
* Set the default JDK to 1.8: `jenv global oracle64-1.8.0.51`
* Set the project specific JDK to 10: `cd dragoman; jenv global oracle64-10.0.2`

### IDE

If using Intellij, upgrade to 2018.1+ and reconfigure the `dragoman` project to use Java 10:

* `Module Settings` > `Platform Settings` > ``SDKs` > Add a new SDK pointing at `/Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home/`
* `Module Settings` > `Project Settings` > `Project SDK`: 10
* `Module Settings` > `Project Settings` > `Project Language Level`: 10

Compile the Application With Java 10
----

### Changes to `dragoman`

* Change `maven.compiler.source` to `1.10`
* Change `maven.compiler.target` to `1.10`
* Upgrade Maven Compiler Plugin
    ```
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.0</version>
        <configuration>
            <release>10</release>
        </configuration>
    </plugin>
    ```
* Add a dependency on `org.ow2.asm:asm:6.1.1` to the `maven-surefire-plugin` declaration:
    ```
    <dependency>
        <groupId>org.ow2.asm</groupId>
        <artifactId>asm</artifactId>
        <version>6.1.1</version>
    </dependency>
    ```
* Upgrade Mockito for compatability with Java 10: change `mockito.version` to `2.21.0`
* Upgrade Jacoco for compatability with Java 10: change `maven.jacoco.plugin.version` to `0.8.1`
* Tell Travis CI to build with Java 10, in `.travis.yml`:
    ```
    jdk:
      - oraclejdk10
    ```

Modularize the Application
----

### Changes to `dragoman`

> TODO complete this!

