/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester;

import java.lang.RuntimeException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.*;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.*;
import play.soap.testservice.client.*;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class HelloWorldTest {

    @Test
    public void sayHello() {
        withClient(client ->
            assertEquals("Hello world", await(client.sayHello("world")))
        );
    }

    @Test
    public void sayHelloToManyPeople() {
        withClient(client -> {
            List<String> names = Arrays.asList("foo", "bar");
            List<String> hellos = Arrays.asList("Hello foo", "Hello bar");
            assertEquals(hellos, await(client.sayHelloToMany(names)));
        });
    }

    @Test
    public void sayHelloToOneUser() {
        withClient(client -> {
            User user = new User();
            user.setName("world");
            assertEquals("world", await(client.sayHelloToUser(user)).getUser().getName());
        });
    }

    @Test
    public void sayHelloException() {
        withClient(client -> {
            try {
                await(client.sayHelloException("world"));
                fail();
            } catch (Exception ex) {
                assertEquals(
                    "play.soap.testservice.client.HelloException_Exception: Hello world",
                    ex.getMessage()
                );
            }
        });
    }

    @Test
    public void dontSayHello() {
        withClient(client -> {
            assertNull(await(client.dontSayHello()));
        });
    }

    @Test
    public void workWithCustomHandlers() {
        withApp(app -> {
            final AtomicBoolean invoked = new AtomicBoolean();
            HelloWorld client = app.injector().instanceOf(HelloWorldService.class).getHelloWorld(new SOAPHandler<SOAPMessageContext>() {
                public Set<QName> getHeaders() {
                    return null;
                }

                public boolean handleMessage(SOAPMessageContext context) {
                    invoked.set(true);
                    return true;
                }

                public boolean handleFault(SOAPMessageContext context) {
                    return true;
                }

                public void close(MessageContext context) {
                }
            });

            assertEquals("Hello world", await(client.sayHello("world")));
            assertTrue(invoked.get());
        });
    }

    private static <T> T await(F.Promise<T> promise) {
        return promise.get(10000); // 10 seconds
    }

    private static void withClient(Consumer<HelloWorld> block) {
        withApp(app -> {
            HelloWorld client = app.injector().instanceOf(HelloWorldService.class).getHelloWorld();
            block.accept(client);
        });
    }

    private static void withApp(Consumer<Application> block) {
        withService(port -> {
            GuiceApplicationBuilder builder = new GuiceApplicationBuilder()
                    .configure("play.soap.address", "http://localhost:"+port+"/helloWorld")
                    .configure("play.soap.debugLog", true);
            Application app = builder.build();
            running(app, () -> block.accept(app));
        });
    }

    private static void withService(Consumer<Integer> block) {
        final int port = findAvailablePort();
        final Endpoint endpoint = Endpoint.publish(
                "http://localhost:"+port+"/helloWorld",
                new play.soap.testservice.HelloWorldImpl());
        try {
            block.accept(port);
        } finally {
            endpoint.stop();
            // Need to shutdown whole engine.  Note, Jetty's shutdown doesn't seem to happen synchronously, have to wait
            // a few seconds for the port to be released. This is why we use a different port each time.
            ((EndpointImpl) endpoint).getBus().shutdown(true);
        }
    }

    private static int findAvailablePort() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            try {
                return socket.getLocalPort();
            } finally {
                socket.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
