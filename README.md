# domino-slf4j-logger

A lightweight SLF4J implementation tailored for browser-based Java (GWT/JS) applications, with first-class support for:
- Marker-based routing to pluggable adapters
- Scoped MDC helpers designed for single-threaded UI runtimes
- Console-oriented adapters suitable for web apps

This library lets you keep using the familiar SLF4J API while routing log events to different destinations (e.g., browser console, remote endpoint) based on markers, and manage contextual information using MDC scopes.

---

## Highlights

- SLF4J-compatible API surface for browser/GWT environments
- Marker-based routing: send different logs to different adapters
- Pluggable adapters: console, tree-style console, or your own
- Scoped MDC utilities with try-with-resources
- Zero-dependency runtime for typical usage; simple to wire in

---

## Installation

- Java: 8+
- Intended for GWT/JS environments (e.g., DominoKit)

Maven (example):
```xml
<dependency>
  <groupId>org.dominokit</groupId>
  <artifactId>domino-slf4j-logger</artifactId>
  <version>YOUR_VERSION_HERE</version>
</dependency>
```


GWT module:
- In your app module `.gwt.xml`, inherit the logging module (example):
```xml
<inherits name="org.dominokit.domino.logger.Logging"/>
```


That’s typically all you need to get the SLF4J API wired for GWT.

---

## Quick start

Basic logging with SLF4J:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hello {
  private static final Logger log = LoggerFactory.getLogger(Hello.class);

  public void run() {
    log.info("Hello from the browser!");
    log.warn("Be careful: {}", "something happened");
    try {
      risky();
    } catch (Exception e) {
      log.error("Operation failed", e);
    }
  }

  private void risky() {
    throw new RuntimeException("boom");
  }
}
```


By default, logs go to the configured adapter(s). See “Routing by marker” for custom routing.

---

## Routing by marker

You can route logs dynamically based on SLF4J markers. For example, direct payment-related logs to one adapter, audit logs to another, and everything else to a default adapter.

- Register adapters by marker name
- Optionally set a default adapter

Example:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.dominokit.domino.logger.LoggingRouter;
import org.dominokit.domino.logger.LoggingAdapter;
import org.dominokit.domino.logger.TestLoggingAdapter; // Example built-in

public class PaymentFlow {
  private static final Logger log = LoggerFactory.getLogger(PaymentFlow.class);
  private static final Marker PAYMENT = MarkerFactory.getMarker("PAYMENT");

  public void initLogging() {
    // Default adapter for logs with no specific marker mapping
    LoggingRouter.setDefaultAdapter(new ConsoleLoggingAdapter());

    // Route PAYMENT marker logs (and marker hierarchies) to a specialized adapter
    LoggingRouter.register("PAYMENT", new ConsoleLoggingAdapter());
  }

  public void charge() {
    log.info(PAYMENT, "Charging customer {}", "123");
  }
}
```


Marker hierarchy is respected: if a marker references another marker that has an adapter mapping, that mapping will be used.

---

## Writing a custom adapter

Adapters receive already-formatted messages along with the level, logger name, marker (if any), throwable (if any), and an MDC snapshot. Implement your own to forward logs to remote endpoints, alternate consoles, buffers, or in-memory stores.

```java
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.dominokit.domino.logger.LoggingAdapter;

public class RemoteAdapter implements LoggingAdapter {
  @Override
  public void log(Level level,
                  String loggerName,
                  Marker marker,
                  String message,
                  Throwable throwable,
                  Map<String, String> mdc) {
    // Example: send JSON to a server endpoint
    // Your transport code here...
  }
}
```


Then install it:
```java
LoggingRouter.register("AUDIT", new RemoteAdapter());
// Optionally set default:
LoggingRouter.setDefaultAdapter(new RemoteAdapter());
```


Unregister, clear, or replace mappings at runtime as needed.

---

## MDC utilities (scoped)

Manage contextual data (like request IDs, user IDs, correlation IDs) using scoped helpers designed for single-threaded browser runtimes.

- Push values for the scope duration; automatically pop on close
- Push multiple entries at once
- Access current stack snapshot of a specific key

Scoped single entry:
```java
import org.dominokit.domino.logger.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFlow {
  private static final Logger log = LoggerFactory.getLogger(UserFlow.class);

  public void execute(String userId) {
    try (AutoCloseable scope = MDCUtils.withMdc("userId", userId)) {
      log.info("Starting user flow");
      nested();
    } catch (Exception ignore) {}
  }

  private void nested() {
    log.info("Nested step");
  }
}
```


Scoped multiple entries:
```java
import java.util.LinkedHashMap;
import java.util.Map;
import org.dominokit.domino.logger.MDCUtils;

Map<String, String> ctx = new LinkedHashMap<>();
ctx.put("flow", "onboarding");
ctx.put("step", "emailVerification");

try (AutoCloseable scope = MDCUtils.withMdc(ctx)) {
  // All logs here include both flow and step
  log.info("Verifying email");
} catch (Exception ignore) {}
```


Get a snapshot of a single MDC key’s stack:
```java
import java.util.Deque;
import org.dominokit.domino.logger.MDCUtils;

Deque<String> userIdStack = MDCUtils.getMdcStack("userId");
// Copy for inspection without mutating the actual MDC
```


Notes:
- Designed for single-threaded UI runtimes (like GWT in the browser).
- Values are pushed in order and popped in reverse order for multi-entry scopes.

---

## Built-in console adapters

- ConsoleLoggingAdapter
    - Straightforward delegation to the browser console with level mapping

- ConsoleTreeLoggingAdapter
    - Similar to console, but may present messages in a structured “tree” style

Use cases:
- Development diagnostics in the browser
- Quick validation of marker-based routing
- Baseline default adapter

Example:
```java
LoggingRouter.setDefaultAdapter(new ConsoleLoggingAdapter());
LoggingRouter.register("DEBUG_TREE", new ConsoleTreeLoggingAdapter());
```


---

## Advanced topics

- Service/provider wiring
    - The library provides the SLF4J service provider and logger factory for a GWT/JS environment. In typical usage, you don’t need to configure SLF4J service discovery manually.

- Startup order
    - If you rely on specific routing, register your adapters early in application startup, before emitting log events.

- Marker hierarchies
    - When no direct adapter mapping exists for a marker, referenced markers are traversed to find the first mapped adapter. If none is found, the default adapter (if any) is used.

---

## Best practices

- Define a small set of well-known markers (e.g., SECURITY, AUDIT, PAYMENT) and route them to purpose-built adapters.
- Prefer scoped MDC with try-with-resources for predictable lifetimes.
- In production, consider a custom adapter that batches and ships logs to your backend.
- Be mindful of PII when logging to browser consoles or remote endpoints.

---

## Troubleshooting

- Logs not appearing
    - Ensure your GWT module inherits the logging module
    - Verify a default adapter is set if no marker mapping applies
    - Confirm your adapter implementation isn’t swallowing errors

- Marker routing not applied
    - Check that the marker name used in code matches the name you registered
    - If using marker references, ensure the referenced markers are correctly wired

- MDC not visible
    - Make sure your adapter uses the MDC snapshot provided to it (if you need to transmit MDC values)

---

## Compatibility

- Java 8
- Intended for GWT/JS environments (including frameworks like DominoKit)
- Uses the SLF4J API familiar to JVM developers, adapted for browser-based execution

---

## License

This project is licensed under the terms of the included LICENSE file.

---

## Contributing

Issues and pull requests are welcome. Please include:
- Clear description of the problem or feature
- Repro steps or sample
- Any environment details that may affect logging behavior

---

## API reference (at a glance)

- SLF4J surface:
    - Logger, LoggerFactory, Marker, MarkerFactory, event.Level, MDC

- Routing:
    - LoggingRouter: setDefaultAdapter, register, unregister, clear

- Adapter SPI:
    - LoggingAdapter: log(level, loggerName, marker, message, throwable, mdc)

- MDC helpers:
    - MDCUtils: withMdc(key, value), withMdc(map), getMdcStack(key)