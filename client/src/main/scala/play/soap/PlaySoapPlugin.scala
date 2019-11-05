/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import javax.inject.Inject
import javax.inject.Singleton
import javax.xml.namespace.QName
import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.Handler

import org.apache.cxf.BusFactory
import org.apache.cxf.interceptor.LoggingOutInterceptor
import org.apache.cxf.interceptor.LoggingInInterceptor
import org.apache.cxf.transport.ConduitInitiatorManager
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduit
import org.apache.cxf.transport.http.asyncclient.AsyncHttpTransportFactory
import play.api._
import play.api.inject.ApplicationLifecycle

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * Abstract plugin extended by all generated SOAP clients.
 */
abstract class PlaySoapClient @Inject() (apacheCxfBus: ApacheCxfBus, configuration: Configuration) {
  private lazy val config                  = Configuration(configuration.underlying.getConfig("play.soap"))
  private lazy val serviceConfig           = config.getOptional[Configuration]("services." + this.getClass.getName)
  private def portConfig(portName: String) = serviceConfig.flatMap(_.getOptional[Configuration]("ports." + portName))
  private def readConfig[T](portName: String, read: Configuration => Option[T], default: T): T = {
    portConfig(portName)
      .flatMap(read)
      .orElse(serviceConfig.flatMap(read))
      .orElse(read(config))
      .getOrElse(default)
  }

  /**
   * Create a port for the given class
   *
   * @param qname The qname of the class
   * @param portName The name of the port
   * @param defaultAddress The default address to use if none configured
   * @param handlers The handlers to use
   * @return The port
   */
  protected def createPort[T](
      qname: QName,
      portName: String,
      defaultAddress: String,
      handlers: Handler[_ <: MessageContext]*
  )(implicit ct: ClassTag[T]): T = {
    val factory = createFactory

    if (readConfig(portName, _.getOptional[Boolean]("debugLog"), false)) {
      factory.getInInterceptors.add(new LoggingInInterceptor)
      factory.getOutInterceptors.add(new LoggingOutInterceptor)
    }
    factory.setServiceClass(ct.runtimeClass)
    val address = readConfig(portName, _.getOptional[String]("address"), defaultAddress)
    factory.setAddress(address)

    factory.setHandlers(handlers.asJava)

    val port = factory.create()

    port.asInstanceOf[T]
  }

  private def createFactory = {
    val factory = new PlayJaxWsProxyFactoryBean
    factory.setBus(apacheCxfBus.bus)
    factory
  }
}

/**
 * Configures and manages the lifecycle of an Apache CXF bus
 */
@Singleton
class ApacheCxfBus @Inject() (lifecycle: ApplicationLifecycle) extends Logging {
  private lazy val asyncTransport = new AsyncHttpTransportFactory
  private[soap] lazy val bus = {
    val bus = BusFactory.newInstance.createBus

    // Although Apache CXF will automatically select the async http transport conduit, we want to ensure that it will
    // use ours no matter what, so we do that here.  Note - in future we could replace this with one based on WS.
    val cim = bus.getExtension(classOf[ConduitInitiatorManager])
    cim.registerConduitInitiator("http://cxf.apache.org/transports/http", asyncTransport)
    cim.registerConduitInitiator("http://cxf.apache.org/transports/https", asyncTransport)
    cim.registerConduitInitiator("http://cxf.apache.org/transports/http/configuration", asyncTransport)
    cim.registerConduitInitiator("http://cxf.apache.org/transports/https/configuration", asyncTransport)

    bus.setProperty(AsyncHTTPConduit.USE_ASYNC, java.lang.Boolean.TRUE)

    bus
  }

  lifecycle.addStopHook { () =>
    bus.shutdown(true)

    // The AsyncHttpTransportFactory holds an AsyncHTTPConduitFactory, which holds a client which holds threads. There
    // is no way to shut this down without using reflection to get the conduit factory.
    try {
      val factoryField = classOf[AsyncHttpTransportFactory].getDeclaredField("factory")
      factoryField.setAccessible(true)
      val factory = factoryField.get(asyncTransport).asInstanceOf[AsyncHTTPConduitFactory]
      factory.shutdown()
    } catch {
      // Ignore, just print the stack trace so we know something has gone wrong
      case e: Exception =>
        logger.warn("Error shutting down CXF bus", e)
    }

    Future.successful(())
  }
}

private case class PortConfig(namespace: QName, address: Option[String], debugLog: Boolean)
