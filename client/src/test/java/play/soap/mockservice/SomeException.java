/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.mockservice;

public class SomeException extends Exception {
    public SomeException() {
    }

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
