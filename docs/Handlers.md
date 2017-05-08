# Using JAX WS Handlers

JAX WS provides an abstraction called handlers to allow cross cutting concerns to be implemented across all calls.  Handlers are able to inspect and modify the incoming and outgoing messages, including SOAP data objects and request/response headers.  They're also able to block requests from being made entirely.

Use cases for handlers include logging, security, monitoring, and many other application specific concerns.  For examples of how to implement security using handlers, see [[Security]].

## Handlers in Java

### Implementing handlers in Java

A handler can be implemented by extending a sub type of `javax.xml.ws.handler.Handler`.  Which subtype you implement depends on your use case, for more details about the types of handlers, see [here](http://docs.oracle.com/cd/E13222_01/wls/docs103/webserv_adv/handlers.html).  We'll implement a simple logging handler, to do this we extend `javax.xml.ws.handler.soap.SOAPHandler`.

```java
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = context.getMessage();

        try {
            if (outbound) {
                System.out.println("Sending message:");
                message.writeTo(System.out);
            } else {
                Integer responseCode = (Integer) context.get(MessageContext.HTTP_RESPONSE_CODE);
                System.out.println("Received " + responseCode + "response:");
                message.writeTo(System.out);
            }
            System.out.println();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }
}
```

The main method to implement here is the `handleMessage` method.  It takes in the message context, and returns a boolean to say whether message processing should continue down the handler chain.  By returning false, you can block the request.

The same method is invoked for outgoing messages (that is, the request sent to the server) and incoming messages (that is, the response coming from the server).  To know whether a message is incoming or outgoing, the outbound property needs to be checked.  This is done using the following code:

```java
Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
```

You can see that the SOAP message is also being loaded so that it can be logged, this is the XML representation of the method invocation or response.  You can also see that the HTTP response code is being read from incoming messages, and being logged.

### Using handlers in Java

To use the logging handler that we implemented, we can supply it to get method for our port from the service.  For example, to use it with the `HelloWorldService` that we saw earlier in the documentation, simply pass the list of handlers to the `getHelloWorld` method, like so:

```java
HelloWorld client = helloWorldService.getHelloWorld(new LoggingHandler);
```

## Handlers in Scala

### Implementing handlers in Scala

A handler can be implemented by extending a sub type of `javax.xml.ws.handler.Handler`.  Which subtype you implement depends on your use case, for more details about the types of handlers, see [here](http://docs.oracle.com/cd/E13222_01/wls/docs103/webserv_adv/handlers.html).  We'll implement a simple logging handler, to do this we extend `javax.xml.ws.handler.soap.SOAPHandler`.

```scala
import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.soap.{SOAPMessageContext, SOAPHandler}

class LoggingHandler extends SOAPHandler[SOAPMessageContext] {
  def getHeaders = null

  def handleMessage(context: SOAPMessageContext) = {
    val outbound = context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)
      .asInstanceOf[java.lang.Boolean]
    val message = context.getMessage

    if (outbound) {
      println(s"Sending message:")
      message.writeTo(System.out)
    } else {
      val responseCode = context.get(MessageContext.HTTP_RESPONSE_CODE)
      println(s"Received $responseCode response:")
      message.writeTo(System.out)
    }
    println()
    true
  }

  def close(context: MessageContext) = ()

  def handleFault(context: SOAPMessageContext) = {
    println(s"Received fault:")
    context.getMessage.writeTo(System.out)
    println()
    true
  }
}
```

The main method to implement here is the `handleMessage` method.  It takes in the message context, and returns a boolean to say whether message processing should continue down the handler chain.  By returning false, you can block the request.

The same method is invoked for outgoing messages (that is, the request sent to the server) and incoming messages (that is, the response coming from the server).  To know whether a message is incoming or outgoing, the outbound property needs to be checked.  This is done using the following code:

```scala
val outbound = context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)
  .asInstanceOf[java.lang.Boolean]
```

You can see that the SOAP message is also being loaded so that it can be logged, this is the XML representation of the method invocation or response.  You can also see that the HTTP response code is being read from incoming messages, and being logged.

### Using handlers in Scala

To use the logging handler that we implemented, we can supply it to get method for our port from the service.  For example, to use it with the `HelloWorldService` that we saw earlier in the documentation, simply pass the list of handlers to the `helloWorld` method, like so:

```scala
val client: HelloWorld = helloWorldService.helloWorld(new LoggingHandler)
```
