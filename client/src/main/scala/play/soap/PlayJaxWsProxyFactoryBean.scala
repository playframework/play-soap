/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
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
 * org.apache.cxf.jaxws.JaxWsProxyFactoryBean, converted to Scala,
 * and modified according to Play SOAP purposes.
 */
package play.soap

import java.io.Closeable
import java.lang.reflect.{ParameterizedType, Type, Proxy}
import java.util
import javax.xml.namespace.QName
import javax.xml.ws.BindingProvider
import javax.xml.ws.handler.{MessageContext, Handler}

import org.apache.cxf.common.classloader.ClassLoaderUtils
import org.apache.cxf.common.injection.ResourceInjector
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.{ClientProxy, ClientProxyFactoryBean}
import org.apache.cxf.jaxws.context.WebServiceContextResourceResolver
import org.apache.cxf.jaxws.handler.AnnotationHandlerChainBuilder
import org.apache.cxf.jaxws.interceptors.{HolderOutInterceptor, WrapperClassOutInterceptor, HolderInInterceptor, WrapperClassInInterceptor}
import org.apache.cxf.jaxws.{JaxWsClientFactoryBean}
import org.apache.cxf.jaxws.support.{JaxWsImplementorInfo, JaxWsEndpointImpl, JaxWsServiceFactoryBean}
import org.apache.cxf.resource.{ResourceResolver, DefaultResourceManager, ResourceManager}
import org.apache.cxf.service.Service
import org.apache.cxf.service.model.{MessagePartInfo, ServiceInfo}
import org.apache.cxf.wsdl.service.factory.{AbstractServiceConfiguration, ReflectionServiceFactoryBean}

import java.util.{ List => JList }

import scala.concurrent.Future

/**
 * Most of this code is copied from the Apache CXF JaxWsProxyFactoryBean
 */
private[soap] class PlayJaxWsProxyFactoryBean extends ClientProxyFactoryBean(new JaxWsClientFactoryBean()) {
  private var handlers: JList[Handler[_ <: MessageContext]] = new util.ArrayList[Handler[_ <: MessageContext]]
  private var loadHandlers: Boolean = true

  setServiceFactory(new PlayJaxWsServiceFactoryBean())

  protected override def getConfiguredName: String = {
    var name: QName = getEndpointName
    if (name == null) {
      val sfb: JaxWsServiceFactoryBean = getClientFactoryBean.getServiceFactory.asInstanceOf[JaxWsServiceFactoryBean]
      name = sfb.getJaxWsImplementorInfo.getEndpointName
    }
    "play." + name + ".jaxws-client.proxyFactory"
  }

  /**
   * Specifies a list of JAX-WS Handler implementations that are to be
   * used by the proxy.
   *
   * @param h a <code>List</code> of <code>Handler</code> objects
   */
  def setHandlers(h: JList[Handler[_ <: MessageContext]]) {
    handlers.clear()
    handlers.addAll(h)
  }

  /**
   * Returns the configured list of JAX-WS handlers for the proxy.
   *
   * @return a <code>List</code> of <code>Handler</code> objects
   */
  def getHandlers: JList[Handler[_ <: MessageContext]] = {
    handlers
  }

  def setLoadHandlers(b: Boolean) {
    loadHandlers = b
  }

  def isLoadHandlers: Boolean = {
    loadHandlers
  }

  protected override def clientClientProxy(c: Client): ClientProxy = {
    val cp = new PlayJaxWsClientProxy(c, c.getEndpoint.asInstanceOf[JaxWsEndpointImpl].getJaxwsBinding)
    cp.getRequestContext.putAll(this.getProperties)
    buildHandlerChain(cp)
    cp
  }

  protected override def getImplementingClasses: Array[Class[_]] = {
    val cls = getClientFactoryBean.getServiceClass
    Array(cls, classOf[BindingProvider], classOf[Closeable], classOf[Client])
  }

  /**
   * Creates a JAX-WS proxy that can be used to make remote invocations.
   *
   * @return the proxy. You must cast the returned object to the approriate class
   *         before making remote calls
   */
  override def create: AnyRef = {

    var orig: ClassLoaderUtils.ClassLoaderHolder = null
    try {
      if (getBus != null) {
        val loader: ClassLoader = getBus.getExtension(classOf[ClassLoader])
        if (loader != null) {
          orig = ClassLoaderUtils.setThreadContextClassloader(loader)
        }
      }
      val obj: AnyRef = super.create

      val service: Service = getServiceFactory.getService
      if (needWrapperClassInterceptor(service.getServiceInfos.get(0))) {
        val in = super.getInInterceptors
        val out = super.getOutInterceptors
        in.add(new WrapperClassInInterceptor)
        in.add(new HolderInInterceptor)
        out.add(new WrapperClassOutInterceptor)
        out.add(new HolderOutInterceptor)
      }

      obj
    } finally {
      if (orig != null) {
        orig.reset()
      }
    }
  }

  private def needWrapperClassInterceptor(serviceInfo: ServiceInfo): Boolean = {
    if (serviceInfo == null) {
      return false
    }
    import scala.collection.JavaConverters._
    for (opInfo <- serviceInfo.getInterface.getOperations.asScala) {
      if (opInfo.isUnwrappedCapable && opInfo.getProperty(ReflectionServiceFactoryBean.WRAPPERGEN_NEEDED) != null) {
        return true
      }
    }
    return false
  }

  private def buildHandlerChain(cp: PlayJaxWsClientProxy): Unit = {
    val builder: AnnotationHandlerChainBuilder = new AnnotationHandlerChainBuilder
    val sf: JaxWsServiceFactoryBean = getServiceFactory.asInstanceOf[JaxWsServiceFactoryBean]
    val chain: JList[Handler[_ <: MessageContext]] = new util.ArrayList[Handler[_ <: MessageContext]](handlers)
    if (loadHandlers) {
      chain.addAll(builder.buildHandlerChainFromClass(sf.getServiceClass, sf.getEndpointInfo.getName, sf.getServiceQName, this.getBindingId))
    }
    if (!chain.isEmpty) {
      var resourceManager: ResourceManager = getBus.getExtension(classOf[ResourceManager])
      val resolvers: JList[ResourceResolver] = resourceManager.getResourceResolvers
      resourceManager = new DefaultResourceManager(resolvers)
      resourceManager.addResourceResolver(new WebServiceContextResourceResolver)
      val injector: ResourceInjector = new ResourceInjector(resourceManager)
      import scala.collection.JavaConverters._
      for (h <- chain.asScala) {
        if (Proxy.isProxyClass(h.getClass) && getServiceClass != null) {
          injector.inject(h, getServiceClass)
          injector.construct(h, getServiceClass)
        }
        else {
          injector.inject(h)
          injector.construct(h)
        }
      }
    }
    cp.getBinding.setHandlerChain(chain)
  }
}
