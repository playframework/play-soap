/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

public class SomeException extends Exception {
  public SomeException() {}

  public SomeException(String message) {
    super(message);
  }

  public SomeException(String message, Throwable cause) {
    super(message, cause);
  }

  public SomeException(Throwable cause) {
    super(cause);
  }
}
