/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.sbtplugin

import org.apache.cxf.tools.common.model.JavaModel
import org.apache.cxf.tools.common.{ToolConstants, ToolContext}
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.AbstractJAXWSGenerator
import org.apache.cxf.tools.wsdlto.frontend.jaxws.processor.WSDLToJavaProcessor

import java.util.{Map => JMap}
import scala.collection.JavaConversions._

/**
 * Generator for play.plugins entries
 */
class PlayPluginsGenerator extends AbstractJAXWSGenerator with PlayGenerator {
  override def setCommonAttributes() = {
    super.setCommonAttributes()
    setPlayAttributes()
  }

  def setAttribute(name: String, value: AnyRef) = setAttributes(name, value)

  def passthrough() = {
    // Same passthrough condition as the ServiceGenerator
    if (env.optionSet(ToolConstants.CFG_GEN_SERVICE) || env.optionSet(ToolConstants.CFG_ALL)) {
      false
    } else if (env.optionSet(ToolConstants.CFG_GEN_ANT) || env.optionSet(ToolConstants.CFG_GEN_TYPES) || env.optionSet(ToolConstants.CFG_GEN_CLIENT) || env.optionSet(ToolConstants.CFG_GEN_IMPL) || env.optionSet(ToolConstants.CFG_GEN_SEI) || env.optionSet(ToolConstants.CFG_GEN_SERVER) || env.optionSet(ToolConstants.CFG_GEN_FAULT)) {
      true
    } else false
  }

  def generate(penv: ToolContext) = {
    this.env = penv
    if (!passthrough) {
      val map = penv.get(WSDLToJavaProcessor.MODEL_MAP).asInstanceOf[JMap[_, JavaModel]]
      val serviceClasses = map.values.flatMap(_.getServiceClasses.values().toSeq).map(_.getFullClassName)
      // Don't write to file, just store in env to be retrieved later
      env.put("play.plugins", serviceClasses)
    }
  }
}
