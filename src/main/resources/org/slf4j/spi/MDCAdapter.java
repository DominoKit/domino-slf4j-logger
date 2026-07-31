package org.slf4j.spi;

import java.util.Deque;
import java.util.Map;

/**
 * Minimal MDCAdapter for GWT/J2CL super-sourcing with stack operations.
 */
public interface MDCAdapter {
  /**
   * Put a context value as identified by the key parameter into the current thread's context map.
   *
   * @param key the key
   * @param val the value
   */
  void put(String key, String val);

  /**
   * Get the context value identified by the key parameter.
   *
   * @param key the key
   * @return the value
   */
  String get(String key);

  /**
   * Remove the context value identified by the key parameter.
   *
   * @param key the key
   */
  void remove(String key);

  /** Clear all entries in the MDC. */
  void clear();

  /**
   * Return a copy of the current thread's context map.
   *
   * @return a copy of the context map
   */
  Map<String, String> getCopyOfContextMap();

  /**
   * Set the current thread's context map by first clearing any existing map and then copying the
   * map passed as parameter.
   *
   * @param contextMap the new context map
   */
  void setContextMap(Map<String, String> contextMap);

  /**
   * Push a value on the stack identified by the key.
   *
   * @param key the key
   * @param value the value
   * @since 2.0.0
   */
  void pushByKey(String key, String value);

  /**
   * Pop the value on the stack identified by the key.
   *
   * @param key the key
   * @return the popped value
   * @since 2.0.0
   */
  String popByKey(String key);

  /**
   * Returns a copy of the deque(stack) at the specified key.
   *
   * @param key the key
   * @return a copy of the stack
   * @since 2.0.0
   */
  Deque<String> getCopyOfDequeByKey(String key);

  /**
   * Clear the stack identified by the key.
   *
   * @param key the key
   * @since 2.0.0
   */
  void clearDequeByKey(String key);
}
