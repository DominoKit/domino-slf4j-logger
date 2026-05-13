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
    /**
     * Puts a key-value pair into the context map.
     *
     * @param key the key to add, if null the call is ignored
     * @param val the value to add, if null the key is removed from the map
     */
    public void put(String key, String val) {
      if (key == null) return;
      if (val == null) {
        CONTEXT_MAP.remove(key);
      } else {
        CONTEXT_MAP.put(key, val);
      }
    }

    @Override
    /**
     * Gets the value associated with the given key from the context map.
     *
     * @param key the key to look up
     * @return the value associated with the key, or null if not found
     */
    public String get(String key) {
      return CONTEXT_MAP.get(key);
    }

    @Override
    /**
     * Removes the key and its associated value from the context map and any maintained stack.
     *
     * @param key the key to remove
     */
    public void remove(String key) {
      CONTEXT_MAP.remove(key);
      // also drop any maintained stack for this key
      CONTEXT_STACKS.remove(key);
    }

    @Override
    /** Clears all entries from the context map and all maintained stacks. */
    public void clear() {
      CONTEXT_MAP.clear();
      CONTEXT_STACKS.clear();
    }

    @Override
    /**
     * Returns a copy of the current context map.
     *
     * @return a copy of the context map, or null if it's empty
     */
    public Map<String, String> getCopyOfContextMap() {
      return CONTEXT_MAP.isEmpty() ? null : new HashMap<>(CONTEXT_MAP);
    }

    @Override
    /**
     * Sets the context map to the provided map, clearing any existing entries and stacks.
     *
     * @param contextMap the new context map to set, may be null
     */
    public void setContextMap(Map<String, String> contextMap) {
      CONTEXT_MAP.clear();
      CONTEXT_STACKS.clear();
      if (contextMap != null) {
        CONTEXT_MAP.putAll(contextMap);
      }
    }

    @Override
    /**
     * Pushes a value onto the stack associated with the given key. The value also becomes the
     * current value for that key in the flat context map.
     *
     * @param key the key associated with the stack
     * @param value the value to push onto the stack
     */
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
    /**
     * Pops the top value from the stack associated with the given key. The new top value (if any)
     * becomes the current value for that key in the flat context map.
     *
     * @param key the key associated with the stack
     * @return the popped value, or null if the stack was empty or key was null
     */
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
    /**
     * Returns a copy of the stack associated with the given key.
     *
     * @param key the key associated with the stack
     * @return a copy of the stack, or null if the stack was empty or key was null
     */
    public java.util.Deque<String> getCopyOfDequeByKey(String key) {
      if (key == null) return null;
      java.util.Deque<String> deque = CONTEXT_STACKS.get(key);
      if (deque == null || deque.isEmpty()) return null;
      // defensive copy to avoid exposing internal state
      return new java.util.ArrayDeque<String>(deque);
    }

    @Override
    /**
     * Clears the stack associated with the given key and removes the key from the context map.
     *
     * @param key the key associated with the stack
     */
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

    /**
     * Creates a new marker with the given name.
     *
     * @param name the name of the marker, must not be null
     * @throws IllegalArgumentException if name is null
     */
    public SimpleMarker(String name) {
      if (name == null) throw new IllegalArgumentException("marker name cannot be null");
      this.name = name;
    }

    @Override
    /**
     * Returns the name of the marker.
     *
     * @return the marker name
     */
    public String getName() {
      return name;
    }

    @Override
    /**
     * Adds a reference to another marker.
     *
     * @param reference the marker to add as a reference, must not be null
     * @throws IllegalArgumentException if reference is null
     */
    public void add(Marker reference) {
      if (reference == null) throw new IllegalArgumentException("marker reference cannot be null");
      if (reference == this) return;
      references.add(reference);
    }

    @Override
    /**
     * Removes a marker reference.
     *
     * @param reference the marker reference to remove
     * @return true if the reference was removed, false otherwise
     */
    public boolean remove(Marker reference) {
      if (reference == null) return false;
      return references.remove(reference);
    }

    @Override
    /**
     * Checks if this marker has any references.
     *
     * @return true if there are references, false otherwise
     */
    public boolean hasChildren() {
      return hasReferences();
    }

    @Override
    /**
     * Checks if this marker has any references.
     *
     * @return true if there are references, false otherwise
     */
    public boolean hasReferences() {
      return !references.isEmpty();
    }

    @Override
    /**
     * Returns an iterator over the references of this marker.
     *
     * @return an iterator over the marker references
     */
    public Iterator<Marker> iterator() {
      return references.iterator();
    }

    @Override
    /**
     * Checks if this marker contains the given marker, either directly or transitively.
     *
     * @param other the marker to check for
     * @return true if this marker contains the other marker, false otherwise
     * @throws IllegalArgumentException if other is null
     */
    public boolean contains(Marker other) {
      if (other == null) throw new IllegalArgumentException("other cannot be null");
      if (this.equals(other)) return true;
      for (Marker m : references) {
        if (m.contains(other)) return true;
      }
      return false;
    }

    @Override
    /**
     * Checks if this marker contains a marker with the given name, either directly or transitively.
     *
     * @param otherName the name of the marker to check for
     * @return true if this marker contains a marker with the given name, false otherwise
     */
    public boolean contains(String otherName) {
      if (otherName == null) return false;
      if (name.equals(otherName)) return true;
      for (Marker m : references) {
        if (m.contains(otherName)) return true;
      }
      return false;
    }

    @Override
    /**
     * Checks if this marker is equal to the given object. Markers are equal if their names are
     * equal.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null) return false;
      if (!(o instanceof Marker)) return false;
      return name.equals(((Marker) o).getName());
    }

    @Override
    /**
     * Returns the hash code of the marker name.
     *
     * @return the marker hash code
     */
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    /**
     * Returns the name of the marker.
     *
     * @return the marker name
     */
    public String toString() {
      return name;
    }
  }

  /** Simple in-memory {@link IMarkerFactory} that caches markers by name. */
  public static class SimpleMarkerFactory implements IMarkerFactory {
    private final Map<String, Marker> markerMap = new HashMap<>();

    @Override
    /**
     * Returns a marker for the given name. If it doesn't exist, a new one is created.
     *
     * @param name the name of the marker
     * @return the marker instance
     */
    public synchronized Marker getMarker(String name) {
      Marker marker = markerMap.get(name);
      if (marker == null) {
        marker = new SimpleMarker(name);
        markerMap.put(name, marker);
      }
      return marker;
    }

    @Override
    /**
     * Checks if a marker with the given name exists.
     *
     * @param name the name of the marker
     * @return true if the marker exists, false otherwise
     */
    public synchronized boolean exists(String name) {
      return markerMap.containsKey(name);
    }

    @Override
    /**
     * Removes the marker with the given name from the factory.
     *
     * @param name the name of the marker to remove
     * @return true if the marker was removed, false otherwise
     */
    public synchronized boolean detachMarker(String name) {
      return markerMap.remove(name) != null;
    }

    @Override
    /**
     * Returns a new detached marker with the given name.
     *
     * @param name the name of the marker
     * @return a new marker instance
     */
    public synchronized Marker getDetachedMarker(String name) {
      return new SimpleMarker(name);
    }
  }
}
