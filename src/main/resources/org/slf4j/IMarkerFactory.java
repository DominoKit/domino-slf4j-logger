/*
 * Minimal IMarkerFactory for GWT/J2CL super-sourcing.
 */
package org.slf4j;

public interface IMarkerFactory {
  org.slf4j.Marker getMarker(String name);
  boolean exists(String name);
  boolean detachMarker(String name);
  org.slf4j.Marker getDetachedMarker(String name);
}
