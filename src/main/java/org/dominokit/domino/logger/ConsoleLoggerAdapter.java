/*
 * Copyright © ${year} Dominokit
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

import java.util.function.Consumer;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * This class is intended to be used in the browser and is a thin wrapper around the {@link
 * elemental2.dom.Console}
 */
public class ConsoleLoggerAdapter extends MarkerIgnoringBase {

  private static final boolean TRACE_ENABLED;
  private static final boolean INFO_ENABLED;
  private static final boolean WARNING_ENABLED;
  private static final boolean ERROR_ENABLED;
  private static final boolean DEBUG_ENABLED;

  static {
    String level = System.getProperty("domino.slf4j.logging.level", "INFO");
    if (level != "INFO"
        && level != "WARN"
        && level != "DEBUG"
        && level != "TRACE"
        && level != "ERROR"
        && level != "OFF") {
      throw new AssertionError("Undefined value for domino.slf4j.logging.level: '" + level + "'");
    }

    TRACE_ENABLED = level == "TRACE";
    INFO_ENABLED = level == "TRACE" || level == "INFO";
    DEBUG_ENABLED = level == "TRACE" || level == "INFO" || level == "DEBUG";
    WARNING_ENABLED = level == "TRACE" || level == "INFO" || level == "DEBUG" || level == "WARN";
    ERROR_ENABLED =
        level == "TRACE"
            || level == "INFO"
            || level == "DEBUG"
            || level == "WARN"
            || level == "ERROR";
  }

  public ConsoleLoggerAdapter(String name) {
    this.name = name;
  }

  @Override
  public boolean isTraceEnabled() {
    return TRACE_ENABLED;
  }

  /**
   * We are using debug instead of trace here because trace would also print a stack trace from
   * where it was called. check
   * https://developer.mozilla.org/en-US/docs/Web/API/Console#Stack_traces
   */
  @Override
  public void trace(String msg) {
    if (TRACE_ENABLED) {
      console.debug(msg);
    }
  }

  @Override
  public void trace(String format, Object arg) {
    if (TRACE_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, arg);
    }
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    if (TRACE_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, arg1, arg2);
    }
  }

  @Override
  public void trace(String format, Object... argArray) {
    if (TRACE_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, argArray);
    }
  }

  @Override
  public void trace(String msg, Throwable t) {
    if (TRACE_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), msg, t);
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return DEBUG_ENABLED;
  }

  @Override
  public void debug(String msg) {
    if (DEBUG_ENABLED) {
      console.debug(msg);
    }
  }

  @Override
  public void debug(String format, Object arg) {
    if (DEBUG_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, arg);
    }
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    if (DEBUG_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, arg1, arg2);
    }
  }

  @Override
  public void debug(String format, Object... argArray) {
    if (DEBUG_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), format, argArray);
    }
  }

  @Override
  public void debug(String msg, Throwable t) {
    if (DEBUG_ENABLED) {
      formatAndLog(ft -> console.debug(ft.getMessage(), ft.getThrowable()), msg, t);
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return INFO_ENABLED;
  }

  @Override
  public void info(String msg) {
    if (INFO_ENABLED) {
      console.info(msg);
    }
  }

  @Override
  public void info(String format, Object arg) {
    if (INFO_ENABLED) {
      formatAndLog(ft -> console.info(ft.getMessage(), ft.getThrowable()), format, arg);
    }
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    if (INFO_ENABLED) {
      formatAndLog(ft -> console.info(ft.getMessage(), ft.getThrowable()), format, arg1, arg2);
    }
  }

  @Override
  public void info(String format, Object... argArray) {
    if (INFO_ENABLED) {
      formatAndLog(ft -> console.info(ft.getMessage(), ft.getThrowable()), format, argArray);
    }
  }

  @Override
  public void info(String msg, Throwable t) {
    if (INFO_ENABLED) {
      console.info(msg, t);
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return WARNING_ENABLED;
  }

  @Override
  public void warn(String msg) {
    if (WARNING_ENABLED) {
      console.warn(msg);
    }
  }

  @Override
  public void warn(String format, Object arg) {
    if (WARNING_ENABLED) {
      formatAndLog(ft -> console.warn(ft.getMessage(), ft.getThrowable()), format, arg);
    }
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    if (WARNING_ENABLED) {
      formatAndLog(ft -> console.warn(ft.getMessage(), ft.getThrowable()), format, arg1, arg2);
    }
  }

  @Override
  public void warn(String format, Object... argArray) {
    if (WARNING_ENABLED) {
      formatAndLog(ft -> console.warn(ft.getMessage(), ft.getThrowable()), format, argArray);
    }
  }

  @Override
  public void warn(String msg, Throwable t) {
    if (WARNING_ENABLED) {
      console.warn(msg, t);
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return ERROR_ENABLED;
  }

  @Override
  public void error(String msg) {
    if (ERROR_ENABLED) {
      console.error(msg);
    }
  }

  @Override
  public void error(String format, Object arg) {
    if (ERROR_ENABLED) {
      formatAndLog(ft -> console.error(ft.getMessage(), ft.getThrowable()), format, arg);
    }
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    if (ERROR_ENABLED) {
      formatAndLog(ft -> console.error(ft.getMessage(), ft.getThrowable()), format, arg1, arg2);
    }
  }

  @Override
  public void error(String format, Object... argArray) {
    if (ERROR_ENABLED) {
      formatAndLog(ft -> console.error(ft.getMessage(), ft.getThrowable()), format, argArray);
    }
  }

  @Override
  public void error(String msg, Throwable t) {
    if (ERROR_ENABLED) {
      console.error(msg, t);
    }
  }

  private void formatAndLog(
      Consumer<FormattingTuple> logConsumer, String format, Object... argArray) {
    logConsumer.accept(MessageFormatter.arrayFormat(format, argArray));
  }
}
