/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.net.ServerSocket
import java.util.concurrent.CompletionStage
import javax.xml.ws.{Endpoint, Holder}

import org.apache.cxf.jaxws.EndpointImpl
import org.specs2.mutable.Specification
import play.soap.mockservice._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.reflect.ClassTag
import org.apache.cxf.binding.soap.SoapFault

import scala.compat.java8.FutureConverters

class PlayJaxWsClientProxySpec extends Specification {

  sequential

  "The client proxy" should {
    "work with a scala client" in {

      "allow calling a simple method" in withScalaClient { client =>
        await(client.add(3, 4)) must_== 7
      }

      "allow calling a method with complex types" in withScalaClient { client =>
        val foo = new Foo(10, "foo")
        val bar = await(client.getBar(foo))
        bar.getFoo must_== foo
        bar.getName must_== "bar"
      }

      "allow calling a method with multiple return types" in withScalaClient { client =>
        val second = new Holder[String]
        val first = await(client.multiReturn(second, "hello", 2))
        first must_== "he"
        second.value must_== "llo"
      }

      "allow calling a method with no return value" in withScalaClient { client =>
        await(client.noReturn("nothing")) must_== ((): Unit)
      }

      "allow calling a method that throws a declared exception" in withScalaClient { client =>
        val result = client.declaredException()
        await(result) must throwA[SomeException].like {
          case e: SomeException => e.getMessage must_== "an error occurred"
        }
      }

      "allow calling a method that throws an undeclared exception" in withScalaClient { client =>
        val result = client.runtimeException()
        await(result) must throwA[SoapFault].like {
          case e: SoapFault => e.getMessage must_== "an error occurred"
        }
      }
    }

    "work with a java client" in {

      "allow calling a simple method" in withJavaClient { client =>
        await(client.add(3, 4)) must_== 7
      }

      "allow calling a method with complex types" in withJavaClient { client =>
        val foo = new Foo(10, "foo")
        val bar = await(client.getBar(foo))
        bar.getFoo must_== foo
        bar.getName must_== "bar"
      }

      "allow calling a method with multiple return types" in withJavaClient { client =>
        val second = new Holder[String]
        val first = await(client.multiReturn(second, "hello", 2))
        first must_== "he"
        second.value must_== "llo"
      }

      "allow calling a method with no return value" in withJavaClient { client =>
        await(client.noReturn("nothing")) must_== null
      }

      "allow calling a method that throws a declared exception" in withJavaClient { client =>
        val result = client.declaredException()
        await(result) must throwA[SomeException].like {
          case e: SomeException => e.getMessage must_== "an error occurred"
        }
      }

      "allow calling a method that throws an undeclared exception" in withJavaClient { client =>
        val result = client.runtimeException()
        await(result) must throwA[SoapFault].like {
          case e: SoapFault => e.getMessage must_== "an error occurred"
        }
      }
    }
  }

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def await[T](completionStage: CompletionStage[T]): T = await(FutureConverters.toScala(completionStage))

  def withScalaClient[T](block: MockServiceScala => T): T = withClient(block)

  def withJavaClient[T](block: MockServiceJava => T): T = withClient(block)

  def withClient[T, S](block: S => T)(implicit serviceClass: ClassTag[S]): T =  withService { port =>
    val factory = new PlayJaxWsProxyFactoryBean
    factory.setServiceClass(serviceClass.runtimeClass)
    factory.setAddress(s"http://localhost:$port/mockService")
    val client = factory.create().asInstanceOf[S]
    block(client)
  }

  def withService[T](block: Int => T): T = {
    val port = findAvailablePort()
    val endpoint = Endpoint.publish(s"http://localhost:$port/mockService", new MockServiceImpl)
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
