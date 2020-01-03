/*
 * Copyright (C) 2015-2020 Lightbend Inc. <https://www.lightbend.com>
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
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.reflect.ClassTag

import play.api.test._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

abstract class ServiceSpec extends PlaySpecification {

  /**
   * The type of the client class that's used to connect to the service.
   */
  type ServiceClient

  /**
   * The type of service.
   */
  type Service

  /**
   * The runtime type of the service client.
   */
  implicit val serviceClientClass: ClassTag[ServiceClient]

  /**
   * Get the service from the client.
   */
  def getServiceFromClient(c: ServiceClient): Service

  /**
   * Create an implementation of the service to run on the server.
   */
  def createServiceImpl(): Any

  /**
   * The path of the service. Omit the leading '/'.
   */
  val servicePath: String

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def withClient[T](block: Service => T): T = withApp { app =>
    val client = app.injector.instanceOf[ServiceClient]
    val service = getServiceFromClient(client)
    block(service)
  }

  def withApp[T](block: Application => T): T = withService { port =>
    implicit val app = new GuiceApplicationBuilder()
      .configure("play.soap.address" -> s"http://localhost:$port/$servicePath")
      .build
    Helpers.running(app) {
      block(app)
    }
  }


  def withService[T](block: Int => T): T = {
    val port = findAvailablePort()
    val impl = createServiceImpl()
    val endpoint = Endpoint.publish(s"http://localhost:$port/$servicePath", impl)
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
