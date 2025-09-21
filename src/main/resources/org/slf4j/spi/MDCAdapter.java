/*
 * Minimal MDCAdapter for GWT/J2CL super-sourcing with stack operations.
 */
package org.slf4j.spi;

import java.util.Deque;
import java.util.Map;

public interface MDCAdapter {
  void put(String key, String val);
  String get(String key);
  void remove(String key);
  void clear();
  Map<String, String> getCopyOfContextMap();
  void setContextMap(Map<String, String> contextMap);

  // Stack-based helpers for scoped MDC
  void pushByKey(String key, String value);
  String popByKey(String key);
  Deque<String> getCopyOfDequeByKey(String key);
  void clearDequeByKey(String key);
}
