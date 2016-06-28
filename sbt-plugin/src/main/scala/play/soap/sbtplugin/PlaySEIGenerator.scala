/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin

import java.io.Writer

import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator

/**
 * Generates the Service Endpoint Interface, ie the actual thing that gets called.
 */
class PlaySEIGenerator extends SEIGenerator with PlayGenerator {

  override def setCommonAttributes() = {
    super.setCommonAttributes()
    setPlayAttributes()
  }


  def setAttribute(name: String, value: AnyRef) = setAttributes(name, value)

  override def doWrite(templateName: String, outputs: Writer) = {

    // Override the template... it should only ever be sei.vm, but in case it's not.
    val newTemplate = if (templateName.endsWith("/sei.vm")) {
      "play/soap/sbtplugin/sei.vm"
    } else templateName

    // Add the future API to the velocity context.  The reason this must be done here is that the method that invokes
    // doWrite() first clears the context before invoking this.
    setAttributes("future", new FutureGenerator(env.get(classOf[Imports.WsdlKeys.FutureApi])))

    super.doWrite(newTemplate, outputs)
  }
}
