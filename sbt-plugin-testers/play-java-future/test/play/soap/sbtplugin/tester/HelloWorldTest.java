/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap.sbtplugin.tester;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.Endpoint;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.*;
import play.soap.testservice.client.HelloWorld;
import play.soap.testservice.client.HelloWorldService;
import play.soap.testservice.client.User;
import play.test.*;
import play.libs.F;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class HelloWorldTest {

    @Test
    public void sayHello() throws Throwable {
        withClient(new F.Callback<HelloWorld>() {
            @Override
            public void invoke(HelloWorld client) throws Throwable {
                assertThat(await(client.sayHello("world"))).isEqualTo("Hello world");
            }
        });
    }

    @Test
    public void sayHelloToManyPeople() throws Throwable {
        withClient(new F.Callback<HelloWorld>() {
            @Override
            public void invoke(HelloWorld client) throws Throwable {
                List<String> names = Arrays.asList(new String[] {"foo", "bar"});
                List<String> hellos = Arrays.asList(new String[] {"Hello foo", "Hello bar"});
                assertThat(await(client.sayHelloToMany(names))).isEqualTo(hellos);
            }
        });
    }

    @Test
    public void sayHelloToOneUser() throws Throwable {
        withClient(new F.Callback<HelloWorld>() {
            @Override
            public void invoke(HelloWorld client) throws Throwable {
                User user = new User();
                user.setName("world");
                assertThat(await(client.sayHelloToUser(user)).getUser().getName()).isEqualTo("world");
            }
        });
    }

    private static <T> T await(F.Promise<T> promise) {
      return promise.get(10000); // 10 seconds
    }

    private static void withClient(final F.Callback<HelloWorld> block) throws Throwable {
        withService(new F.Callback<Integer>() {
            @Override
            public void invoke(Integer port) throws Throwable {
                Map<String, Object> additionalConfig = new HashMap<String, Object>();
                additionalConfig.put("play.soap.address", "http://localhost:"+port+"/helloWorld");
                additionalConfig.put("play.soap.debugLog", true);
                FakeApplication fakeApp = Helpers.fakeApplication(additionalConfig);
                running(fakeApp, new Runnable() {
                  @Override
                  public void run() {
                      try {
                          HelloWorld client = HelloWorldService.getHelloWorld();
                          block.invoke(client);
                      } catch (RuntimeException e) {
                          throw e;
                      } catch (Error e) {
                          throw e;
                      } catch (Throwable t) {
                          throw new RuntimeException(t);
                      }
                  }
                });
            }
        });
    }

    private static void withService(F.Callback<Integer> block) throws Throwable {
        final int port = findAvailablePort();
        final Endpoint endpoint = Endpoint.publish(
            "http://localhost:"+port+"/helloWorld",
            new play.soap.testservice.HelloWorldImpl());
        try {
            block.invoke(port);
        } finally {
            endpoint.stop();
            // Need to shutdown whole engine.  Note, Jetty's shutdown doesn't seem to happen synchronously, have to wait
            // a few seconds for the port to be released. This is why we use a different port each time.
            ((EndpointImpl) endpoint).getBus().shutdown(true);
        }
    }

    private static int findAvailablePort() throws Exception {
        final ServerSocket socket = new ServerSocket(0);
        try {
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }

}
