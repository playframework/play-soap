# Play SOAP

Play SOAP allows a Play application to make calls on a remote web service using SOAP.  It provides a reactive interface to doing so, making HTTP requests asynchronously and returning promises/futures of the result.

## JAX WS support

Play SOAP builds on the JAX WS spec, but doesn't implement it exactly.  JAX WS, while it does have support for making asynchronous calls, this support is somewhat clumsy, requiring all asynchronous methods to have an `Async` suffix, and requiring the passing of an `AsyncHandler` argument to handle the response, which makes it awkward to integrate into an asynchronous framework since `AsyncHandler`'s do not compose well with other asynchronous constructs.  This support could be described as a second class citizen, bolted on to the spec as an after thought.

In contrast, Play SOAP provides asynchronous invocation of SOAP services as a first class citizen.  Play SOAP methods all return promises, making them easy to compose with promises from other libraries, and allowing application code to be focussed on business logic, not on wiring asynchronous callbacks together.

## Using Play SOAP

Play SOAP is an sbt plugin that transforms WSDLs into SOAP client interfaces, and provides a client library that takes Play SOAP generated interfaces and dynamically implements them to make calls on remote services.  The sbt plugin is called `SbtWsdl`, and this is the starting point to installing and using Play SOAP.

### Getting started

* [[Installation and using SbtWsdl|SbtWsdl]]
* [[Using a Play SOAP client|PlaySoapClient]]

### Advanced

* [[Using JAX WS Handlers|Handlers]]
* [[Security|Security]]

### API Docs

* [Play SOAP client](api/client/index.html)
* [SbtWsdl](api/sbtwsdl/index.html)
