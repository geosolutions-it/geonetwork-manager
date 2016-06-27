**geonetwork-manager** is a java library to interact with GeoNetwork Opensource. 
Its aim is to provide a simple API to create, delete, administer the catalog entries programmatically.

## Features

The supported operations are:

* insert metadata
* search metadata
* retrieve metadata
* update metadata
* delete metadata
* change permissions

## Build

clone the repo

```
#~$ git clone git@github.com:geosolutions-it/geonetwork-manager.git
```

then from the root of the repo run

```
#~/geonetwork-manager$ mvn clean install
```

## Usage

Follow the wiki instructions to [add geonetwork-manager as a dependency](https://github.com/geosolutions-it/geonetwork-manager/wiki#working-with-maven) in your project and see some [code usage examples](https://github.com/geosolutions-it/geonetwork-manager/wiki/Examples).


## Online tests

Long story short, read [How to run the online test suite](#how-to-run-the-online-test-suite)

In order to run the geonetwork-manager **online test suite** a running geonetwork test instance is required. Don't use a production instance with a real metadata catalog otherwise the metadata stored will be lost.

By default, only unit tests are executed with the build process. The online test suite uses the [geotools Test Data module](http://docs.geotools.org/latest/developer/conventions/test/data.html).

The test framework looks for a **fixture** (.properties) file in the **home folder of the user** in order to gather all the conection parameters required to connect to the test instance.

* If the **fixture** file **is found**, the online tests suite is executed. If the connection parameters reported are wrong all the tests will fail.
* If the **fixture** file **is NOT found**, the test framework will create a template fixture file with a fake extension.
* If only the **template fixture** file **is found**, the test suite is skipped.

#### Where exactly the template fixture file is created?

As said above, the location of the fixture file is under the home folder of the OS user in the (hidden) directory ``.geotools/geonetwork`` and the name of the file is ``params.properties``.

During the first run of the tests the subdirectories and a template file are automatically created in order to simplify the process.

If the OS is **win7** and user is called **geosolutions** the generated directories/file will be

```
C:\Users\geosolutions\.geotools\geonetwork\params.properties.example
```

On **ubuntu**

```
/home/geosolutions/.geotools/geonetwork/params.properties.example
```

#### How to run the online test suite

1. Startup, remotely or locally, a geonetwork `2.10` or `3.x` test instance
1. Run the test for the first time via maven or eclipse
1. Open the template fixture file generated and fill the `URL`, `version`, `username`, and `password` fields with the information of the test instance
1. Rename the fixture file removing the ``.example`` suffix
1. Run tests again, this time the online tests will be executed and reported as junit tests.

The template fixture file content:

```
#This is an example fixture. Update the values and remove the .example suffix to enable the test
#Mon Jun 27 14:27:21 CEST 2016
URL=http\://localhost\:8080/geonetwork
version=type_2_or_3
password=admin
username=admin
```