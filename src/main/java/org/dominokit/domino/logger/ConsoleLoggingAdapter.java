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

import static elemental2.dom.DomGlobal.*;

import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.event.Level;

/** Console-based LoggingAdapter implementation that writes to the browser console. */
public class ConsoleLoggingAdapter implements LoggingAdapter {

  @Override
  /**
   * Writes the log message to the browser console using a console method that matches the level.
   *
   * @param level the log level
   * @param loggerName the name of the originating logger
   * @param marker the marker associated with the event, may be null
   * @param message the formatted message to be logged
   * @param throwable an optional throwable to be printed alongside the message
   * @param mdc a snapshot of the MDC context, ignored by this adapter
   */
  public void log(
      Level level,
      String loggerName,
      Marker marker,
      String message,
      Throwable throwable,
      Map<String, String> mdc) {
    switch (level) {
      case TRACE:
      case DEBUG:
        if (throwable != null) {
          console.debug(message, throwable);
        } else {
          console.debug(message);
        }
        break;
      case INFO:
        if (throwable != null) {
          console.info(message, throwable);
        } else {
          console.info(message);
        }
        break;
      case WARN:
        if (throwable != null) {
          console.warn(message, throwable);
        } else {
          console.warn(message);
        }
        break;
      case ERROR:
        if (throwable != null) {
          console.error(message, throwable);
        } else {
          console.error(message);
        }
        break;
      default:
        if (throwable != null) {
          console.log(message, throwable);
        } else {
          console.log(message);
        }
    }
  }
}
