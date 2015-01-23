/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap.testservice;

public class HelloException extends Exception {

    public HelloException() {
    }

    public HelloException(String msg) {
        super(msg);
    }
}