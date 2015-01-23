/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap

import javax.xml.namespace.QName
import javax.xml.ws.handler.{MessageContext, Handler}

import org.apache.cxf.BusFactory
import org.apache.cxf.interceptor.{LoggingOutInterceptor, LoggingInInterceptor}
import org.apache.cxf.transport.ConduitInitiatorManager
import org.apache.cxf.transport.http.asyncclient.{AsyncHTTPConduitFactory, AsyncHTTPConduit, AsyncHttpTransportFactory}
import play.api._

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
 * Abstract plugin extended by all generated SOAP clients
 */
abstract class PlaySoapPlugin(app: Application) extends Plugin {

  private lazy val config = Configuration(app.configuration.underlying.getConfig("play.soap"))
  private lazy val serviceConfig = config.getConfig("services." + this.getClass.getName)
  private def portConfig(portName: String) = serviceConfig.flatMap(_.getConfig("ports." + portName))
  private def readConfig[T](portName: String, read: Configuration => Option[T], default: T): T = {
    portConfig(portName).flatMap(read)
      .orElse(serviceConfig.flatMap(read))
      .orElse(read(config))
      .getOrElse(default)
  }

  protected def createPort[T](qname: QName, portName: String, defaultAddress: String, handlers: Handler[_ <: MessageContext]*)(implicit ct: ClassTag[T]): T = {

    val factory = createFactory

    if (readConfig(portName, _.getBoolean("debugLog"), false)) {
      factory.getInInterceptors.add(new LoggingInInterceptor)
      factory.getOutInterceptors.add(new LoggingOutInterceptor)
    }
    factory.setServiceClass(ct.runtimeClass)
    val address = readConfig(portName, _.getString("address"), defaultAddress)
    factory.setAddress(address)

    factory.setHandlers(handlers.asJava)

    val port = factory.create()

    port.asInstanceOf[T]
  }

  private def createFactory = {
    app.plugin[ApacheCxfBusPlugin] match {
      case Some(plugin) =>
        val factory = new PlayJaxWsProxyFactoryBean
        factory.setBus(plugin.bus)
        factory
      case None =>
        throw new IllegalStateException("play.soap.ApacheCxfBusPlugin is not enabled!")
    }
  }
}

/**
 * Configures and manages the lifecycle of an Apache CXF bus
 */
class ApacheCxfBusPlugin(app: Application) extends Plugin {

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

  override def onStop() = {
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
      case e: Exception => e.printStackTrace()
    }
  }

}

private case class PortConfig(namespace: QName, address: Option[String], debugLog: Boolean)

