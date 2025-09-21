/*
 * Copyright © 2019 Dominokit
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import org.slf4j.MDC;

/**
 * Utility helpers for scoped MDC operations using withMdc naming.
 *
 * <p>These helpers assume a single-threaded environment and delegate to the bound MDCAdapter's
 * pushByKey/popByKey/getCopyOfDequeByKey methods.
 */
public final class MDCUtils {

  private MDCUtils() {}

  /**
   * Begin a scoped MDC entry for the given key/value. The value is pushed on the stack and
   * automatically popped when the returned scope is closed.
   */
  public static AutoCloseable withMdc(final String key, final String value) {
    final org.slf4j.spi.MDCAdapter adapter = MDC.getMDCAdapter();
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
   * Begin a scoped MDC section for multiple key/value pairs. Keys are popped in reverse order on
   * close.
   */
  public static AutoCloseable withMdc(final Map<String, String> entries) {
    if (entries == null || entries.isEmpty()) {
      return () -> {};
    }
    final org.slf4j.spi.MDCAdapter adapter = MDC.getMDCAdapter();
    final List<String> order = new ArrayList<String>(entries.size());
    for (Map.Entry<String, String> e : entries.entrySet()) {
      adapter.pushByKey(e.getKey(), e.getValue());
      order.add(e.getKey());
    }
    return new AutoCloseable() {
      private boolean closed = false;

      @Override
      public void close() {
        if (closed) return;
        closed = true;
        for (int i = order.size() - 1; i >= 0; i--) {
          adapter.popByKey(order.get(i));
        }
      }
    };
  }

  /** Get a snapshot copy of the MDC stack for the given key or null if empty. */
  public static Deque<String> getMdcStack(String key) {
    Deque<String> d = MDC.getMDCAdapter().getCopyOfDequeByKey(key);
    if (d == null) return null;
    return new ArrayDeque<String>(d);
  }
}
