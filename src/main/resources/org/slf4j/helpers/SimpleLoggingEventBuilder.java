/*
 * Minimal LoggingEventBuilder implementation delegating to the underlying Logger
 * for GWT/J2CL environments.
 */
package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

/**
 * Minimal LoggingEventBuilder implementation delegating to the underlying Logger for GWT/J2CL
 * environments.
 */
public class SimpleLoggingEventBuilder implements LoggingEventBuilder {

  private final Logger logger;
  private final Level level;
  private Throwable cause;
  private Marker marker;

  /**
   * Creates a new SimpleLoggingEventBuilder.
   *
   * @param logger the logger to delegate to
   * @param level the log level
   */
  public SimpleLoggingEventBuilder(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  @Override
  public LoggingEventBuilder setCause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  @Override
  public LoggingEventBuilder addMarker(Marker marker) {
    this.marker = marker;
    return this;
  }

  @Override
  public LoggingEventBuilder addArgument(Object p) {
    // arguments are passed via log(format, args)
    return this;
  }

  @Override
  public LoggingEventBuilder addKeyValue(String key, Object value) {
    // key-values are ignored in this minimal implementation
    return this;
  }

  @Override
  public void log(String message) {
    if (cause != null) {
      log(message, new Object[]{});
    } else {
      log(message, new Object[]{});
    }
  }

  @Override
  public void log(String format, Object arg) {
    if (marker != null) {
      logWithMarker(format, new Object[] {arg});
    } else {
      switch (level) {
        case TRACE:
          if (cause != null) logger.trace(format, arg, cause); else logger.trace(format, arg);
          break;
        case DEBUG:
          if (cause != null) logger.debug(format, arg, cause); else logger.debug(format, arg);
          break;
        case INFO:
          if (cause != null) logger.info(format, arg, cause); else logger.info(format, arg);
          break;
        case WARN:
          if (cause != null) logger.warn(format, arg, cause); else logger.warn(format, arg);
          break;
        case ERROR:
          if (cause != null) logger.error(format, arg, cause); else logger.error(format, arg);
          break;
      }
    }
  }

  @Override
  public void log(String format, Object arg1, Object arg2) {
    if (marker != null) {
      logWithMarker(format, new Object[] {arg1, arg2});
    } else {
      switch (level) {
        case TRACE:
          if (cause != null) logger.trace(format, arg1, arg2, cause); else logger.trace(format, arg1, arg2);
          break;
        case DEBUG:
          if (cause != null) logger.debug(format, arg1, arg2, cause); else logger.debug(format, arg1, arg2);
          break;
        case INFO:
          if (cause != null) logger.info(format, arg1, arg2, cause); else logger.info(format, arg1, arg2);
          break;
        case WARN:
          if (cause != null) logger.warn(format, arg1, arg2, cause); else logger.warn(format, arg1, arg2);
          break;
        case ERROR:
          if (cause != null) logger.error(format, arg1, arg2, cause); else logger.error(format, arg1, arg2);
          break;
      }
    }
  }

  @Override
  public void log(String format, Object... args) {
    if (marker != null) {
      logWithMarker(format, args);
      return;
    }
    switch (level) {
      case TRACE:
        if (cause != null) logger.trace(format, appendCause(args)); else logger.trace(format, args);
        break;
      case DEBUG:
        if (cause != null) logger.debug(format, appendCause(args)); else logger.debug(format, args);
        break;
      case INFO:
        if (cause != null) logger.info(format, appendCause(args)); else logger.info(format, args);
        break;
      case WARN:
        if (cause != null) logger.warn(format, appendCause(args)); else logger.warn(format, args);
        break;
      case ERROR:
        if (cause != null) logger.error(format, appendCause(args)); else logger.error(format, args);
        break;
    }
  }

  @Override
  public void log() {
    // nothing to log without a message in this minimal impl
  }

  private void logWithMarker(String format, Object[] args) {
    switch (level) {
      case TRACE:
        if (cause != null) logger.trace(marker, format, appendCause(args)); else logger.trace(marker, format, args);
        break;
      case DEBUG:
        if (cause != null) logger.debug(marker, format, appendCause(args)); else logger.debug(marker, format, args);
        break;
      case INFO:
        if (cause != null) logger.info(marker, format, appendCause(args)); else logger.info(marker, format, args);
        break;
      case WARN:
        if (cause != null) logger.warn(marker, format, appendCause(args)); else logger.warn(marker, format, args);
        break;
      case ERROR:
        if (cause != null) logger.error(marker, format, appendCause(args)); else logger.error(marker, format, args);
        break;
    }
  }

  private Object[] appendCause(Object[] args) {
    if (cause == null) return args;
    Object[] with = new Object[args.length + 1];
    for (int i = 0; i < args.length; i++) with[i] = args[i];
    with[args.length] = cause;
    return with;
  }
}
