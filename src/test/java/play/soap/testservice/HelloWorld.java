package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld {
    public String sayHello(@WebParam(name = "name") String name);
}
