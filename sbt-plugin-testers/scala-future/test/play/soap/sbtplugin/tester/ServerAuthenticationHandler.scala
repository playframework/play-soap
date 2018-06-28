/*
 * Copyright (C) 2015-2018 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester

import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.soap.{SOAPMessageContext, SOAPHandler}

import java.util.{Map => JMap, List => JList}
import scala.collection.JavaConverters._

class ServerAuthenticationHandler extends SOAPHandler[SOAPMessageContext] {
  def getHeaders = null

  def handleMessage(context: SOAPMessageContext) = {

    // If this is an inbound message
    if (!context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)
      .asInstanceOf[java.lang.Boolean]) {

      // Check the Authentication-Token in the HTTP request headers
      val headers = context.get(MessageContext.HTTP_REQUEST_HEADERS)
        .asInstanceOf[JMap[String, JList[String]]]

      Option(headers.get("Authentication-Token"))
        .flatMap(_.headOption)
        .collect { case  "somesecret" => true }
        .getOrElse {
        val soapBody = context.getMessage.getSOAPBody

        // Clear the body
        soapBody.removeContents()
        // And add a fault to it
        val fault = soapBody.addFault()
        fault.setFaultString("Bad or missing authentication token")

        false
      }

    } else {
      true
    }
  }

  def close(context: MessageContext) = ()

  def handleFault(context: SOAPMessageContext) = true
}
