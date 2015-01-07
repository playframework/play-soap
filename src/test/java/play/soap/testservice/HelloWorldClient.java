package play.soap.testservice;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class HelloWorldClient {

    public static void main(String... args) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(HelloWorld.class);
        factory.setAddress("http://localhost:9000/helloWorld");
        HelloWorld client = (HelloWorld) factory.create();

        String reply = client.sayHello("world");
        System.out.println("Server said: " + reply);
        System.exit(0);
    }

}
