// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;

abstract public class ConnectorConverter extends AbstractXmlElementConverter {

    private static final String CONNECTOR = "Connector";

    private static final String SCHEME = "scheme";

    private static final String REDIRECT_PORT = "redirectPort";

    private static final String PROXY_PORT = "proxyPort";

    private static final String PROXY_NAME = "proxyName";

    private static final String PORT = "port";

    private static final String MAX_THREADS = "maxThreads";

    private static final String CONNECTION_TIMEOUT = "connectionTimeout";

    private static final String ADDRESS = "address";

    protected void convert(Connector connector, Element connectorElement, Properties catalinaProperties) {
        final String address = parseProperties(connectorElement.getAttribute(ADDRESS), catalinaProperties);
        final String port = parseProperties(connectorElement.getAttribute(PORT), catalinaProperties);
        connector.setId(address + ":" + port);
        if (!(XmlElementConverter.EMPTY_STRING.equals(address))) {
            connector.setAddress(address);
        }
        String connectionTimeout = parseProperties(connectorElement.getAttribute(CONNECTION_TIMEOUT), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(connectionTimeout))) {
            try {
                connector.setConnectionTimeout(Long.valueOf(connectionTimeout));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONNECTOR, CONNECTION_TIMEOUT, connectionTimeout);
            }
        }
        String maxThreads = parseProperties(connectorElement.getAttribute(MAX_THREADS), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(maxThreads))) {
            try {
                connector.setMaxThreads(Long.valueOf(maxThreads));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONNECTOR, MAX_THREADS, maxThreads);
            }
        }
        if (!(XmlElementConverter.EMPTY_STRING.equals(port))) {
            try {
                connector.setPort(Long.valueOf(port));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONNECTOR, PORT, port);
            }
        }
        String proxyName = parseProperties(connectorElement.getAttribute(PROXY_NAME), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(proxyName))) {
            connector.setProxyName(proxyName);
        }
        String proxyPort = parseProperties(connectorElement.getAttribute(PROXY_PORT), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(proxyPort))) {
            try {
                connector.setProxyPort(Long.valueOf(proxyPort));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONNECTOR, PROXY_PORT, proxyPort);
            }
        }
        String redirectPort = parseProperties(connectorElement.getAttribute(REDIRECT_PORT), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(redirectPort))) {
            try {
                connector.setRedirectPort(Long.valueOf(redirectPort));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONNECTOR, REDIRECT_PORT, redirectPort);
            }
        }
        String scheme = parseProperties(connectorElement.getAttribute(SCHEME), catalinaProperties);
        if (!(XmlElementConverter.EMPTY_STRING.equals(scheme))) {
            connector.setScheme(scheme);
        }
    }

    protected void convert(Document document, Element connectorElement, Connector from, Properties catalinaProperties) {
        setAttribute(connectorElement, ADDRESS, from.getAddress(), catalinaProperties, false);
        setAttribute(connectorElement, CONNECTION_TIMEOUT, from.getConnectionTimeout(), catalinaProperties, false);
        setAttribute(connectorElement, MAX_THREADS, from.getMaxThreads(), catalinaProperties, false);
        setAttribute(connectorElement, PORT, from.getPort(), catalinaProperties, false);
        setAttribute(connectorElement, PROXY_NAME, from.getProxyName(), catalinaProperties, false);
        setAttribute(connectorElement, PROXY_PORT, from.getProxyPort(), catalinaProperties, false);
        setAttribute(connectorElement, REDIRECT_PORT, from.getRedirectPort(), catalinaProperties, false);
        setAttribute(connectorElement, SCHEME, from.getScheme(), catalinaProperties, false);
    }
}
