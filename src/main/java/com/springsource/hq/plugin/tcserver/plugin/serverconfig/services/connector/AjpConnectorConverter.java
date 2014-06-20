// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;

public class AjpConnectorConverter extends ConnectorConverter implements XmlElementConverter<AjpConnector> {

    private static final String DEFAULT_PROTOCOL = "org.apache.coyote.ajp.AjpProtocol";

    private static final String PROTOCOL = "protocol";

    private static final String REQUEST_SECRET = "request.secret";

    private static final String REQUEST_USE_SECRET = "request.useSecret";

    public AjpConnector convert(Element element, Properties catalinaProperties) {
        final AjpConnector connector = new AjpConnector();
        convert(connector, element, catalinaProperties);
        String requestUseSecret = parseProperties(element.getAttribute(REQUEST_USE_SECRET), catalinaProperties);
        if (!(EMPTY_STRING.equals(requestUseSecret))) {
            connector.setRequestUseSecret(Boolean.valueOf(requestUseSecret));
        }
        String requestSecret = parseProperties(element.getAttribute(REQUEST_SECRET), catalinaProperties);
        if (!(EMPTY_STRING.equals(requestSecret))) {
            connector.setRequestSecret(requestSecret);
        }
        final String protocol = parseProperties(element.getAttribute(PROTOCOL), catalinaProperties);
        if ("AJP/1.3".equals(protocol)) {
            // If not FQN, can't properly tell if this is AJP/APR or AJP/Java.
            // Assume AJP/Java.
            connector.setProtocol(DEFAULT_PROTOCOL);
        } else {
            connector.setProtocol(protocol);
        }
        return connector;
    }

    public void convert(Document document, Element connectorElement, AjpConnector from, Properties catalinaProperties) {
        super.convert(document, connectorElement, from, catalinaProperties);
        setAttribute(connectorElement, REQUEST_SECRET, from.getRequestSecret(), catalinaProperties, false);
        setAttribute(connectorElement, REQUEST_USE_SECRET, from.getRequestUseSecret(), catalinaProperties, false);
        setAttribute(connectorElement, PROTOCOL, from.getProtocol(), catalinaProperties, true);
    }

}
