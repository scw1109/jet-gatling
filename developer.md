# Developer notes

## Prerequisites
  * Java version = 1.8
    * Gatling is not working well for jdk9/jdk10 due to scala 2.12 supports only jdk 1.8
  * Maven version >= 3.0.4

## Build
### Locally

Make sure you have all the prerequisites installed.
To build locally, simply run 

```
mvn package
```

### CI

This project use [Travis CI](https://travis-ci.org/) as the CI build system. 
Visit [here](https://travis-ci.org/scw1109/jet-gatling) to see the build status.
 
#### Setup notes

Travis CI is sync with the personal Github repositories, 
which allow one to turn on/off each Github repo on Travis CI account management page.

## Release

[JitPack](https://jitpack.io/) is used to release and publish the artifacts.
Visit [here](https://jitpack.io/#scw1109/jet-gatling) for more details.

Although JitPack is used as a release tool here, 
it includes the responsibility of building the final publish artifact.
It is important to note that the artifact built on Travis CI is not used as the published artifact.

### Setup notes

Once sync JitPack with Github account, 
you may see the Github repositories and start a build on JitPack management page.

The JitPack project page should have a "How to" guide which is very simple, just adding few lines to the ```pom.xml```
 file.
 
### Release steps

 1. Before start, make sure the build status of CI is success.
 1. To create a new release, follow the [instructions](https://github.com/blog/1547-release-your-software) to create a Github release.
    * JitPack will use Github "tag name" as the "version" of the artifact, 
    and automatically changes the version xml tag in ```pom.xml```. 
    Hence, it should follow the naming convention like ```0.3```.
    * For Github "release title", convention is something like ```v0.3```.     
 1. JitPack will then build a new release and published to JitPack repository.
 1. Although the version in ```pom.xml``` does not directly impact the release step,
 to make sure its easy to know what is the developing version, 
 use the ```bump_version.sh``` (or manually) to increase the version to indicate the next release version.
  