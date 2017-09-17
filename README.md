# Open Desert

This project is my OpenGL and LWJGL 3 sandbox. The current aim is to learn how OpenGL works, while making a usable 2D engine. A big emphasis is placed on modularity and clean code.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

In order to build the project from source, you need the following software installed:

* Java (OpenJDK) >= 1.8
* Apache Maven >= 3.1.0

### Building

To build the project, just `package` it:

```
mvn clean package
```

## Running the unit tests

To run the unit tests, just invoke the `test` Maven goal:

```
mvn test
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [LWJGL 3](https://www.lwjgl.org/) - OpenGL and other bindings
* [JUnit](https://junit.org/) - Unit tests
* [TWL PNGDecoder](http://twl.l33tlabs.org) - PNG file loading  

## Contributing

Please read the [Contributor Guidelines](CONTRIBUTING.md) for details on the process of contributing code and submitting pull requests.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/pilif0/open-desert/tags). 

## Authors

* **Filip Smola** - *Initial work* - [Filip Smola](https://smola.me)

See also the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.