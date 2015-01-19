/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap

import javax.xml.namespace.QName

import org.apache.cxf.interceptor.{LoggingOutInterceptor, LoggingInInterceptor}
import play.api._

import scala.reflect.ClassTag

abstract class PlaySoapPlugin(app: Application) extends Plugin {

  private lazy val config = Configuration(app.configuration.underlying.getConfig("play.soap"))
  private lazy val debugLogDefault = {
    config.underlying.getBoolean("debugLog")
  }
  private lazy val configuredPorts: Map[QName, PortConfig] = {
    val configs = for {
      ports <- config.getConfig("ports").toSeq
      portKey <- ports.subKeys.toSeq
      portConfig <- ports.getConfig(portKey).toSeq
    } yield {
      val namespace = portConfig.getString("namespace") match {
        case Some(ns) => new QName(ns)
        case None => throw new IllegalArgumentException(s"play.soap.ports.$portKey must have a namespace property")
      }
      val debugLog = portConfig.getBoolean("debugLog").getOrElse(debugLogDefault)
      val address = portConfig.getString("address")
      namespace -> PortConfig(namespace, address, debugLog)
    }
    configs.toMap
  }

  protected def createPort[T](qname: QName, portName: String, defaultAddress: String)(implicit ct: ClassTag[T]): T = {
    val factory = new PlayJaxWsProxyFactoryBean
    if (shouldLog(qname)) {
      factory.getInInterceptors.add(new LoggingInInterceptor)
      factory.getOutInterceptors.add(new LoggingOutInterceptor)
    }
    factory.setServiceClass(ct.runtimeClass)
    val address = configuredPorts.get(qname)
      .flatMap(_.address)
      .getOrElse(defaultAddress)
    factory.setAddress(address)
    factory.create().asInstanceOf[T]
  }

  private def shouldLog(qname: QName) = configuredPorts.get(qname).fold(debugLogDefault)(_.debugLog)
}

private case class PortConfig(namespace: QName, address: Option[String], debugLog: Boolean)

