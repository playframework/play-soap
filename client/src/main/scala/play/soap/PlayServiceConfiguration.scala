/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.lang.reflect.{ Method, ParameterizedType, Type }

import org.apache.cxf.wsdl.service.factory.AbstractServiceConfiguration
import java.util.concurrent.CompletionStage

import scala.concurrent.Future

import java.lang.Boolean.FALSE

private[soap] class PlayServiceConfiguration extends AbstractServiceConfiguration {

  /**
   * We say future/promise is a holder type so that JAXB will create bindings for the inner type, not the outer.
   */
  override def isHolder(cls: Class[_], `type`: Type) =
    if (classOf[Future[_]] == cls || classOf[CompletionStage[_]] == cls) java.lang.Boolean.TRUE else null

  override def getHolderType(cls: Class[_], `type`: Type) = {
    if (classOf[Future[_]] == cls || classOf[CompletionStage[_]] == cls) {
      `type` match {
        case p: ParameterizedType => p.getActualTypeArguments()(0)
      }
    } else null
  }

  /**
   * Neither scala.Unit or java.lang.Void are output types
   */
  override def hasOutMessage(m: Method) = {
    m.getGenericReturnType match {
      case future: ParameterizedType if future.getRawType == classOf[Future[_]] ||
        future.getRawType == classOf[CompletionStage[_]] =>
        future.getActualTypeArguments.headOption match {
          case Some(unit) if unit == classOf[Unit] || unit == classOf[Void] => FALSE
          case _ => null
        }
      case _ => null
    }
  }
}
