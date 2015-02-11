/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "play.soap.testservice.HelloWorld",
        serviceName = "HelloWorldService", portName = "HelloWorld")
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String sayHello(@WebParam(name = "name") String name) {
        System.out.println("Say hello invoked with " + name);
        return "Hello " + name;
    }

    @Override
    public List<String> sayHelloToMany(@WebParam(name = "names") List<String> names) {
        System.out.println("Say hello to many invoked with " + names);
        List<String> hellos = new ArrayList<String>(names.size());
        for (String name: names) {
            hellos.add("Hello " + name);
        }
        return hellos;
    }

    @Override
    public Hello sayHelloToUser(@WebParam(name = "user") User user) {
        System.out.println("Say hello to user invoked with " + user.getName());
        Hello hello = new Hello();
        hello.setUser(user);
        return hello;
    }

    @Override
    public String sayHelloException(@WebParam(name = "name") String name) throws HelloException {
        throw new HelloException("Hello " + name);
    }

    @Override
    public void dontSayHello() {
    }

}