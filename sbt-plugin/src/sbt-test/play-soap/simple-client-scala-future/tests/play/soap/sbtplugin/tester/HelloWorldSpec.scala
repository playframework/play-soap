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

import org.apache.cxf.jaxws.EndpointImpl
import play.soap.testservice.client._
import scala.collection.JavaConversions._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.reflect.ClassTag

import play.api.test._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.soap.testservice.HelloWorldImpl

object HelloWorldSpec extends ServiceSpec {

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
      withApp { app =>
        val client = app.injector.instanceOf[HelloWorldService].helloWorld(new SOAPHandler[SOAPMessageContext] {
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

  override type ServiceClient = HelloWorldService

  override type Service = HelloWorld

  override implicit val serviceClientClass: ClassTag[HelloWorldService] = ClassTag(classOf[HelloWorldService])

  override def getServiceFromClient(c: ServiceClient): Service = c.helloWorld

  override def createServiceImpl(): Any = new HelloWorldImpl

  val servicePath: String = "helloWorld"

}