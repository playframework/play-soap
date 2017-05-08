/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.testservice;

public class HelloException extends Exception {

    public HelloException() {
    }

    public HelloException(String msg) {
        super(msg);
    }
}
