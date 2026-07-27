# domino-slf4j-logger

`domino-slf4j-logger` is an SLF4J implementation for browser-side Java applications built with GWT or J2CL. It keeps the familiar SLF4J API, but adds browser-friendly routing, scoped MDC helpers, and console adapters that work well in web apps.

## What it gives you

- Standard `Logger`, `Marker`, and `MDC` entry points
- Marker-based routing to different adapters
- Browser console output out of the box
- Scoped MDC helpers that work with try-with-resources
- A default SLF4J provider you can extend if you need custom factories

## Requirements

- Java 11+
- SLF4J 2.x
- A GWT or J2CL browser runtime

## Install

Add the dependency:

```xml
<dependency>
  <groupId>org.dominokit</groupId>
  <artifactId>domino-slf4j-logger</artifactId>
  <version>${domino.slf4j.version}</version>
</dependency>
```

Then inherit the GWT module in your app module:

```xml
<inherits name="org.dominokit.domino.logger.Logging"/>
```

That is usually enough to start using SLF4J in the client.

## Quick start

Basic logging looks the same as regular SLF4J:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hello {
  private static final Logger log = LoggerFactory.getLogger(Hello.class);

  public void run() {
    log.info("Hello from the browser");
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

By default, output goes to the browser console through the built-in console adapter.

## How it works

The logging flow is simple:

1. `LoggerFactory.getLogger(...)` returns a `DominoLoggingAdapter`.
2. The logger formats SLF4J placeholders and enriches messages with marker and MDC context.
3. `LoggingRouter` chooses the adapter for marker-aware calls.
4. The selected `LoggingAdapter` writes to the final destination, such as the browser console or a remote endpoint.

## Routing by marker

Marker routing lets you send different log streams to different adapters. The router works with marker names, not marker instances, so you usually register routes once during startup.

```java
import org.dominokit.domino.logger.ConsoleLoggingAdapter;
import org.dominokit.domino.logger.LoggingRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PaymentFlow {
  private static final Logger log = LoggerFactory.getLogger(PaymentFlow.class);
  private static final Marker PAYMENT = MarkerFactory.getMarker("PAYMENT");

  public void initLogging() {
    LoggingRouter.setDefaultAdapter(new ConsoleLoggingAdapter());
    LoggingRouter.register("PAYMENT", new ConsoleLoggingAdapter());
  }

  public void charge(String orderId) {
    log.info(PAYMENT, "Charging order {}", orderId);
  }
}
```

Some details matter:

- A direct marker mapping wins first.
- If no direct mapping exists, the router walks referenced markers recursively.
- If nothing matches, the default adapter is used, if one is configured.
- `LoggingRouter.clear()` removes all marker mappings, and `unregister(...)` removes one mapping.

If you want a hierarchy, add references through SLF4J markers:

```java
Marker payment = MarkerFactory.getMarker("PAYMENT");
Marker online = MarkerFactory.getMarker("ONLINE");
payment.add(online);

LoggingRouter.register("ONLINE", new ConsoleLoggingAdapter());
log.info(payment, "Hierarchical marker example");
```

If `PAYMENT` is not registered, the router will continue through the referenced marker names until it finds a match.

## Custom adapters

Implement `LoggingAdapter` when you want to send logs somewhere other than the browser console.

```java
import java.util.Map;
import org.dominokit.domino.logger.LoggingAdapter;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public class RemoteAdapter implements LoggingAdapter {
  @Override
  public void log(
      Level level,
      String loggerName,
      Marker marker,
      String message,
      Throwable throwable,
      Map<String, String> mdc) {
    // Serialize the event and send it to your backend here.
  }
}
```

Register it like any other adapter:

```java
LoggingRouter.register("AUDIT", new RemoteAdapter());
LoggingRouter.setDefaultAdapter(new RemoteAdapter());
```

If you need to fan out to more than one destination, use `CompositeLoggingAdapter`:

```java
import org.dominokit.domino.logger.CompositeLoggingAdapter;
import org.dominokit.domino.logger.ConsoleLoggingAdapter;
import org.dominokit.domino.logger.LoggingRouter;

LoggingRouter.setDefaultAdapter(
    new CompositeLoggingAdapter(new ConsoleLoggingAdapter(), new RemoteAdapter()));
```

`CompositeLoggingAdapter` broadcasts to every delegate. Because it stores delegates in a set, the call order is not guaranteed. If order matters, write a small custom adapter instead.

## MDC utilities

The MDC helpers are scoped and work well with browser-style, single-threaded code. Use them for request IDs, user IDs, correlation IDs, and similar context.

Single value:

```java
import org.dominokit.domino.logger.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFlow {
  private static final Logger log = LoggerFactory.getLogger(UserFlow.class);

  public void execute(String userId) throws Exception {
    try (AutoCloseable scope = MDCUtils.withMdc("userId", userId)) {
      log.info("Starting user flow");
      nested();
    }
  }

  private void nested() {
    log.info("Nested step");
  }
}
```

Multiple values:

```java
import java.util.LinkedHashMap;
import java.util.Map;
import org.dominokit.domino.logger.MDCUtils;

Map<String, String> ctx = new LinkedHashMap<>();
ctx.put("flow", "onboarding");
ctx.put("step", "emailVerification");

try (AutoCloseable scope = MDCUtils.withMdc(ctx)) {
  log.info("Verifying email");
}
```

Snapshot access:

```java
import java.util.Deque;
import org.dominokit.domino.logger.MDCUtils;

Deque<String> userIdStack = MDCUtils.getMdcStack("userId");
```

Notes:

- Values are pushed for the lifetime of the scope and popped automatically on close.
- Multi-value scopes are popped in reverse insertion order.
- `getMdcStack(...)` returns a defensive copy, so reading it does not mutate live MDC state.
- The implementation is designed for the single-threaded browser environment.

## Built-in adapters

### ConsoleLoggingAdapter

This adapter writes to the browser console and maps levels to matching console methods:

- `TRACE` and `DEBUG` to `console.debug`
- `INFO` to `console.info`
- `WARN` to `console.warn`
- `ERROR` to `console.error`

Use it for quick development output or as a default adapter.

### CompositeLoggingAdapter

This adapter forwards each event to multiple adapters. It is useful when you want console output and a remote sink at the same time.

## Logging controls

The module defines a few GWT properties with defaults:

```xml
<define-property name="domino.slf4j.logging.level" values="INFO,WARN,DEBUG,TRACE,ERROR,OFF"/>
<define-property name="domino.slf4j.logging.showCaller" values="true,false"/>
<define-property name="domino.slf4j.logging.showMdc" values="true,false"/>
```

Default values are:

- `domino.slf4j.logging.level=INFO`
- `domino.slf4j.logging.showCaller=false`
- `domino.slf4j.logging.showMdc=false`

To override them, set the properties in your app module before compilation:

```xml
<module>
  <inherits name="org.dominokit.domino.logger.Logging"/>
  <set-property name="domino.slf4j.logging.level" value="DEBUG"/>
  <set-property name="domino.slf4j.logging.showCaller" value="true"/>
  <set-property name="domino.slf4j.logging.showMdc" value="true"/>
</module>
```

`showCaller` prepends source location information like `[MyClass:42]`. `showMdc` prepends the current MDC context when one is available.

## Advanced topics

### Service provider wiring

The library ships a default SLF4J service provider for GWT/J2CL environments. In normal use, you just inherit the GWT module and start logging. You do not need to wire `META-INF/services` by hand.

If you need custom behavior, extend `DefaultDominoLoggingServiceProvider` and install your provider early in application startup, before the first `LoggerFactory`, `MarkerFactory`, or `MDC` access.

### Marker factories

The bundled marker factory caches markers by name, so repeated `MarkerFactory.getMarker("PAYMENT")` calls return the same marker instance. Use `getDetachedMarker(...)` when you need a standalone marker that is not cached.

### Startup order

Routing is global state. Register adapters during bootstrap, before the first log event, if you want marker-specific routing to apply consistently.

If you reconfigure logging in tests or hot-reload flows, clear router state and MDC state explicitly so one run does not leak into the next.

### Marker hierarchies

The router searches the marker itself first, then walks referenced markers recursively, and finally falls back to the default adapter. That makes marker hierarchies useful for broad categories:

- `payment.add(online)` lets `PAYMENT` events reuse the `ONLINE` route if `PAYMENT` has no direct adapter
- a direct mapping still overrides any referenced markers

## Troubleshooting

- No output: confirm the GWT module inherits `org.dominokit.domino.logger.Logging` and that your level property is not set to `OFF`.
- Wrong adapter: verify the marker name matches the registered route and that the route is installed before the first log call.
- Missing MDC data: make sure the log call happens inside the scope returned by `MDCUtils.withMdc(...)`.

## Best practices

- Keep the marker set small and stable, for example `SECURITY`, `AUDIT`, and `PAYMENT`.
- Use scoped MDC for correlation IDs and similar request context.
- Use `CompositeLoggingAdapter` when you want the same event in both the console and a backend.
- Avoid logging sensitive data to the browser console.
- Reset router and MDC state in tests when you change them.
