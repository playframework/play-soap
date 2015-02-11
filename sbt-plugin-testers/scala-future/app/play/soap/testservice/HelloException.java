/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.testservice;

public class HelloException extends Exception {

    public HelloException() {
    }

    public HelloException(String msg) {
        super(msg);
    }
}