/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester

import java.net.ServerSocket
import javax.xml.ws.{BindingProvider, Endpoint}

import org.apache.cxf.endpoint.Server
import org.apache.cxf.interceptor.{LoggingInInterceptor, LoggingOutInterceptor}
import org.apache.cxf.jaxws.EndpointImpl
import play.soap.PlayJaxWsProxyFactoryBean
import play.soap.testservice.client._
import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import play.api.test._
import play.api.inject.guice.GuiceApplicationBuilder

object HelloWorldSpec extends PlaySpecification {

  sequential

  "HelloWorld" should {
    "say hello" in withClient { client =>
      await(client.sayHello("world")) must_== "Hello world"
    }

    "say hello to many people" in withClient { client =>
      await(client.sayHelloToMany(List("foo", "bar"))).toList must_== List("Hello foo", "Hello bar")
    }

    "say hello to one user" in withClient { client =>
      val user = new User
      user.setName("world")
      await(client.sayHelloToUser(user)).getUser.getName must_== "world"
    }

    "say hello with an exception" in withClient { client =>
      await(client.sayHelloException("world")) must throwA[HelloException_Exception].like {
        case e => e.getMessage must_== "Hello world"
      }
    }

    "dont say hello" in withClient { client =>
      await(client.dontSayHello()) must_== ()
    }
  }

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def withClient[T](block: HelloWorld => T): T = withService { port =>
    val app = new GuiceApplicationBuilder()
      .configure("play.soap.address" -> s"http://localhost:$port/helloWorld")
      .build
    Helpers.running(app) {
      val helloWorld = app.injector.instanceOf[HelloWorldService].helloWorld(new LoggingHandler, new AuthenticationHandler)
      block(helloWorld)
    }
  }

  def withService[T](block: Int => T): T = {
    val port = findAvailablePort()
    val endpoint = Endpoint.publish(s"http://localhost:$port/helloWorld", new play.soap.testservice.HelloWorldImpl)
    endpoint.getBinding.setHandlerChain(List(new ServerAuthenticationHandler))
    try {
      block(port)
    } finally {
      endpoint.stop()
      // Need to shutdown whole engine.  Note, Jetty's shutdown doesn't seem to happen synchronously, have to wait
      // a few seconds for the port to be released. This is why we use a different port each time.
      endpoint.asInstanceOf[EndpointImpl].getBus.shutdown(true)
    }
  }

  def findAvailablePort() = {
    val socket = new ServerSocket(0)
    try {
      socket.getLocalPort
    } finally {
      socket.close()
    }
  }

}
