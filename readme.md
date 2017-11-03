Dragoman
======

> an interpreter (of sorts)

Provides a facade which allows the user to ...

* Query datasets, applying projections and predicates to filter and shape the datasets to meet the user's needs
* Subscribe for updates, listen to changes in datasets

The datasets are, broadly speaking, of two types:

* Local: these are stored within Dragoman
* Remote: these are read, on demand, from a remote source over HTTP 

Although this app is functional it is primarily a sandbox, a place for trying out technologies. Bearing that caveat in mind, feel free to dig deeper ...
 
* [What Does It Do?](docs/what-does-it-do.md)
* [How Does It Work?](docs/how-does-it-work.md)
* [Contributing To This Project](docs/contributing.md)
* [Code Of Conduct For This Project](docs/code-of-conduct.md)
* [License](license.md)

Building Dragoman
-------

To build Dragoman:

```
$ git clone https://github.com/glytching/dragoman.git
$ cd dragoman
$ mvn clean package
```

This will compile and run all automated tests and create the application distributable. 

Running Dragoman
-------

The application distributable is an uber JAR, it contains the application's class files and resources alongside the application's dependencies. When run in embedded mode the application will start an in-process instance of MongoDB. When run in non embedded mode the application will expect to find a Mongo instance at the address defined in the [application.properties](src/main/resources/application.properties) (specifically the `mongo.host` and `mongo.port` properties).

To run the uber JAR:

##### In Embedded Mode, Logging To Console

```
$ java -Denv=embedded -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```

##### In Embedded Mode, Logging To File

```
$ java -Denv=embedded -Dlog.dir=logs -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```

##### In Non Embedded Mode, Logging To Console

```
$ java -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```
 
License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.