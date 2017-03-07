# JetGatling: gatling load testing make easy

[![Build Status](https://travis-ci.org/scw1109/jet-gatling.svg?branch=master)](https://travis-ci.org/scw1109/jet-gatling)
[![Release Status](https://jitpack.io/v/scw1109/jet-gatling.svg)](https://jitpack.io/#scw1109/jet-gatling)
[![Dependency Status](https://www.versioneye.com/user/projects/58bce50c01b5b7003d620a56/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58bce50c01b5b7003d620a56)

[![Quality gate](https://sonarqube.com/api/badges/gate?key=com.github.scw1109:jet-gatling)](https://sonarqube.com/dashboard/?id=com.github.scw1109%3Ajet-gatling)
[![Open issues](https://sonarqube.com/api/badges/measure?key=com.github.scw1109:jet-gatling&metric=open_issues)](https://sonarqube.com/component_issues?id=com.github.scw1109%3Ajet-gatling)
[![Lines of code](https://sonarqube.com/api/badges/measure?key=com.github.scw1109:jet-gatling&metric=ncloc)](https://sonarqube.com/component_measures/metric/ncloc/list?id=com.github.scw1109%3Ajet-gatling)
[![Coverage](https://sonarqube.com/api/badges/measure?key=com.github.scw1109:jet-gatling&metric=coverage)](https://sonarqube.com/component_measures/metric/coverage/list?id=com.github.scw1109%3Ajet-gatling)
[![Class complexity](https://sonarqube.com/api/badges/measure?key=com.github.scw1109:jet-gatling&metric=class_complexity)](https://sonarqube.com/component_measures/metric/class_complexity/list?id=com.github.scw1109%3Ajet-gatling)

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/license/LICENSE-2.0.txt)

-----
 
## Overview

JetGatling provides a scenario DSL and package with [Gatling](http://gatling.io/), 
make the use of [Gatling](http://gatling.io/) without the trouble of writing the DSL. 

### Background

[Gatling](http://gatling.io/) is a great load testing tool. 
However it requires one to write the scenario in DSL to start a load testing.

This is not as convenient as using simple benchmarking tool like 
[ab](https://httpd.apache.org/docs/2.4/programs/ab.html) or 
[wrk](https://httpd.apache.org/docs/2.4/programs/ab.html).

Hence, JetGatling provides a scenario DSL which covers simple common use cases, 
make the usage of [Gatling](http://gatling.io/) as easy as  
[ab](https://httpd.apache.org/docs/2.4/programs/ab.html).

## Using JetGatling

### Docker image

This is the recommended approach of using JetGatling, 
visit [here](https://hub.docker.com/r/scw1109/jet-gatling/) for more information.

### Download Jar file 

Download the the jar file from this [link](https://jitpack.io/com/github/scw1109/jet-gatling/0.1/jet-gatling-0.1.jar)

```
https://jitpack.io/com/github/scw1109/jet-gatling/0.1/jet-gatling-0.1.jar
```

### Usage

 * Requires JDK 1.8 or higher

#### Run a simple load test (Fixed RPS mode)

```
java -jar jet-gatling.jar -r 5 -d 30 -u "https://google.com"
```

This will start a test that running **5 RPS** (requests per second) with a **30 seconds** duration against ```google.com```

Note that no matter how long the response take, the tool will send 5 requests in each second.

#### Run a simple load test (Fixed concurrent mode)

```
java -jar jet-gatling.jar -c 5 -d 30 -u "https://google.com"
```

This will start a test that running **5 concurrent clients** with a **30 seconds** duration against ```google.com```

Note that each client will send next request as soon as it receive a response.
Which acts more like [ab](https://httpd.apache.org/docs/2.4/programs/ab.html)

#### Show usage

For other parameters, use the following command to check the details.

```
java -jar jet-gatling.jar -h
```

## Developer Guide

See [developer.md](developer.md)

## License

The license is Apache 2.0, see [LICENSE-2.0.txt](LICENSE-2.0.txt)
