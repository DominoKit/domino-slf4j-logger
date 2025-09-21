/*
 * Minimal SLF4J 2.x-compatible LoggingEventBuilder for GWT/J2CL super-sourcing.
 */
package org.slf4j.spi;

import org.slf4j.Marker;

public interface LoggingEventBuilder {

  LoggingEventBuilder setCause(Throwable cause);

  LoggingEventBuilder addMarker(Marker marker);

  LoggingEventBuilder addArgument(Object p);

  LoggingEventBuilder addKeyValue(String key, Object value);

  void log(String message);

  void log(String format, Object arg);

  void log(String format, Object arg1, Object arg2);

  void log(String format, Object... args);

  void log();
}
