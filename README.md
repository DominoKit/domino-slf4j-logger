<a title="Gitter" href="https://gitter.im/DominoKit/domino"><img src="https://badges.gitter.im/Join%20Chat.svg"></a>
[![Development Build Status](https://github.com/DominoKit/domino-slf4j-logger/actions/workflows/deploy.yaml/badge.svg?branch=development)](https://github.com/DominoKit/domino-slf4j-logger/actions/workflows/deploy.yaml/badge.svg?branch=development)
![Maven Central](https://img.shields.io/badge/Release-1.0.2-green)
![Sonatype Nexus (Snapshots)](https://img.shields.io/badge/Snapshot-HEAD--SNAPSHOT-orange)
![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)

# domino-slf4j-logger
Enable using slf4j loggers inside GWT client side code, which increase code portability to the JVM where the slf4j logging is preferred.

### Maven dependencies

#### Release

```
<dependency>
    <groupId>org.dominokit</groupId>
    <artifactId>domino-slf4j-logger</artifactId>
    <version>1.0.2</version>
</dependency>
```

#### Snapshot

```
<dependency>
    <groupId>org.dominokit</groupId>
    <artifactId>domino-slf4j-logger</artifactId>
    <version>HEAD-SNAPSHOT</version>
</dependency>
```

### GWT2 Inherits

```
<inherits name="org.dominokit.domino.logger.Logging"/>
```

### Usage


```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

...

private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

...

LOGGER.info("I cann use SLF4J logger in gwt client side code.");
```

### Logging level

Use the following system property to control the log level

`domino.slf4j.logging.level` possible values are `OFF`, `INFO`, `WARN`, `DEBUG`, `ERROR`, `TRACE`  default is `INFO`.