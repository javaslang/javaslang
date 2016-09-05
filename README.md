[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.javaslang/javaslang/badge.png)](https://maven-badges.herokuapp.com/maven-central/io.javaslang/javaslang)
[![Build Status](https://travis-ci.org/javaslang/javaslang.png)](https://travis-ci.org/javaslang/javaslang)
[![Coverage Status](https://codecov.io/github/javaslang/javaslang/coverage.png?branch=master)](https://codecov.io/github/javaslang/javaslang?branch=master)
[![Gitter Chat](https://badges.gitter.im/Join%20Chat.png)](https://gitter.im/javaslang/javaslang)

# [Javaslang](http://javaslang.io/)

[![Join the chat at https://gitter.im/javaslang/javaslang](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javaslang/javaslang?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Feature-rich & self-contained functional programming in Java&trade; 8 and above.
Javaslang is a functional library for Java 8+ that provides persistent data types and functional control structures. Because Javaslang does not depend on any libraries (other than the JVM) you can easily add it as standalone .jar to your classpath.

To stay up to date please follow the [blog](http://blog.javaslang.io).

## Using Javaslang

See [User Guide](http://docs.javaslang.io)

### Useful Maven Goals

* Executing tests: `mvn clean test`
* Executing doclint: `mvn javadoc:javadoc`
* Executing code coverage report: `mvn -P ci clean test jacoco:report`
* Create -javadoc.jar: `mvn javadoc:jar`
* Create -source.jar: `mvn source:jar`
* Update version properties: `mvn versions:update-properties`
* Check for new plugin version: `mvn versions:display-plugin-updates`

### Benchmarks

Currently only basic microbenchmarks are available. To run

```
mvn clean test -Pbenchmark
```
