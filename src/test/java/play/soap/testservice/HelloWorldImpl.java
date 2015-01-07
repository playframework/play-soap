package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(endpointInterface = "play.soap.testservice.HelloWorld",
        serviceName = "HelloWorld")
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String sayHello(@WebParam(name = "name") String name) {
        System.out.println("Say hello invoked with " + name);
        return "Hello " + name;
    }
}