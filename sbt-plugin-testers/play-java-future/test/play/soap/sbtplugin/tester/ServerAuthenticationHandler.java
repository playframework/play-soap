/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.sbtplugin.tester;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

public class ServerAuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!outbound) {
            // Check the Authentication-Token in the HTTP request headers
            Map<String, List<String>> headers = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);

            List<String> authTokenHeader = headers.get("Authentication-Token");
            String authToken = null;
            if (authTokenHeader != null && authTokenHeader.size() > 0) {
                authToken = authTokenHeader.get(0);
            }

            if (authToken == null || !authToken.equals("somesecret")) {
                try {
                    SOAPBody soapBody = context.getMessage().getSOAPBody();

                    // Clear the body
                    soapBody.removeContents();
                    // And add a fault to it
                    SOAPFault fault = soapBody.addFault();
                    fault.setFaultString("Bad or missing authentication token");

                    return false;
                } catch (SOAPException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }
}