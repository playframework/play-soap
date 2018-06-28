/*
 * Copyright (C) 2015-2018 Lightbend Inc. <https://www.lightbend.com>
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

    public String sayHelloException(@WebParam(name = "name") String name) throws HelloException;

    public void dontSayHello();
}
