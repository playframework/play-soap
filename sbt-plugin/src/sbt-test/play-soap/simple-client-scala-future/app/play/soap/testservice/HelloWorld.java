/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HelloWorld {
    public String sayHello(@WebParam(name = "name") String name);

    public List<String> sayHelloToMany(@WebParam(name = "names") List<String> names);

    public Hello sayHelloToUser(@WebParam(name = "user") User user);
}
