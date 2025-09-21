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
 * Holder/configurator for the active ILoggerFactory used by super-sourced org.slf4j.LoggerFactory.
 *
 * Library or application code can replace the factory at startup to plug a different logger
 * implementation, e.g.:
 *   DominoLoggerFactory.setFactory(new MyCustomLoggerFactory());
 *
 * If not changed, the default returns ConsoleLoggerAdapter instances.
 */
package org.dominokit.domino.logger;

import org.slf4j.ILoggerFactory;

/**
 * Holder and configurator for the active {@link ILoggerFactory} used by the super-sourced
 * org.slf4j.LoggerFactory.
 *
 * <p>Library or application code may replace the factory at startup to plug a different logger
 * implementation.
 */
public final class DominoLoggerFactory {

  private static volatile ILoggerFactory FACTORY = new DefaultLoggerFactory();

  private DominoLoggerFactory() {}

  /**
   * Sets the active {@link ILoggerFactory}.
   *
   * @param factory a non-null factory instance
   * @throws IllegalArgumentException if the provided factory is null
   */
  public static void setFactory(ILoggerFactory factory) {
    if (factory == null) {
      throw new IllegalArgumentException("ILoggerFactory must not be null");
    }
    FACTORY = factory;
  }

  /**
   * Returns the currently configured {@link ILoggerFactory}.
   *
   * @return the active logger factory
   */
  public static ILoggerFactory getFactory() {
    return FACTORY;
  }
}
