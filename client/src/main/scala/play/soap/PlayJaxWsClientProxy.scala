/*
 * Copyright (C) 2015-2020 Lightbend Inc. <https://www.lightbend.com>
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * This source file has been copied from
 * org.apache.cxf.jaxws.JaxWsClientProxy, converted to Scala,
 * and modified according to Play SOAP purposes.
 */
package play.soap

import java.io.Closeable
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.net.HttpURLConnection
import java.util.concurrent.CompletionStage
import javax.xml.soap.SOAPConstants
import javax.xml.soap.SOAPException
import javax.xml.soap.SOAPFault
import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.MessageContext.Scope
import javax.xml.ws.http.HTTPBinding
import javax.xml.ws.http.HTTPException
import javax.xml.ws.soap.SOAPBinding
import javax.xml.ws.soap.SOAPFaultException
import javax.xml.ws._

import org.apache.cxf.binding.soap.SoapFault
import org.apache.cxf.binding.soap.saaj.SAAJFactoryResolver
import org.apache.cxf.binding.soap.saaj.SAAJUtils
import org.apache.cxf.common.i18n.Message
import org.apache.cxf.common.logging.LogUtils
import org.apache.cxf.common.util.StringUtils
import org.apache.cxf.endpoint.Client
import org.apache.cxf.endpoint.ClientCallback
import org.apache.cxf.endpoint.Endpoint
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.helpers.CastUtils
import org.apache.cxf.interceptor.Fault
import org.apache.cxf.jaxws.EndpointReferenceBuilder
import org.apache.cxf.jaxws.context.WrappedMessageContext
import org.apache.cxf.jaxws.support.JaxWsEndpointImpl
import org.apache.cxf.service.invoker.MethodDispatcher
import java.util.Locale
import java.util.{ Map => JMap }

import org.apache.cxf.service.model.BindingOperationInfo
import org.w3c.dom.Node
import play.libs.F

import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Try

private[soap] object PlayJaxWsClientProxy {
  val log = LogUtils.getL7dLogger(classOf[PlayJaxWsClientProxy])

  @throws(classOf[SOAPException])
  def createSoapFault(binding: SOAPBinding, ex: Exception): SOAPFault = {
    Try(binding.getSOAPFactory.createFault)
      .orElse(Try(binding.getMessageFactory.createMessage.getSOAPPart.getEnvelope.getBody.addFault()))
      .toOption
      .map { soapFault1 =>
        ex match {
          case sf: SoapFault =>
            val soapFault =
              if (!(soapFault1.getNamespaceURI() == sf.getFaultCode.getNamespaceURI) &&
                  (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE == sf.getFaultCode.getNamespaceURI)) {
                Try(SAAJFactoryResolver.createSOAPFactory(null).createFault).getOrElse(soapFault1)
              } else soapFault1

            val isSoap11 = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE == soapFault.getNamespaceURI()

            if (StringUtils.isEmpty(sf.getLang)) {
              soapFault.setFaultString(sf.getReason)
            } else {
              soapFault.setFaultString(sf.getReason, stringToLocale(sf.getLang))
            }

            SAAJUtils.setFaultCode(soapFault, sf.getFaultCode)

            val role = sf.getRole
            if (role != null) {
              soapFault.setFaultActor(role)
            }

            if (sf.getSubCodes != null && !isSoap11) {
              import scala.collection.JavaConverters._
              for (fsc <- sf.getSubCodes.asScala) {
                soapFault.appendFaultSubcode(fsc)
              }
            }

            if (sf.hasDetails) {
              var nd: Node = soapFault.getOwnerDocument.importNode(sf.getDetail, true)
              nd = nd.getFirstChild
              soapFault.addDetail()
              while (nd != null) {
                val next: Node = nd.getNextSibling
                soapFault.getDetail.appendChild(nd)
                nd = next
              }
            }

            soapFault
          case _ =>
            val msg = ex.getMessage
            if (msg != null) {
              soapFault1.setFaultString(msg)
            }

            soapFault1
        }
      }
      .orNull
  }

  def stringToLocale(locale: String): Locale = {
    val parts = locale.split("-", 0)
    if (parts.length == 1) {
      new Locale(parts(0))
    } else if (parts.length == 2) {
      new Locale(parts(0), parts(1))
    } else {
      new Locale(parts(0), parts(1), parts(2))
    }
  }
}

private[soap] class PlayJaxWsClientProxy(c: Client, binding: Binding) extends ClientProxy(c) with BindingProvider {
  setupEndpointAddressContext(getClient.getEndpoint)
  private val builder = new EndpointReferenceBuilder(getClient.getEndpoint.asInstanceOf[JaxWsEndpointImpl])

  import PlayJaxWsClientProxy._

  override def invoke(proxy: AnyRef, method: Method, args: Array[AnyRef]): AnyRef = {
    if (client == null) {
      throw new IllegalStateException("The client has been closed.")
    }
    val endpoint   = getClient.getEndpoint
    val address    = endpoint.getEndpointInfo.getAddress
    val dispatcher = endpoint.getService.get(classOf[MethodDispatcher].getName).asInstanceOf[MethodDispatcher]

    val params = if (args == null) Array.empty[AnyRef] else args

    // The proxy returned by the factory bean implements a number of different interfaces, not just the service endpoint
    // interface. If the method invoked was declared by one of those interfaces, then we handle it here.
    try {
      if ((method.getDeclaringClass == classOf[BindingProvider]) || (method.getDeclaringClass == classOf[AnyRef]) || (method.getDeclaringClass == classOf[
            Closeable
          ])) {
        return method.invoke(this, params: _*)
      } else if (method.getDeclaringClass.isInstance(client)) {
        return method.invoke(client, params: _*)
      }
    } catch {
      case e: InvocationTargetException => throw e.getCause
    }

    val oi = dispatcher.getBindingOperation(method, endpoint)
    if (oi == null) {
      throw new WebServiceException(new Message("NO_BINDING_OPERATION_INFO", log, method.getName).toString)
    }

    client.getRequestContext.put(classOf[Method].getName, method)
    val result =
      try {
        val returnType: Class[_] = method.getReturnType
        if (returnType == classOf[Future[_]]) {
          invokeScalaFuture(method, oi, params)
        } else if (returnType == classOf[CompletionStage[_]]) {
          invokePlayJavaFuture(method, oi, params)
        } else {
          throw new WebServiceException(
            s"Can't invoke method with return type of $returnType, expected return type of scala.concurrent.Future or java.util.concurrent.CompletionStage"
          )
        }
      } catch {
        case wex: WebServiceException => throw wex
        case ex: Exception =>
          for (excls <- method.getExceptionTypes) {
            if (excls.isInstance(ex)) {
              throw ex
            }
          }
          if (ex.isInstanceOf[Fault] && ex.getCause.isInstanceOf[IOException]) {
            throw new WebServiceException(ex.getMessage, ex.getCause)
          }
          getBinding match {
            case http: HTTPBinding =>
              val exception = new HTTPException(HttpURLConnection.HTTP_INTERNAL_ERROR)
              exception.initCause(ex)
              throw exception
            case soap: SOAPBinding =>
              val soapFault = createSoapFault(getBinding.asInstanceOf[SOAPBinding], ex)
              if (soapFault == null) {
                throw new WebServiceException(ex)
              }
              val exception = new SOAPFaultException(soapFault)
              if (ex.isInstanceOf[Fault] && ex.getCause != null) {
                exception.initCause(ex.getCause)
              } else {
                exception.initCause(ex)
              }
              throw exception
            case _ =>
              throw new WebServiceException(ex)
          }
      } finally {
        if (addressChanged(address)) {
          setupEndpointAddressContext(getClient.getEndpoint)
        }
      }

    val respContext = client.getResponseContext
    val scopes      = CastUtils.cast(respContext.get(WrappedMessageContext.SCOPES).asInstanceOf[JMap[_, _]])
    if (scopes != null) {
      import scala.collection.JavaConverters._
      for (scope <- scopes.entrySet.asScala) {
        if (Scope.HANDLER.equals(scope.getValue)) {
          respContext.remove(scope.getKey)
        }
      }
    }
    adjustObject(result)
  }

  private def invokeScalaFuture(method: Method, oi: BindingOperationInfo, params: Array[AnyRef]): Future[Any] = {
    client.setExecutor(getClient.getEndpoint.getExecutor)

    val noResponseValue: Any = method.getGenericReturnType match {
      case parameterized: ParameterizedType =>
        parameterized.getActualTypeArguments.headOption match {
          case Some(clazz) if clazz == classOf[Unit] => ()
          case _                                     => null
        }
      case _ => null
    }

    val promise  = Promise[Any]()
    val callback = new PlayJaxwsClientCallback(promise, noResponseValue)
    client.invoke(callback, oi, params: _*)
    promise.future
  }

  private def invokePlayJavaFuture(
      method: Method,
      oi: BindingOperationInfo,
      params: Array[AnyRef]
  ): CompletionStage[Any] = {
    val future: Future[Any]                   = invokeScalaFuture(method, oi, params)
    val playJavaPromise: CompletionStage[Any] = FutureConverters.toJava(future)
    playJavaPromise
  }

  def getBinding = binding

  private def addressChanged(address: String): Boolean = {
    !(address == null ||
      getClient.getEndpoint.getEndpointInfo == null ||
      (address == getClient.getEndpoint.getEndpointInfo.getAddress))
  }

  private def setupEndpointAddressContext(endpoint: Endpoint): Unit = {
    if (endpoint != null && endpoint.getEndpointInfo.getAddress != null) {
      getRequestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint.getEndpointInfo.getAddress)
    }
  }

  override def getRequestContext: MessageContext = {
    new WrappedMessageContext(this.getClient.getRequestContext, null, Scope.APPLICATION)
  }

  override def getResponseContext = {
    new WrappedMessageContext(this.getClient.getResponseContext, null, Scope.APPLICATION)
  }

  def getEndpointReference: EndpointReference = {
    builder.getEndpointReference
  }

  def getEndpointReference[T <: EndpointReference](clazz: Class[T]): T = {
    builder.getEndpointReference(clazz)
  }
}
