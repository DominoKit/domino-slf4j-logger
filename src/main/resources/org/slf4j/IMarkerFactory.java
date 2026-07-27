/**
 * Minimal IMarkerFactory for GWT/J2CL super-sourcing.
 */
public interface IMarkerFactory {
  /**
   * Return a {@link Marker} instance as specified by its name.
   *
   * @param name the name of the marker
   * @return the marker instance
   */
  org.slf4j.Marker getMarker(String name);

  /**
   * Check whether the marker with the given name exists.
   *
   * @param name the name of the marker
   * @return true if the marker exists, false otherwise
   */
  boolean exists(String name);

  /**
   * Detach the marker with the given name.
   *
   * @param name the name of the marker to be detached
   * @return true if the marker was detached, false otherwise
   */
  boolean detachMarker(String name);

  /**
   * Return a detached {@link Marker} instance as specified by its name.
   *
   * @param name the name of the marker
   * @return the marker instance
   */
  org.slf4j.Marker getDetachedMarker(String name);
}
