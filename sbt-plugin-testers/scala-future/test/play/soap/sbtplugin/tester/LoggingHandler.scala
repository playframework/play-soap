/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester

import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.soap.{SOAPMessageContext, SOAPHandler}

class LoggingHandler extends SOAPHandler[SOAPMessageContext] {
  def getHeaders = null

  def handleMessage(context: SOAPMessageContext) = {
    val outbound = context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)
      .asInstanceOf[java.lang.Boolean]
    val message = context.getMessage

    if (outbound) {
      println(s"Sending message:")
      message.writeTo(System.out)
    } else {
      val responseCode = context.get(MessageContext.HTTP_RESPONSE_CODE)
      println(s"Received $responseCode response:")
      message.writeTo(System.out)
    }
    println()
    true
  }

  def close(context: MessageContext) = ()

  def handleFault(context: SOAPMessageContext) = {
    println(s"Received fault:")
    context.getMessage.writeTo(System.out)
    println()
    true
  }
}
