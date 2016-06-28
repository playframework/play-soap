/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.testservice;

public class Hello {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
