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

Dragoman is on Travis, current branch build statuses are:

| Branch  | Status |
| --------| ------ |
| Master  | [![Master Build Status](https://travis-ci.org/glytching/dragoman.svg?branch=master)](https://travis-ci.org/glytching/dragoman)  |


To build Dragoman locally:

```
$ git clone https://github.com/glytching/dragoman.git
$ cd dragoman
$ mvn clean package
```

This will compile and run all automated tests and create the application distributable. 

Running Dragoman
-------
 
#### Runtime Parameters

The following JVM parameters will change aspects of Dragoman's behaviour:

* `env` controls the set of properties to be chosen from [application.properties](src/main/resources/application.properties). The default environment runs against a local MongoDB server and is accessible on a well known HTTP port. The alternative environment is named `embedded`, in this mode the application spins up an embedded MongoDB instance nd makes itself available on a randomly selected port from the free port range. In this mode the application is entirely self contained, it makes no assumptions about the host it is running on.
* `log.dir` controls how logs are written. When this parameter is supplied all log output is written to file and these files are stored in the address supplied by this parameter. When this parameter is not supplied all log output is written to console.

#### Running In-IDE

Just run the `org.glitch.dragoman.Dragoman` class, it has a main method.

#### Running As A Standalone Process

The application distributable is an uber JAR, it contains the application's class files and resources alongside the application's dependencies. When run in embedded mode the application will start an in-process instance of MongoDB. When run in non embedded mode the application will expect to find a Mongo instance at the address defined in the [application.properties](src/main/resources/application.properties) (specifically the `mongo.host` and `mongo.port` properties).

To run the uber JAR:

###### In Embedded Mode, Logging To Console

```
$ java -Denv=embedded -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```

###### In Embedded Mode, Logging To File

```
$ java -Denv=embedded -Dlog.dir=logs -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```

###### In Non Embedded Mode, Logging To Console

```
$ java -jar target/dragoman-1.0-SNAPSHOT-uber.jar
```

#### Using the Webapp

When the server is running the web application will be available at:

> `http://<host>:<port>/dragoman/about.hbs`

Where:

* `<host>` is the host on whihc the server is running
* `<port>` is defined by the `http.port` value in [application.properties](src/main/resources/application.properties). By default, this is `31000` but if the server is running in embedded mode then this port will be assigned from the free port range. To see what port has been assigned look for a log message like this:

> o.g.dragoman.web.WebServerVerticle|Starting HTTP server on port: ... 

###### Browser Compatability

The web app has been tested and verified for the following browsers:

* Safari 9.x
* Firefox 51.0
* Chrome 61.x

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