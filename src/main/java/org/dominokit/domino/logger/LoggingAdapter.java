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

import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.event.Level;

/**
 * Pluggable logging adapter used by marker-based routing.
 *
 * <p>Implementations may forward to remote endpoints, alternate consoles, buffers, etc.
 */
public interface LoggingAdapter {

  /**
   * Log a message via this adapter.
   *
   * @param level the SLF4J level
   * @param loggerName the originating logger name
   * @param marker the SLF4J marker, may be null
   * @param message the already-formatted message, may include caller and marker/MDC decorations
   * @param throwable optional throwable, may be null
   * @param mdc a snapshot of the MDC context, may be null or empty
   */
  void log(
      Level level,
      String loggerName,
      Marker marker,
      String message,
      Throwable throwable,
      Map<String, String> mdc);
}
