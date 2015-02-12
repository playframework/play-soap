# Reactive SOAP for Play

There are a number of distinct parts to this, not all of them need to be implemented to have a viable product:

Feature           | Importance | Status      | Description
------------------|------------|-------------|------------
**Client proxy**  | Required   | PoC         | Given an interface that returns responses as `scala.concurrent.Future` or `play.libs.F.Promise`, generate an asynchronous web services client that implements it.
**wsdl2java**     | Required   | Not started | sbt plugin that, given a wsdl, generates the above interface
**WS backend**    | Low        | Not started | Implement the cxf http backend using Play's WS API
**Scala binding** | Medium     | Not started | Allow the use of Scala case classes, collections, option etc, rather than using Java beans. May use JAXB or completely different xml binding library.
**wsdl2scala**    | Medium     | Not started | sbt plugin that, given a wsdl, generates the client interface with Scala dataobjects using the above bindings
**Server**        | Low        | Not started | Given an implementation of an interface that returns responses as future or promise, implement a Play server that serves it

Note that Scala support is a lower priority than Java support because a Java client with Java data objects can trivially be consumed by Scala, but not the other way around.  Also it's possible (but not yet known) that Scala data binding may be very difficult.

## Client proxy

### Proxy interceptor

To implement the proxy, we have to implement our own version of JaxWsClientProxy. This is the CXF JDK proxy interceptor that implements JAX WS interfaces.  It's here that asynchronous requests are handled, and the logic here is hard coded - it implements the JAX WS requirements, if a method ends in `Async` and returns something that implements `Future` then dispatch an asynchronous call.  Hence why we have to implement our own to make every method asynchronous regardless of name, and to allow scala Future and Play Promise return types.

This class has a lot of logic copied from JaxWsClientProxy, the actual part that has been customised is quite simple, it just creates a promise, and sends an asynchronous callback that redeems the promise.

### Return type binding

When the SOAP bindings are generated, JAXB bindings are generated from the return type.  Since this type is a future, we need to tell CXF to use the type it contains.

JAX WS provides a `Holder` type, this is used to allow methods return multiple values, something that is not possible otherwise in Java.  It does this by having the first value returned as the return value of the method, and passing additional `Holder` objects as arguments to the method, whose values are set when the method returns.  Although this wasn't designed with returning futures in mind, the implementation of it in Apache CXF makes it quite simple to reuse this mechanism to extract the return type, hence this is what we're doing.  This allows us to completely reuse all the reflection code from Apache CXF that generates the bindings from the interface.

In future, we may decide to implement our own reflection code for generating bindings, but for now using the `Holder` mechanism is good enough.  A small amount of hacking is necessary to use it, due to some odd behaviour by the CXF JAX WS support - when the bindings are created, the JAX WS support automatically inserts its own configuration, and implements it in such a way that any configuration that we've added for holders gets overridden.  We work around this by overriding a method that eventually injects this configuration but is invoked before the binding is actually done, and after invoking the super for the method, we inject our own configuration to override the default behaviour.

### Next steps

* Implement support for `play.libs.F.Promise`
* Improve API for creating the client proxy (make it simpler for the simple cases)
* Document how to create and configure the client proxy
* Tests, and general clean up of code.
* Ensure asynchronous http backend is used (currently JDK URLConnection is used)

## wsdl2java

This is implemented as an SBT plugin.  Apache CXF's wsdl2java support is in fact very pluggable, there is a `META-INF/tools-plugin.xml` descriptor that allows you to define custom generators ("frontend profiles").  So we've got a custom one of these, and it provides a custom Service Endpoint Interface (SEI) generator, which extends the default one, but provides a different velocity template.  This velocity template is essentially identical to the default one, except that it wraps the return type in a future/promise.

For invoking wsdl2java, the interface provided by Apache CXF is one where arguments are passed as a sequence of Strings.  So the sbt plugin has to build up these arguments.

### Next steps

* Scripted tests
* Support common arguments to wsdl2java as SBT settings/tasks
* Incremental compilation support
* Documentation

## WS backend

## Scala binding

## wsdl2scala

## Server

Not likely to ever be implemented.

# Docs

Start sbt, and run:

    project docs
    ~webStage

Now modify the docs (either markdown, or the template, or the stylesheets), and open `target/web/stage/Home.html` to view them.

To deploy the docs, simply run:

    sbt docs/publish
