![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)

# domino-slf4j-logger
Enable using slf4j loggers inside GWT client side code, which increase code portability to the JVM where the slf4j logging is preferred.

> Still needs some work to make sure the logs can be optimized by gwt compiler.

### Maven dependencies

```
<dependency>
    <groupId>org.dominokit</groupId>
    <artifactId>domino-slf4j-logger</artifactId>
    <version>1.0.0</version>
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

Use the following system property to controle the log level

`domino.slf4j.logging.level` possible values are `OFF`, `INFO`, `WARN`, `DEBUG`, `ERROR`, `TRACE`  default is `INFO`.