/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.lang.reflect.Method
import javax.xml.ws.{FaultAction, Action}

import org.apache.cxf.jaxws.support.{JaxWsImplementorInfo, JaxWsServiceFactoryBean}
import org.apache.cxf.service.model.{OperationInfo, InterfaceInfo}

private[soap] class PlayJaxWsServiceFactoryBean extends JaxWsServiceFactoryBean {

  /**
   * Since we're returning futures, we don't set a throws clause, because the method doesn't throw anything,
   * it redeems its returned future with a failure.  This means though that the automatic binding doesn't detect the
   * faults.  So, instead, we add them as explicit action faults, and bind them here.
   */
  override def initializeFaults(service: InterfaceInfo, op: OperationInfo, method: Method) = {
    // Ignore the declared faults
    val faults = Option(method.getAnnotation(classOf[Action])).fold(Array.empty[FaultAction])(_.fault())
    faults.foreach { fault =>
      addFault(service, op, fault.className())
    }
  }

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
