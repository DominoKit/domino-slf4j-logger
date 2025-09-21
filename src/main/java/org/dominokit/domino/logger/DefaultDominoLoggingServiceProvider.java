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
/*
 * Public default SLF4J service provider for GWT/J2CL environments.
 *
 * Users can extend this class and override specific factory creation methods
 * to customize only parts of the logging stack while reusing the default
 * MDC and Marker implementations provided here.
 */
package org.dominokit.domino.logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * Default SLF4J service provider for GWT/J2CL environments.
 *
 * <p>Provides default factories for Logger, Marker, and MDC. Subclasses can override factory
 * creation methods to customize behavior.
 */
public class DefaultDominoLoggingServiceProvider implements SLF4JServiceProvider {

  protected final ILoggerFactory loggerFactory;
  protected final IMarkerFactory markerFactory;
  protected final MDCAdapter mdcAdapter;

  /** Creates a service provider with default logger, marker, and MDC factories. */
  public DefaultDominoLoggingServiceProvider() {
    this.loggerFactory = createLoggerFactory();
    this.markerFactory = createMarkerFactory();
    this.mdcAdapter = createMDCAdapter();
  }

  /**
   * Factory method for creating the ILoggerFactory. Subclasses may override to provide a different
   * factory.
   */
  protected ILoggerFactory createLoggerFactory() {
    return new ILoggerFactory() {
      @Override
      public Logger getLogger(String name) {
        return new DominoLoggingAdapter(name);
      }
    };
  }

  /**
   * Factory method for creating the IMarkerFactory. Subclasses may override to provide a different
   * marker factory.
   */
  protected IMarkerFactory createMarkerFactory() {
    return new SimpleMarkerFactory();
  }

  /**
   * Factory method for creating the MDCAdapter. Subclasses may override to provide a different MDC
   * adapter.
   */
  protected MDCAdapter createMDCAdapter() {
    return new SimpleMDCAdapter();
  }

  @Override
  /**
   * Returns the {@link ILoggerFactory} provided by this service.
   *
   * @return the logger factory
   */
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  /**
   * Returns the {@link IMarkerFactory} provided by this service.
   *
   * @return the marker factory
   */
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  @Override
  /**
   * Returns the {@link MDCAdapter} provided by this service.
   *
   * @return the MDC adapter
   */
  public MDCAdapter getMDCAdapter() {
    return mdcAdapter;
  }

  @Override
  /**
   * Returns the SLF4J API version requested by this provider.
   *
   * @return the requested API version string
   */
  public String getRequestedApiVersion() {
    return "2.0.99"; // any 2.x compatible
  }

  @Override
  /** Initializes the provider. No-op by default. */
  public void initialize() {
    // nothing by default
  }

  // ----------------- Default MDC implementation -----------------
  /**
   * Simple, single-threaded {@link MDCAdapter} implementation suitable for GWT/J2CL.
   *
   * <p>Maintains flat key/value map plus optional LIFO stacks per key to support SLF4J 2.x
   * push/pop.
   */
  public static class SimpleMDCAdapter implements MDCAdapter {
    // GWT/J2CL compatible: single-threaded map
    private static final Map<String, String> CONTEXT_MAP = new HashMap<>();
    // Optional per-key stacks to support SLF4J 2.x push/pop semantics
    private static final Map<String, java.util.Deque<String>> CONTEXT_STACKS = new HashMap<>();

    @Override
    public void put(String key, String val) {
      if (key == null) return;
      if (val == null) {
        CONTEXT_MAP.remove(key);
      } else {
        CONTEXT_MAP.put(key, val);
      }
    }

    @Override
    public String get(String key) {
      return CONTEXT_MAP.get(key);
    }

    @Override
    public void remove(String key) {
      CONTEXT_MAP.remove(key);
      // also drop any maintained stack for this key
      CONTEXT_STACKS.remove(key);
    }

    @Override
    public void clear() {
      CONTEXT_MAP.clear();
      CONTEXT_STACKS.clear();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
      return CONTEXT_MAP.isEmpty() ? null : new HashMap<>(CONTEXT_MAP);
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
      CONTEXT_MAP.clear();
      CONTEXT_STACKS.clear();
      if (contextMap != null) {
        CONTEXT_MAP.putAll(contextMap);
      }
    }

    @Override
    public void pushByKey(String key, String value) {
      if (key == null || value == null) {
        return;
      }
      java.util.Deque<String> deque = CONTEXT_STACKS.get(key);
      if (deque == null) {
        deque = new java.util.ArrayDeque<String>();
        CONTEXT_STACKS.put(key, deque);
      }
      deque.push(value);
      // reflect the top value into the flat map so MDC.get(key) returns current
      CONTEXT_MAP.put(key, value);
    }

    @Override
    public String popByKey(String key) {
      if (key == null) {
        return null;
      }
      java.util.Deque<String> deque = CONTEXT_STACKS.get(key);
      if (deque == null || deque.isEmpty()) {
        // nothing to pop, ensure flat map is consistent
        CONTEXT_MAP.remove(key);
        return null;
      }
      String popped = deque.pollFirst(); // pop from head (LIFO)
      if (deque.isEmpty()) {
        CONTEXT_STACKS.remove(key);
        CONTEXT_MAP.remove(key);
      } else {
        // set to the new top of the stack
        String top = deque.peekFirst();
        if (top == null) {
          CONTEXT_MAP.remove(key);
        } else {
          CONTEXT_MAP.put(key, top);
        }
      }
      return popped;
    }

    @Override
    public java.util.Deque<String> getCopyOfDequeByKey(String key) {
      if (key == null) return null;
      java.util.Deque<String> deque = CONTEXT_STACKS.get(key);
      if (deque == null || deque.isEmpty()) return null;
      // defensive copy to avoid exposing internal state
      return new java.util.ArrayDeque<String>(deque);
    }

    @Override
    public void clearDequeByKey(String key) {
      if (key == null) return;
      CONTEXT_STACKS.remove(key);
      CONTEXT_MAP.remove(key);
    }
  }

  // ----------------- Default Marker implementation -----------------
  /** Minimal {@link Marker} implementation with hierarchical references. */
  public static class SimpleMarker implements Marker {
    private final String name;
    private final Set<Marker> references = new HashSet<>();

    public SimpleMarker(String name) {
      if (name == null) throw new IllegalArgumentException("marker name cannot be null");
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void add(Marker reference) {
      if (reference == null) throw new IllegalArgumentException("marker reference cannot be null");
      if (reference == this) return;
      references.add(reference);
    }

    @Override
    public boolean remove(Marker reference) {
      if (reference == null) return false;
      return references.remove(reference);
    }

    @Override
    public boolean hasChildren() {
      return hasReferences();
    }

    @Override
    public boolean hasReferences() {
      return !references.isEmpty();
    }

    @Override
    public Iterator<Marker> iterator() {
      return references.iterator();
    }

    @Override
    public boolean contains(Marker other) {
      if (other == null) throw new IllegalArgumentException("other cannot be null");
      if (this.equals(other)) return true;
      for (Marker m : references) {
        if (m.contains(other)) return true;
      }
      return false;
    }

    @Override
    public boolean contains(String otherName) {
      if (otherName == null) return false;
      if (name.equals(otherName)) return true;
      for (Marker m : references) {
        if (m.contains(otherName)) return true;
      }
      return false;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null) return false;
      if (!(o instanceof Marker)) return false;
      return name.equals(((Marker) o).getName());
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /** Simple in-memory {@link IMarkerFactory} that caches markers by name. */
  public static class SimpleMarkerFactory implements IMarkerFactory {
    private final Map<String, Marker> markerMap = new HashMap<>();

    @Override
    public synchronized Marker getMarker(String name) {
      Marker marker = markerMap.get(name);
      if (marker == null) {
        marker = new SimpleMarker(name);
        markerMap.put(name, marker);
      }
      return marker;
    }

    @Override
    public synchronized boolean exists(String name) {
      return markerMap.containsKey(name);
    }

    @Override
    public synchronized boolean detachMarker(String name) {
      return markerMap.remove(name) != null;
    }

    @Override
    public synchronized Marker getDetachedMarker(String name) {
      return new SimpleMarker(name);
    }
  }
}
