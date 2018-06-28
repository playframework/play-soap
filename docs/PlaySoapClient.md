# Using the Play SOAP client

## Accessing the client

Once you have sbt WSDL generating a soap client for you, it is quite straightforward to use.  How you access it depends on the service and port names in the WSDL.  Consider the following service section from a WSDL:

```xml
  <wsdl:service name="HelloWorldService">
    <wsdl:port binding="tns:HelloWorldSoapBinding" name="HelloWorld">
      <soap:address location="http://example.com/helloWorld"/>
    </wsdl:port>
  </wsdl:service>
```

Assuming that the package name that the client was generated into is `com.example`, Play will generate a service class called `com.example.HelloWorldService`.  This class is actually a Play plugin that allows you to configure the client, including configuring the address for each port to use.

Note that there are some situations where sbt WSDL won't use the service name from the WSDL, these are when the name of the service conflicts with another class that it generated, such as the name of the service endpoint interface.  In that case, sbt WSDL will append `_Service` to the end of the service name, for example `com.example.HelloWorldService_Service`.

Having located the service class, you can now get a port.  In the above WSDL there is one port named `HelloWorld`, and, according to the `HelloWorldSoapBinding` (not shown above), this returns a service endpoint interface called `HelloWorld`.  To access the endpoint, simply have it injected into your components or controllers, like so in Scala:

```scala
class MyComponent @Inject() (helloWorldService: HelloWorldService) {
  val client: HelloWorld = helloWorldService.helloWorld
}
```

Or in Java:

```java
public class MyComponent {
  
    private final HelloWorldService helloWorldService;

    @Inject
    public MyComponent(HelloWorldService helloWorldService) {
        this.helloWorldService - helloWorldService;
    }

    public void someMethod() {
        HelloWorld client = helloWorldService.getHelloWorld();
        // use the client somehow
    }
```

## Using the client

Once you've got a reference to the `client`, you can invoke methods on it.  For example, let's assume our client has operation called `sayHello` that takes a String parameter and returns a String parameter.  To invoke this from a Play Scala action, you would do this:

```scala
import play.api.libs.concurrent.Execution.Implicits._

def hello(name: String) = Action.async {
  val client: HelloWorld = helloWorldService.helloWorld
  client.sayHello(name).map { answer =>
    Ok(answer)
  }
}
```

To invoke it from a Play Java action you would do this:

```java
import java.util.concurrent.CompletionStage;

public CompletionStage<Result> hello(String name) {
    HelloWorld client = helloWorldService.getHelloWorld();
    return client.sayHello(name).map(answer -> {
        return ok(answer);
    });
}
```

### A note on using Scala

The generated data objects will all be Java beans, with getter/setter style properties, and using Java collections.  For convenience when working with the Java collections, you may import the Scala implicit conversions for Scala collections, like so:

```scala
import scala.collection.JavaConverters._
```

Using this you can work with Java collections as if they were Scala collections, and pass Scala collections to setters and methods that accept Java collections.

It's also important to remember that many properties could be `null`.

A pure Scala client that uses case classes, Scala collections and `Option` is a possible future enhancement for the Play SOAP library.

## Configuring the client

Configuration for the client works hierarchically, each configuration item is first checked to see if it's defined for the port, if not then for the service, and finally globally.  The format for global configuration is `play.soap.*`.  The format for configuration applying to a particular service is `play.soap.services.<fqsn>`, where `<fqsn>` is the fully qualified service name, for example, `com.example.HelloWorldService`.  The format for configuration applying to a particular port is `play.soap.services.<fqsn>.ports.<portName>`, where `<portName>` is the name of the port, for example `HelloWorld`.

So for the client above, to set the debug log just for the port, you would set:

    play.soap.services.com.example.HelloWorldService.ports.HelloWorld.debugLog = true

To set it for the whole service, you would set:

    play.soap.services.com.example.HelloWorldService.debugLog = true

And to set it globally, you would set:

    play.soap.debugLog = true

### Changing the address

The address of a port can be set using the `address` property.  For example, to set the address of every port for the `HelloWorldService`:

    play.soap.services.com.example.HelloWorldService.address = "http://example.com/helloWorld"

### Turning on the debug log

The debug log will log the outbound and inbound messages, including HTTP headers, made by the client.  An example of this is as follows:

```
16:52:17.953 [pool-1-thread-1] INFO  o.a.c.s.H.HelloWorldPort.HelloWorld - Outbound Message
---------------------------
ID: 1
Address: http://example.com/helloWorld
Encoding: UTF-8
Http-Method: POST
Content-Type: text/xml
Headers: {Accept=[*/*], SOAPAction=[""]}
Payload: <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns2:sayHello xmlns:ns2="http://example.com/"><name>world</name></ns2:sayHello></soap:Body></soap:Envelope>
--------------------------------------
16:52:18.180 [default-workqueue-1] INFO  o.a.c.s.H.HelloWorldPort.HelloWorld - Inbound Message
----------------------------
ID: 1
Response-Code: 200
Encoding: UTF-8
Content-Type: text/xml;charset=UTF-8
Headers: {Content-Length=[204], content-type=[text/xml;charset=UTF-8], Server=[Jetty(8.1.15.v20140411)]}
Payload: <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns2:sayHelloResponse xmlns:ns2="http://example.com/"><return>Hello world</return></ns2:sayHelloResponse></soap:Body></soap:Envelope>
--------------------------------------
```

The debug log can be turned on using the `debugLog` property, for example, to turn it on globally:

    play.soap.debugLog = true

In combination with the `debugLog` property, you may need to adjust the logging levels in your Play application.  To see the debug log, you need to ensure that `org.apache.cxf.services` is configured to log at least `INFO` messages.  This can be further refined by supplying the service name, port and service endpoint interface name, for example, `org.apache.cxf.services.HelloWorldService`.
