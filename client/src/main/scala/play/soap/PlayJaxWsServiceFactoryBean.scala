/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.lang.reflect.Method

import org.apache.cxf.jaxws.support.JaxWsImplementorInfo
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean
import org.apache.cxf.service.model.OperationInfo

private[soap] class PlayJaxWsServiceFactoryBean extends JaxWsServiceFactoryBean {

  /**
   * Massive hack here.
   * We want to register Future as a holder type, to do this we add a custom AbstractServiceConfiguration.
   * However, JaxWsServiceFactorBean, whenever setJaxWsImplementorInfo is invoked, injects its own configuration
   * in the first place, and its isHolder method only returns true for javax.xml.ws.Holder, and does not return null
   * for other types (null is used to indicate to move to the next configuration in the chain).
   * Consequently, we must inject our configuration after setJaxWsImplementorInfo is invoked, otherwise it will take
   * no effect.
   */
  override def setJaxWsImplementorInfo(jaxWsImplementorInfo: JaxWsImplementorInfo) = {
    super.setJaxWsImplementorInfo(jaxWsImplementorInfo)

    getConfigurations.add(0, new PlayServiceConfiguration)
  }

  override def bindOperation(op: OperationInfo, method: Method) = super.bindOperation(op, method)
}
