/*
 * Copyright © ${year} Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.domino.logger;

import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * Domino SLF4J Logger that enriches messages with Marker and MDC data, and routes marker-aware
 * calls via LoggingRouter. All console output is delegated via ConsoleLoggingAdapter.
 */
public class DominoLoggingAdapter implements Logger {

  private static final LoggingAdapter CONSOLE_ADAPTER = new ConsoleLoggingAdapter();

  private final String name;

  private static final boolean TRACE_ENABLED;
  private static final boolean INFO_ENABLED;
  private static final boolean WARNING_ENABLED;
  private static final boolean ERROR_ENABLED;
  private static final boolean DEBUG_ENABLED;
  private static final boolean SHOW_CALLER =
      Boolean.parseBoolean(System.getProperty("domino.slf4j.logging.showCaller", "false"));
  private static final boolean SHOW_MDC =
      Boolean.parseBoolean(System.getProperty("domino.slf4j.logging.showMdc", "false"));

  static {
    String level = System.getProperty("domino.slf4j.logging.level", "INFO");
    if (level != "INFO"
        && level != "WARN"
        && level != "DEBUG"
        && level != "TRACE"
        && level != "ERROR"
        && level != "OFF") {
      throw new AssertionError("Undefined value for domino.slf4j.logging.level: '" + level + "'");
    }

    TRACE_ENABLED = level == "TRACE";
    INFO_ENABLED = level == "TRACE" || level == "INFO";
    DEBUG_ENABLED = level == "TRACE" || level == "INFO" || level == "DEBUG";
    WARNING_ENABLED = level == "TRACE" || level == "INFO" || level == "DEBUG" || level == "WARN";
    ERROR_ENABLED =
        level == "TRACE"
            || level == "INFO"
            || level == "DEBUG"
            || level == "WARN"
            || level == "ERROR";
  }

  public DominoLoggingAdapter(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isTraceEnabled() {
    return isEnabledForLevel(Level.TRACE);
  }

  /**
   * We are using debug instead of trace here because trace would also print a stack trace from
   * where it was called. check
   * https://developer.mozilla.org/en-US/docs/Web/API/Console#Stack_traces
   */
  @Override
  public void trace(String msg) {
    if (isEnabledForLevel(Level.TRACE)) {
      CONSOLE_ADAPTER.log(
          Level.TRACE, this.name, null, withCaller(msg), null, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public void trace(String format, Object arg) {
    if (isEnabledForLevel(Level.TRACE)) {
      formatAndLog(Level.TRACE, format, arg);
    }
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    if (isEnabledForLevel(Level.TRACE)) {
      formatAndLog(Level.TRACE, format, arg1, arg2);
    }
  }

  @Override
  public void trace(String format, Object... argArray) {
    if (isEnabledForLevel(Level.TRACE)) {
      formatAndLog(Level.TRACE, format, argArray);
    }
  }

  @Override
  public void trace(String msg, Throwable t) {
    if (isEnabledForLevel(Level.TRACE)) {
      CONSOLE_ADAPTER.log(
          Level.TRACE, this.name, null, withCaller(msg), t, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return isEnabledForLevel(Level.DEBUG);
  }

  @Override
  public void debug(String msg) {
    if (isEnabledForLevel(Level.DEBUG)) {
      CONSOLE_ADAPTER.log(
          Level.DEBUG, this.name, null, withCaller(msg), null, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public void debug(String format, Object arg) {
    if (isEnabledForLevel(Level.DEBUG)) {
      formatAndLog(Level.DEBUG, format, arg);
    }
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    if (isEnabledForLevel(Level.DEBUG)) {
      formatAndLog(Level.DEBUG, format, arg1, arg2);
    }
  }

  @Override
  public void debug(String format, Object... argArray) {
    if (isEnabledForLevel(Level.DEBUG)) {
      formatAndLog(Level.DEBUG, format, argArray);
    }
  }

  @Override
  public void debug(String msg, Throwable t) {
    if (isEnabledForLevel(Level.DEBUG)) {
      CONSOLE_ADAPTER.log(
          Level.DEBUG, this.name, null, withCaller(msg), t, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return isEnabledForLevel(Level.INFO);
  }

  @Override
  public void info(String msg) {
    if (isEnabledForLevel(Level.INFO)) {
      CONSOLE_ADAPTER.log(
          Level.INFO, this.name, null, withCaller(msg), null, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public void info(String format, Object arg) {
    if (isEnabledForLevel(Level.INFO)) {
      formatAndLog(Level.INFO, format, arg);
    }
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    if (isEnabledForLevel(Level.INFO)) {
      formatAndLog(Level.INFO, format, arg1, arg2);
    }
  }

  @Override
  public void info(String format, Object... argArray) {
    if (isEnabledForLevel(Level.INFO)) {
      formatAndLog(Level.INFO, format, argArray);
    }
  }

  @Override
  public void info(String msg, Throwable t) {
    if (isEnabledForLevel(Level.INFO)) {
      CONSOLE_ADAPTER.log(
          Level.INFO, this.name, null, withCaller(msg), t, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return isEnabledForLevel(Level.WARN);
  }

  @Override
  public void warn(String msg) {
    if (isEnabledForLevel(Level.WARN)) {
      CONSOLE_ADAPTER.log(
          Level.WARN, this.name, null, withCaller(msg), null, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public void warn(String format, Object arg) {
    if (isEnabledForLevel(Level.WARN)) {
      formatAndLog(Level.WARN, format, arg);
    }
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    if (isEnabledForLevel(Level.WARN)) {
      formatAndLog(Level.WARN, format, arg1, arg2);
    }
  }

  @Override
  public void warn(String format, Object... argArray) {
    if (isEnabledForLevel(Level.WARN)) {
      formatAndLog(Level.WARN, format, argArray);
    }
  }

  @Override
  public void warn(String msg, Throwable t) {
    if (isEnabledForLevel(Level.WARN)) {
      CONSOLE_ADAPTER.log(
          Level.WARN, this.name, null, withCaller(msg), t, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return isEnabledForLevel(Level.ERROR);
  }

  public boolean isEnabledForLevel(Level level) {
    switch (level) {
      case TRACE:
        return TRACE_ENABLED;
      case DEBUG:
        return DEBUG_ENABLED;
      case INFO:
        return INFO_ENABLED;
      case WARN:
        return WARNING_ENABLED;
      case ERROR:
        return ERROR_ENABLED;
      default:
        return false;
    }
  }

  @Override
  public void error(String msg) {
    if (isEnabledForLevel(Level.ERROR)) {
      CONSOLE_ADAPTER.log(
          Level.ERROR, this.name, null, withCaller(msg), null, MDC.getCopyOfContextMap());
    }
  }

  @Override
  public void error(String format, Object arg) {
    if (isEnabledForLevel(Level.ERROR)) {
      formatAndLog(Level.ERROR, format, arg);
    }
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    if (isEnabledForLevel(Level.ERROR)) {
      formatAndLog(Level.ERROR, format, arg1, arg2);
    }
  }

  @Override
  public void error(String format, Object... argArray) {
    if (isEnabledForLevel(Level.ERROR)) {
      formatAndLog(Level.ERROR, format, argArray);
    }
  }

  @Override
  public void error(String msg, Throwable t) {
    if (ERROR_ENABLED) {
      CONSOLE_ADAPTER.log(
          Level.ERROR, this.name, null, withCaller(msg), t, MDC.getCopyOfContextMap());
    }
  }

  // Marker-aware routing: resolve adapter per marker, fallback to default behavior if none
  @Override
  public boolean isTraceEnabled(Marker marker) {
    return isTraceEnabled();
  }

  @Override
  public void trace(Marker marker, String msg) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.TRACE, this.name, marker, out, null, MDC.getCopyOfContextMap());
    } else {
      trace(enriched);
    }
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    trace(marker, format, new Object[] {arg});
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    trace(marker, format, new Object[] {arg1, arg2});
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enrichedFormat = enrichWithMarkerAndMdc(marker, format);
    if (adapter != null) {
      FormattingTuple ft = MessageFormatter.arrayFormat(enrichedFormat, argArray);
      String out = ft.getMessage();
      if (SHOW_CALLER) {
        out = callerPrefix() + out;
      }
      adapter.log(
          Level.TRACE, this.name, marker, out, ft.getThrowable(), MDC.getCopyOfContextMap());
    } else {
      trace(enrichedFormat, argArray);
    }
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.TRACE, this.name, marker, out, t, MDC.getCopyOfContextMap());
    } else {
      trace(enriched, t);
    }
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isDebugEnabled();
  }

  @Override
  public void debug(Marker marker, String msg) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.DEBUG, this.name, marker, out, null, MDC.getCopyOfContextMap());
    } else {
      debug(enriched);
    }
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    debug(marker, format, new Object[] {arg});
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    debug(marker, format, new Object[] {arg1, arg2});
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enrichedFormat = enrichWithMarkerAndMdc(marker, format);
    if (adapter != null) {
      FormattingTuple ft = MessageFormatter.arrayFormat(enrichedFormat, arguments);
      String out = ft.getMessage();
      if (SHOW_CALLER) {
        out = callerPrefix() + out;
      }
      adapter.log(
          Level.DEBUG, this.name, marker, out, ft.getThrowable(), MDC.getCopyOfContextMap());
    } else {
      debug(enrichedFormat, arguments);
    }
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.DEBUG, this.name, marker, out, t, MDC.getCopyOfContextMap());
    } else {
      debug(enriched, t);
    }
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isInfoEnabled();
  }

  @Override
  public void info(Marker marker, String msg) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.INFO, this.name, marker, out, null, MDC.getCopyOfContextMap());
    } else {
      info(enriched);
    }
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    info(marker, format, new Object[] {arg});
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    info(marker, format, new Object[] {arg1, arg2});
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enrichedFormat = enrichWithMarkerAndMdc(marker, format);
    if (adapter != null) {
      FormattingTuple ft = MessageFormatter.arrayFormat(enrichedFormat, arguments);
      String out = ft.getMessage();
      if (SHOW_CALLER) {
        out = callerPrefix() + out;
      }
      adapter.log(Level.INFO, this.name, marker, out, ft.getThrowable(), MDC.getCopyOfContextMap());
    } else {
      info(enrichedFormat, arguments);
    }
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.INFO, this.name, marker, out, t, MDC.getCopyOfContextMap());
    } else {
      info(enriched, t);
    }
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isWarnEnabled();
  }

  @Override
  public void warn(Marker marker, String msg) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.WARN, this.name, marker, out, null, MDC.getCopyOfContextMap());
    } else {
      warn(enriched);
    }
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    warn(marker, format, new Object[] {arg});
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    warn(marker, format, new Object[] {arg1, arg2});
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enrichedFormat = enrichWithMarkerAndMdc(marker, format);
    if (adapter != null) {
      FormattingTuple ft = MessageFormatter.arrayFormat(enrichedFormat, arguments);
      String out = ft.getMessage();
      if (SHOW_CALLER) {
        out = callerPrefix() + out;
      }
      adapter.log(Level.WARN, this.name, marker, out, ft.getThrowable(), MDC.getCopyOfContextMap());
    } else {
      warn(enrichedFormat, arguments);
    }
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.WARN, this.name, marker, out, t, MDC.getCopyOfContextMap());
    } else {
      warn(enriched, t);
    }
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isErrorEnabled();
  }

  @Override
  public void error(Marker marker, String msg) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.ERROR, this.name, marker, out, null, MDC.getCopyOfContextMap());
    } else {
      error(enriched);
    }
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    error(marker, format, new Object[] {arg});
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    error(marker, format, new Object[] {arg1, arg2});
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enrichedFormat = enrichWithMarkerAndMdc(marker, format);
    if (adapter != null) {
      FormattingTuple ft = MessageFormatter.arrayFormat(enrichedFormat, arguments);
      String out = ft.getMessage();
      if (SHOW_CALLER) {
        out = callerPrefix() + out;
      }
      adapter.log(
          Level.ERROR, this.name, marker, out, ft.getThrowable(), MDC.getCopyOfContextMap());
    } else {
      error(enrichedFormat, arguments);
    }
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    LoggingAdapter adapter = LoggingRouter.resolve(marker);
    String enriched = enrichWithMarkerAndMdc(marker, msg);
    if (adapter != null) {
      String out = withCaller(enriched);
      adapter.log(Level.ERROR, this.name, marker, out, t, MDC.getCopyOfContextMap());
    } else {
      error(enriched, t);
    }
  }

  /**
   * Begin a scoped MDC entry for the given key/value. The value is pushed on the MDC stack for the
   * key and automatically popped when the returned scope is closed.
   *
   * <p>Usage: try (AutoCloseable scope = logger.withMdc("requestId", reqId)) { // logging with
   * requestId }
   */
  public AutoCloseable withMdc(final String key, final String value) {
    final org.slf4j.spi.MDCAdapter adapter = org.slf4j.MDC.getMDCAdapter();
    adapter.pushByKey(key, value);
    return new AutoCloseable() {
      private boolean closed = false;

      @Override
      public void close() {
        if (closed) return;
        closed = true;
        adapter.popByKey(key);
      }
    };
  }

  /**
   * Begin a scoped MDC section for multiple key/value pairs. Each key is pushed and will be popped
   * on close. Keys are popped in reverse insertion order.
   */
  public AutoCloseable withMdc(final java.util.Map<String, String> entries) {
    if (entries == null || entries.isEmpty()) {
      return () -> {};
    }
    final org.slf4j.spi.MDCAdapter adapter = org.slf4j.MDC.getMDCAdapter();
    final java.util.List<String> order = new java.util.ArrayList<String>(entries.size());
    for (java.util.Map.Entry<String, String> e : entries.entrySet()) {
      adapter.pushByKey(e.getKey(), e.getValue());
      order.add(e.getKey());
    }
    return new AutoCloseable() {
      private boolean closed = false;

      @Override
      public void close() {
        if (closed) return;
        closed = true;
        // pop in reverse order
        for (int i = order.size() - 1; i >= 0; i--) {
          adapter.popByKey(order.get(i));
        }
      }
    };
  }

  /** Retrieve a snapshot copy of the MDC stack for the given key, or null if empty. */
  public java.util.Deque<String> getMdcStack(String key) {
    return org.slf4j.MDC.getMDCAdapter().getCopyOfDequeByKey(key);
  }

  /**
   * Builds a prefix that includes marker hierarchy and MDC context, e.g. "[marker=PAYMENT->ONLINE]
   * [mdc=requestId=123, user=alice] " or "" if neither is present.
   */
  private String decoratePrefix(Marker marker) {
    StringBuilder sb = new StringBuilder();

    String markerStr = formatMarker(marker);
    if (!markerStr.isEmpty()) {
      sb.append("[marker=").append(markerStr).append("] ");
    }

    String mdcStr = formatMdc();
    if (!mdcStr.isEmpty()) {
      sb.append("[mdc=").append(mdcStr).append("] ");
    }

    return sb.toString();
  }

  private String formatMarker(Marker marker) {
    if (marker == null) return "";
    // Include simple hierarchy chain "PARENT->CHILD->...".
    StringBuilder sb = new StringBuilder(marker.getName());
    // Show only direct references chain if present
    if (marker.hasReferences()) {
      sb.append("->");
      Iterator<Marker> it = marker.iterator();
      boolean first = true;
      while (it.hasNext()) {
        Marker m = it.next();
        if (!first) sb.append("->");
        sb.append(m.getName());
        first = false;
      }
    }
    return sb.toString();
  }

  private String formatMdc() {
    Map<String, String> ctx = MDC.getCopyOfContextMap();
    if (ctx == null || ctx.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> e : ctx.entrySet()) {
      if (!first) sb.append(", ");
      sb.append(e.getKey()).append("=").append(String.valueOf(e.getValue()));
      first = false;
    }
    return sb.toString();
  }

  private String enrichWithMarkerAndMdc(Marker marker, String msg) {
    String prefix = decoratePrefix(marker);
    if (prefix.isEmpty()) return msg;
    return prefix + msg;
  }

  private String withCaller(String msg) {
    String result = msg;

    // Prepend MDC prefix if enabled and not already present
    if (SHOW_MDC) {
      String mdcPrefix = decoratePrefix(null); // includes only [mdc=...] when marker is null
      if (!mdcPrefix.isEmpty() && result.indexOf("[mdc=") < 0) {
        result = mdcPrefix + result;
      }
    }

    // Prepend caller info last so it appears first
    if (SHOW_CALLER) {
      result = callerPrefix() + result;
    }
    return result;
  }

  private String callerPrefix() {
    try {
      StackTraceElement[] st = new Throwable().getStackTrace();
      for (StackTraceElement e : st) {
        String cn = e.getFileName();
        if (isInternalFrame(cn)) continue;
        String simple = cn;
        int idx = cn.lastIndexOf('.');
        if (idx >= 0 && idx + 1 < cn.length()) simple = cn.substring(0, idx);
        int line = e.getLineNumber();
        String loc = (line >= 0) ? (simple + ":" + line) : simple;
        return "[" + loc + "] ";
      }
    } catch (Throwable ignore) {
      // ignore and fall through
    }
    return "";
  }

  private boolean isInternalFrame(String className) {
    if (className == null) return true;
    if (className.startsWith("DominoLoggingAdapter")) return true;
    if (className.startsWith("Throwable")) return true;
    if (className.startsWith("org.slf4j")) return true;
    if (className.startsWith("java.lang")) return true;
    if (className.startsWith("sun.reflect")) return true;
    if (className.startsWith("jdk.internal")) return true;
    if (className.startsWith("com.google.gwt")) return true;
    if (className.startsWith("elemental2.")) return true;
    return false;
  }

  private void formatAndLog(Level level, String format, Object... argArray) {
    FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
    String message = withCaller(ft.getMessage());
    CONSOLE_ADAPTER.log(
        level, this.name, null, message, ft.getThrowable(), MDC.getCopyOfContextMap());
  }
}
