**geonetwork-manager** is a Java library to interact with [GeoNetwork Opensource](http://geonetwork-opensource.org/). 
Its aim is to provide a simple Java interface to create, delete, administer the catalog entries programmatically.

Since version 1.4 (currently on the master branch) **geonetwork-manager** is also compatible with GeoNetwork v3.0.x.

The currently **supported operations** are:

* insert metadata
* search metadata
* retrieve metadata
* update metadata
* delete metadata
* change metadata permissions

## Use the library

In order to use the **geonetwork-manager** library you can add it as a dependency in your [maven](https://maven.apache.org/) project:

        <dependency>
            <groupId>it.geosolutions</groupId>
            <artifactId>geonetwork-manager</artifactId>
            <version>1.4-SNAPSHOT</version>
        </dependency>

using the [GeoSolutions](http://www.geo-solutions.it/) maven repository:

        <repository>
            <id>geosolutions</id>
            <name>GeoSolutions Repository</name>
            <url>http://maven.geo-solutions.it</url>
        </repository>

If you need, you can download the `.jar` files for any available version from [here](http://maven.geo-solutions.it/it/geosolutions/geonetwork-manager/).

## Build the library

Clone the geonetwork-manager repo

```
~$ git clone git@github.com:geosolutions-it/geonetwork-manager.git
```

then from the root dir of the cloned repository, run

```
~/geonetwork-manager$ mvn clean install
```

## Usage

Follow the wiki instructions to [add geonetwork-manager as a dependency](https://github.com/geosolutions-it/geonetwork-manager/wiki#working-with-maven)
in your project and see some [code usage examples](https://github.com/geosolutions-it/geonetwork-manager/wiki/Examples).


## Online tests

In order to run the geonetwork-manager **online test suite**, a running geonetwork test instance is required.
The tests are destructive, so **DO NOT** use a production instance with a real metadata catalog otherwise the stored metadata will be lost.

By default, only unit tests are executed in the build process. 
The online test suite uses the [geotools Test Data module](http://docs.geotools.org/latest/developer/conventions/test/data.html).

The test framework looks for a **fixture** (.properties) file in the **home directory of the user** in order to gather all the connection
parameters required to connect to the GeoNetowrk test instance.

* If the **fixture** file **is found**, the online tests suite is executed. If the connection parameters reported are wrong, all the tests will fail.
* If the **fixture** file **is NOT found**, the test framework will create a template fixture file with a fake ``.sample`` extension.
* If only the **template fixture** file **is found**, the test suite is skipped.

#### Where can I find the sample fixture file?

As said above, the location of the fixture file is under the home folder of the OS user in the (hidden) directory
``.geotools/geonetwork-manager`` and the name of the file is ``params.properties``.

During the first run of the tests the subdirectories and a template file are automatically created in order to simplify the process.

If the OS is **win7** and user is called **geosolutions** the generated directories/file will be

```
C:\Users\geosolutions\.geotools\geonetwork-manager\params.properties.example
```

On **Linux**

```
/home/geosolutions/.geotools/geonetwork-manager/params.properties.example
```

#### How to run the online test suite

1. Startup, remotely or locally, a GeoNetwork test instance, either `2.6`, `2.8`, `2.10` or `3.0.x`
1. Run the test for the first time via maven or eclipse
1. Edit the template fixture file generated and change the `url`, `version`, `username`, and `password` fields with the information related to the test instance
1. `version` should be either `26`, `28`, `210` or `3`
1. Rename the fixture file removing the ``.example`` suffix
1. Run the tests again; this time the online tests will be executed and reported as junit tests.

The template fixture file content will look like this:

```
#This is an example fixture. Update the values and remove the .example suffix to enable the test
#Mon Jun 27 14:27:21 CEST 2016
url=http\://localhost\:8080/geonetwork
version=26|28|210|3
password=admin
username=admin
```
