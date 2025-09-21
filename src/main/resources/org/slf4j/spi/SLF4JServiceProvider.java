/*
 * Minimal SLF4JServiceProvider for GWT/J2CL super-sourcing.
 * Mirrors SLF4J 2.x SPI so that applications can plug alternative logging providers.
 */
package org.slf4j.spi;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;

public interface SLF4JServiceProvider {

  /**
   * Return the {@link ILoggerFactory} to be used by SLF4J.
   */
  ILoggerFactory getLoggerFactory();

  /**
   * Return the {@link IMarkerFactory} to be used by SLF4J. May return null in minimal environments.
   */
  IMarkerFactory getMarkerFactory();

  /**
   * Return the {@link MDCAdapter} to be used by SLF4J. May return null in minimal environments.
   */
  MDCAdapter getMDCAdapter();

  /**
   * Must return the requested API version, e.g., "2.0.99" format in SLF4J 2.x.
   */
  String getRequestedApiVersion();

  /**
   * Called by the framework once the provider is selected to allow initialization.
   */
  void initialize();
}
