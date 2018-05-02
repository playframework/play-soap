# Security

Most security protocols can be implemented in Play SOAP using handlers.  For general documentation on handlers, see [[here|Handlers]].

## Adding an authentication token to requests

Let's say you wanted to make authenticated requests on a web service that expected an authentication token in the request header.  To implement this, you can get the request headers from the message context, and add the authentication token there.  The HTTP request headers can be loaded by reading the `javax.xml.ws.handler.MessageContext.HTTP_REQUEST_HEADERS` property.  Of course, this should only be done when the message is an outbound message, so the implementation needs to check that as well.

### Adding an authentication token in Java

```java
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outbound) {
            // Get headers, create if null
            Map<String, List<String>> headers = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
            if (headers == null) {
                headers = new HashMap<>();
            }

            // Add authentication header
            headers.put("Authentication-Token", Arrays.asList("somesecret"));
            context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
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

### Adding an authentication token in Scala

Note that in the collection types used by the JAX WS API are Java collection types, so care needs to be taken to ensure that these types are used in the Scala code, rather than the Scala collection types.  A common way to address this in Scala is to use aliased imports, prepending the letter `J` to each type, for example, `import java.util.{Map => JMap}`.

```scala
import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.soap.{SOAPMessageContext, SOAPHandler}

import java.util.{Map => JMap, List => JList, HashMap => JHashMap}
import scala.collection.JavaConverters._

class AuthenticationHandler extends SOAPHandler[SOAPMessageContext] {
  def getHeaders = null

  def handleMessage(context: SOAPMessageContext) = {
    // If this is an outbound message
    if (context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)
      .asInstanceOf[java.lang.Boolean]) {

      // Get the request headers, which may be null, in which case create them
      val headers = Option(context.get(MessageContext.HTTP_REQUEST_HEADERS)
        .asInstanceOf[JMap[String, JList[String]]]
      ).getOrElse(new JHashMap[String, JList[String]])

      // Add the authentication token to the headers
      headers += ("Authentication-Token" -> List("somesecret"))

      // Attach the headers to the context
      context += (MessageContext.HTTP_REQUEST_HEADERS -> headers)

    }
    true
  }

  def close(context: MessageContext) = ()

  def handleFault(context: SOAPMessageContext) = true
}
```
