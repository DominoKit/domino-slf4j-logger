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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Marker;
import org.slf4j.event.Level;

/**
 * A LoggingAdapter that broadcasts each log event to a set of delegate adapters.
 *
 * <p>Use this to fan out logs to multiple destinations (e.g., console plus remote sink).
 */
public class CompositeLoggingAdapter implements LoggingAdapter {

  private final Set<LoggingAdapter> adapters;

  /**
   * Creates a new composite adapter that forwards to the given adapters.
   *
   * @param adapters one or more delegate adapters; nulls are not expected
   */
  public CompositeLoggingAdapter(LoggingAdapter... adapters) {
    this.adapters = new HashSet<>(Arrays.asList(adapters));
  }

  @Override
  /**
   * Forwards the log call to all configured delegate adapters.
   *
   * @param level the log level
   * @param loggerName the originating logger name
   * @param marker the marker associated with the event, may be null
   * @param message the formatted message
   * @param throwable an optional throwable, may be null
   * @param mdc a snapshot of the MDC context, may be null or empty
   */
  public void log(
      Level level,
      String loggerName,
      Marker marker,
      String message,
      Throwable throwable,
      Map<String, String> mdc) {
    adapters.forEach(adapter -> adapter.log(level, loggerName, marker, message, throwable, mdc));
  }
}
