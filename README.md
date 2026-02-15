<div align="center">
<h1 align="center">Yacy Evolved</h1>

An improved version of [Yacy](https://github.com/yacy/yacy_search_server) created by webman168. Original developed by Michael Peter Christen.
</div>

## Installation
Yacy Nebula is written in Java and can be compiled using a Java 11 or 17 JDK and apache ant.

Pre-compiled YaCy packages may be available at a later date.

You need Java 11 or later to run Yacy Nebula.

### Compile and run YaCy from git sources
This will install the requirements on debian:

```
sudo apt-get install openjdk-11-jdk-headless ant
```

Then clone the repository and build the application:

```
git clone --depth 1 https://github.com/webman168/yacy-nebula/
cd yacy_search_server
ant clean all
```
