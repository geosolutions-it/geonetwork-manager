
## GeoNetwork Manager

**geonetwork-manager** is a Java library to interact with [GeoNetwork Opensource](http://geonetwork-opensource.org/). 
Its aim is to provide a simple Java interface to create, delete, administer the catalog entries programmatically.

Since version 1.4 (currently on the master branch) **geonetwork-manager** is also compatible with GeoNetwork v3.0.x.
Compatibility with GN2.6 and GN2.8 has been restored as well.

The currently **supported operations** are:

* insert metadata
* search metadata
* retrieve metadata
* update metadata
* delete metadata
* change metadata permissions

## Compatibility

The latest version (1.4) supports GN2.6, GN2.8, GN2.10 and GN3.

Version 1.3 moved compatibility from the older GN2.6 and GN2.8 to GN2.10, which introduced a new authorization
mechanism and some differences in the services URLs.
If you need to talk to a GN2.10, you'd better use the newer 1.4 version.

Version 1.2 supported GN2.6 and GN2.8.


## Using the library

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


## Doc and examples

Follow the wiki instructions to [add geonetwork-manager as a dependency](https://github.com/geosolutions-it/geonetwork-manager/wiki#working-with-maven)
in your project and see some [code usage examples](https://github.com/geosolutions-it/geonetwork-manager/wiki/Examples).


## Mailing list for users 
The mailing list for the project is located here:

<https://groups.google.com/group/geonetwork-manager-users>


## License

GeoNetwork-Manager is open source and licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
