/**
 * Minimal SLF4J 2.x-compatible LoggingEventBuilder for GWT/J2CL super-sourcing.
 */
public interface LoggingEventBuilder {

  /**
   * Set the cause of the logging event.
   *
   * @param cause the cause
   * @return this builder
   */
  LoggingEventBuilder setCause(Throwable cause);

  /**
   * Add a marker to the logging event.
   *
   * @param marker the marker
   * @return this builder
   */
  LoggingEventBuilder addMarker(Marker marker);

  /**
   * Add an argument to the logging event.
   *
   * @param p the argument
   * @return this builder
   */
  LoggingEventBuilder addArgument(Object p);

  /**
   * Add a key-value pair to the logging event.
   *
   * @param key the key
   * @param value the value
   * @return this builder
   */
  LoggingEventBuilder addKeyValue(String key, Object value);

  /**
   * Log the event with the given message.
   *
   * @param message the message
   */
  void log(String message);

  /**
   * Log the event with the given format and argument.
   *
   * @param format the format string
   * @param arg the argument
   */
  void log(String format, Object arg);

  /**
   * Log the event with the given format and arguments.
   *
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  void log(String format, Object arg1, Object arg2);

  /**
   * Log the event with the given format and arguments.
   *
   * @param format the format string
   * @param args the arguments
   */
  void log(String format, Object... args);

  /** Log the event. */
  void log();
}
