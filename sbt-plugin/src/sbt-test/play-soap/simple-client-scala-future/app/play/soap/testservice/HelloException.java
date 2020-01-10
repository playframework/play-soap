/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.testservice;

public class HelloException extends Exception {

    public HelloException() {
    }

    public HelloException(String msg) {
        super(msg);
    }
}
