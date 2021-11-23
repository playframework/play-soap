/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester

import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean
import javax.xml.ws.Endpoint
import javax.xml.ws.handler.soap._
import javax.xml.ws.handler.MessageContext

import org.apache.cxf.jaxws.EndpointImpl
import play.soap.testservice.client._
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag

import play.api.test._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.soap.testservice.HelloWorldImpl

class HelloWorldSpec extends ServiceSpec {

  sequential

  "HelloWorld" should {
    "say hello" in withClient { client =>
      await(client.sayHello("world")) must_== "Hello world"
    }

    "say hello to many people" in withClient { client =>
      await(client.sayHelloToMany(java.util.Arrays.asList("foo", "bar"))).asScala must_== List("Hello foo", "Hello bar")
    }

    "say hello to one user" in withClient { client =>
      val user = new User
      user.setName("world")
      await(client.sayHelloToUser(user)).getUser.getName must_== "world"
    }

    "say hello with an exception" in withClient { client =>
      await(client.sayHelloException("world")) must throwA[HelloException_Exception].like { case e =>
        e.getMessage must_== "Hello world"
      }
    }

    "dont say hello" in withClient { client =>
      await(client.dontSayHello()) must_== ((): Unit)
    }

    "allow adding custom handlers" in {
      val invoked = new AtomicBoolean()
      withApp { app =>
        val client = app.injector
          .instanceOf[HelloWorldService]
          .helloWorld(new SOAPHandler[SOAPMessageContext] {
            def getHeaders = null
            def handleMessage(context: SOAPMessageContext) = {
              invoked.set(true)
              true
            }
            def close(context: MessageContext)           = ()
            def handleFault(context: SOAPMessageContext) = true
          })

        await(client.sayHello("world")) must_== "Hello world"
        invoked.get() must_== true
      }
    }
  }

  override type ServiceClient = HelloWorldService

  override type Service = HelloWorld

  implicit override val serviceClientClass: ClassTag[HelloWorldService] = ClassTag(classOf[HelloWorldService])

  override def getServiceFromClient(c: ServiceClient): Service = c.helloWorld

  override def createServiceImpl(): Any = new HelloWorldImpl

  val servicePath: String = "helloWorld"

}
