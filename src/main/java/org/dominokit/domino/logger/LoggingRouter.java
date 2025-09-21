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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Marker;

/**
 * Routes log events to marker-specific adapters or to a default adapter if configured.
 *
 * <p>Usage: - LoggingRouter.register("PAYMENT", someAdapter); -
 * LoggingRouter.setDefaultAdapter(defaultAdapter);
 */
public final class LoggingRouter {

  private static final Map<String, LoggingAdapter> ADAPTERS = new HashMap<>();
  private static volatile LoggingAdapter defaultAdapter;

  private LoggingRouter() {}

  /**
   * Set a default adapter to be used when no specific adapter is found for a marker, or when the
   * marker is null.
   */
  public static void setDefaultAdapter(LoggingAdapter adapter) {
    defaultAdapter = adapter;
  }

  /** Get the current default adapter, may be null. */
  public static LoggingAdapter getDefaultAdapter() {
    return defaultAdapter;
  }

  /** Register an adapter for a marker name. Nulls are ignored. */
  public static void register(String markerName, LoggingAdapter adapter) {
    if (markerName == null || adapter == null) return;
    ADAPTERS.put(markerName, adapter);
  }

  /** Unregister the adapter associated with the given marker name. */
  public static void unregister(String markerName) {
    if (markerName == null) return;
    ADAPTERS.remove(markerName);
  }

  /** Clear all marker-specific adapter mappings. */
  public static void clear() {
    ADAPTERS.clear();
  }

  /**
   * Resolve an adapter for the given marker. - Returns the adapter mapped to the marker's name, if
   * present. - Otherwise traverses marker references to find the first mapped adapter. - Otherwise
   * returns the default adapter (if set), or null if none.
   */
  public static LoggingAdapter resolve(Marker marker) {
    if (marker == null) return defaultAdapter;

    LoggingAdapter direct = ADAPTERS.get(marker.getName());
    if (direct != null) return direct;

    Set<String> visited = new HashSet<>();
    LoggingAdapter found = findInHierarchy(marker, visited);
    return (found != null) ? found : defaultAdapter;
  }

  private static LoggingAdapter findInHierarchy(Marker marker, Set<String> visited) {
    if (marker == null) return null;
    String name = marker.getName();
    if (name != null && !visited.add(name)) {
      return null;
    }
    LoggingAdapter a = ADAPTERS.get(name);
    if (a != null) return a;

    if (marker.hasReferences()) {
      for (java.util.Iterator<Marker> it = marker.iterator(); it.hasNext(); ) {
        LoggingAdapter child = findInHierarchy(it.next(), visited);
        if (child != null) return child;
      }
    }
    return null;
  }
}
