/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
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
