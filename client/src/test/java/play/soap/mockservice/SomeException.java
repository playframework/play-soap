/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
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
