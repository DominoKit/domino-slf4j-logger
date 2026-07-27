/**
 * Minimal SLF4JServiceProvider for GWT/J2CL super-sourcing.
 *
 * <p>Mirrors SLF4J 2.x SPI so that applications can plug alternative logging providers.
 */
public interface SLF4JServiceProvider {

  /**
   * Return the {@link ILoggerFactory} to be used by SLF4J.
   *
   * @return the logger factory
   */
  ILoggerFactory getLoggerFactory();

  /**
   * Return the {@link IMarkerFactory} to be used by SLF4J. May return null in minimal environments.
   *
   * @return the marker factory
   */
  IMarkerFactory getMarkerFactory();

  /**
   * Return the {@link MDCAdapter} to be used by SLF4J. May return null in minimal environments.
   *
   * @return the MDC adapter
   */
  MDCAdapter getMDCAdapter();

  /**
   * Must return the requested API version, e.g., "2.0.99" format in SLF4J 2.x.
   *
   * @return the requested API version
   */
  String getRequestedApiVersion();

  /** Called by the framework once the provider is selected to allow initialization. */
  void initialize();
}
