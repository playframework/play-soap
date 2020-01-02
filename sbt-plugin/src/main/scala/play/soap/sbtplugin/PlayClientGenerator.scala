/*
 * Copyright (C) 2015-2020 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin

import java.io.Writer

import org.apache.cxf.tools.common.model.JavaPort
import org.apache.cxf.tools.util.ClassCollector
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.ServiceGenerator

/**
 * Generator for the Play plugin
 */
class PlayClientGenerator extends ServiceGenerator with PlayGenerator {
  override def setCommonAttributes() = {
    super.setCommonAttributes()
    setPlayAttributes()
  }

  def setAttribute(name: String, value: AnyRef) = setAttributes(name, value)

  override def doWrite(templateName: String, outputs: Writer) = {
    // Override the template... it should only ever be sei.vm, but in case it's not.
    val newTemplate = if (templateName.endsWith("/service.vm")) {
      "play/soap/sbtplugin/client.vm"
    } else templateName

    setAttribute("portMethod", PortMethodNameGenerator)

    super.doWrite(newTemplate, outputs)
  }

  /**
   * Overridden to make the output name Scala instead of Java.
   */
  override def parseOutputName(packageName: String, filename: String) = {
    register(env.get(classOf[ClassCollector]), packageName, filename)
    parseOutputName(packageName, filename, ".scala")
  }

  private object PortMethodNameGenerator {
    def transform(port: JavaPort): String = {
      port.getName.head.toLower + port.getName.tail
    }
  }
}
