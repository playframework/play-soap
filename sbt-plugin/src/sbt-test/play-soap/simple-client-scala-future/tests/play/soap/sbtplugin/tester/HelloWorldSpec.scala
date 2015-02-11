/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.sbtplugin.tester

import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean
import javax.xml.ws.Endpoint
import javax.xml.ws.handler.soap._
import javax.xml.ws.handler.MessageContext

import org.apache.cxf.endpoint.Server
import org.apache.cxf.interceptor.{LoggingInInterceptor, LoggingOutInterceptor}
import org.apache.cxf.jaxws.EndpointImpl
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine
import play.soap.PlayJaxWsProxyFactoryBean
import play.soap.testservice.client._
import scala.collection.JavaConversions._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import play.api.test._
import play.api.Application

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

    "allow adding custom handlers" in {
      val invoked = new AtomicBoolean()
      withApp { implicit app =>
        val client = HelloWorldService.helloWorld(new SOAPHandler[SOAPMessageContext] {
          def getHeaders = null
          def handleMessage(context: SOAPMessageContext) = {
            invoked.set(true)
            true
          }
          def close(context: MessageContext) = ()
          def handleFault(context: SOAPMessageContext) = true
        })

        await(client.sayHello("world")) must_== "Hello world"
        invoked.get() must_== true
      }
    }
  }

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def withClient[T](block: HelloWorld => T): T = withApp { implicit app =>
    block(HelloWorldService.helloWorld)
  }

  def withApp[T](block: Application => T): T = withService { port =>
    implicit val app = FakeApplication(additionalConfiguration =
      Map(
        "play.soap.address" -> s"http://localhost:$port/helloWorld"
      ))
    Helpers.running(app) {
      block(app)
    }
  }


  def withService[T](block: Int => T): T = {
    val port = findAvailablePort()
    val endpoint = Endpoint.publish(s"http://localhost:$port/helloWorld", new play.soap.testservice.HelloWorldImpl)
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