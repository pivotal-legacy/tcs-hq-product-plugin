// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;

public class HttpConnectorConverter extends ConnectorConverter implements XmlElementConverter<HttpConnector> {

    private static final String HTTP_CONNECTOR = "Http Connector";

    private static final String SSL_ENABLED = "SSLEnabled";

    private static final String SECURE = "secure";

    private static final String PROTOCOL = "protocol";

    private static final String MAX_KEEP_ALIVE_REQUESTS = "maxKeepAliveRequests";

    private static final String KEYSTORE_PASS = "keystorePass";

    private static final String KEYSTORE_FILE = "keystoreFile";

    private static final String KEY_ALIAS = "keyAlias";

    private static final String ALGORITHM = "algorithm";

    private static final String ACCEPT_COUNT = "acceptCount";

    public HttpConnector convert(Element connectorElement, Properties catalinaProperties) {
        final HttpConnector connector = new HttpConnector();
        convert(connector, connectorElement, catalinaProperties);
        final String protocol = parseProperties(connectorElement.getAttribute(PROTOCOL), catalinaProperties);
        connector.setProtocol(protocol);

        String acceptCount = parseProperties(connectorElement.getAttribute(ACCEPT_COUNT), catalinaProperties);
        if (!(EMPTY_STRING.equals(acceptCount))) {
            try {
                connector.setAcceptCount(Long.valueOf(acceptCount));
            } catch (NumberFormatException e) {
                logNonNumericValue(HTTP_CONNECTOR, ACCEPT_COUNT, acceptCount);
            }
        }
        String algorithm = parseProperties(connectorElement.getAttribute(ALGORITHM), catalinaProperties);
        if (!(EMPTY_STRING.equals(algorithm))) {
            connector.setAlgorithm(algorithm);
        }
        String keyAlias = parseProperties(connectorElement.getAttribute(KEY_ALIAS), catalinaProperties);
        if (!(EMPTY_STRING.equals(keyAlias))) {
            connector.setKeyAlias(keyAlias);
        }
        String keystoreFile = parseProperties(connectorElement.getAttribute(KEYSTORE_FILE), catalinaProperties);
        if (!(EMPTY_STRING.equals(keystoreFile))) {
            connector.setKeystoreFile(keystoreFile);
        }
        String keystorePass = parseProperties(connectorElement.getAttribute(KEYSTORE_PASS), catalinaProperties);
        if (!(EMPTY_STRING.equals(keystorePass))) {
            connector.setKeystorePass(keystorePass);
        }
        String maxKeepAliveRequests = parseProperties(connectorElement.getAttribute(MAX_KEEP_ALIVE_REQUESTS), catalinaProperties);
        if (!(EMPTY_STRING.equals(maxKeepAliveRequests))) {
            try {
                connector.setMaxKeepAliveRequests(Long.valueOf(maxKeepAliveRequests));
            } catch (NumberFormatException e) {
                logNonNumericValue(HTTP_CONNECTOR, MAX_KEEP_ALIVE_REQUESTS, maxKeepAliveRequests);
            }
        }
        String secure = parseProperties(connectorElement.getAttribute(SECURE), catalinaProperties);
        if (!(EMPTY_STRING.equals(secure))) {
            connector.setSecure(Boolean.valueOf(secure));
        }
        String SSLEnabled = parseProperties(connectorElement.getAttribute(SSL_ENABLED), catalinaProperties);
        if (!(EMPTY_STRING.equals(SSLEnabled))) {
            connector.setSSLEnabled(Boolean.valueOf(SSLEnabled));
        }

        return connector;
    }

    public void convert(Document document, Element connectorElement, HttpConnector from, Properties catalinaProperties) {
        super.convert(document, connectorElement, from, catalinaProperties);

        setAttribute(connectorElement, PROTOCOL, from.getProtocol(), catalinaProperties, true);
        setAttribute(connectorElement, ACCEPT_COUNT, from.getAcceptCount(), catalinaProperties, false);
        setAttribute(connectorElement, ALGORITHM, from.getAlgorithm(), catalinaProperties, false);
        setAttribute(connectorElement, KEY_ALIAS, from.getKeyAlias(), catalinaProperties, false);
        setAttribute(connectorElement, KEYSTORE_FILE, from.getKeystoreFile(), catalinaProperties, false);
        setAttribute(connectorElement, KEYSTORE_PASS, from.getKeystorePass(), catalinaProperties, false);
        setAttribute(connectorElement, MAX_KEEP_ALIVE_REQUESTS, from.getMaxKeepAliveRequests(), catalinaProperties, false);
        setAttribute(connectorElement, SECURE, from.getSecure(), catalinaProperties, false);
        setAttribute(connectorElement, SSL_ENABLED, from.getSSLEnabled(), catalinaProperties, false);
    }
}
