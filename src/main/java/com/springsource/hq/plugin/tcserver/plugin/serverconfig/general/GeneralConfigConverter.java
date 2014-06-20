/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.general;

import java.util.List;
import java.util.Properties;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.AprLifecycleListener;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.JmxListener;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.ServerProperties;

/**
 * 
 * TODO Document GeneralConfigConverter
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of GeneralConfigConverter
 * 
 * @author Greg Turnquist
 * 
 */
public class GeneralConfigConverter extends AbstractXmlElementConverter implements XmlElementConverter<GeneralConfig> {

    private static final String TAG_NAME_LISTENER = "Listener";

    private static final String ATTRIBUTE_CLASS_NAME = "className";

    private static final String CLASS_NAME_APR_LIFECYCLE_LISTENER = "org.apache.catalina.core.AprLifecycleListener";

    private static final String SHUTDOWN = "shutdown";

    private static final String PORT = "port";

    private static final String ACCESS_FILE = "accessFile";

    private static final String AUTHENTICATE = "authenticate";

    private static final String BIND = "bind";

    private static final String CIPHER_SUITES = "cipherSuites";

    private static final String CLIENT_AUTH = "clientAuth";

    private static final String KEYSTORE_FILE = "keystoreFile";

    private static final String KEYSTORE_PASS = "keystorePass";

    private static final String PASSOWRD_FILE = "passwordFile";

    private static final String JMX_PORT = "port";

    private static final String PROTOCOLS = "protocols";

    private static final String TRUSTSTORE_FILE = "truststoreFile";

    private static final String TRUSTSTORE_PASS = "truststorePass";

    private static final String USE_JDK_CLIENT_FACTORY = "useJdkClientFactory";

    private static final String USE_SSL = "useSSL";

    public GeneralConfig convert(final Element server, final Properties catalinaProperties) {
        final GeneralConfig generalConfig = new GeneralConfig();
        generalConfig.setServerProperties(createServerProperties(server, catalinaProperties));
        generalConfig.setJmxListener(createJmxListener(server, catalinaProperties));
        generalConfig.setAprLifecycleListener(createAprLifecycleListener(server));
        return generalConfig;
    }

    private AprLifecycleListener createAprLifecycleListener(Element server) {

        List<Element> listeners = DomUtils.getChildElementsByTagName(server, TAG_NAME_LISTENER);
        for (Element listener : listeners) {
            if (isAprLifecycleListener(listener)) {
                return new AprLifecycleListener();
            }
        }
        return null;
    }

    /**
     * @param listener
     * @return
     */
    private boolean isAprLifecycleListener(Element listener) {
        return CLASS_NAME_APR_LIFECYCLE_LISTENER.equals(listener.getAttribute(ATTRIBUTE_CLASS_NAME));
    }

    public void convert(Document document, Element server, GeneralConfig from, Properties catalinaProperties) {
        setAttribute(server, PORT, from.getServerProperties().getPort(), catalinaProperties, true);
        setAttribute(server, SHUTDOWN, from.getServerProperties().getShutdown(), catalinaProperties, true);
        convertJmxListener(document, server, from.getJmxListener(), catalinaProperties);
        convertAprLifecycleListener(document, server, from.getAprLifecycleListener());
    }

    /**
     * This method sifts through server.xml to find the AprLifecycleListener. If it exists, but is not defined in the
     * locally cached copy of GeneralConfig, it is stripped out when pushing a new copy of server.xml. If it exists,
     * then the className is forced to the correct fully qualified class name. If it doesn't exist, and IS defined in
     * GeneralConfig, it is added to server.xml.
     */
    private void convertAprLifecycleListener(Document document, Element server, AprLifecycleListener configuredAprLifecycleListener) {
        Element aprLifecycleListenerXmlElementInServerXml = null;
        Element lastListener = null;
        boolean foundAprLifecycleXmlElementInServerXml = false;

        List<Element> listeners = DomUtils.getChildElementsByTagName(server, TAG_NAME_LISTENER);

        for (Element listener : listeners) {
            lastListener = listener; // We need to catch the last listener, so we can insert before its nextSibling.
            if (isAprLifecycleListener(listener)) {
                foundAprLifecycleXmlElementInServerXml = true;
                aprLifecycleListenerXmlElementInServerXml = listener;
                if (configuredAprLifecycleListener == null) {
                    server.removeChild(aprLifecycleListenerXmlElementInServerXml);
                }
            }
        }

        if (configuredAprLifecycleListener != null) {
            if (!foundAprLifecycleXmlElementInServerXml) {
                aprLifecycleListenerXmlElementInServerXml = document.createElement(TAG_NAME_LISTENER);
            }
            aprLifecycleListenerXmlElementInServerXml.setAttribute(ATTRIBUTE_CLASS_NAME, CLASS_NAME_APR_LIFECYCLE_LISTENER);
            if (!foundAprLifecycleXmlElementInServerXml) {
                if (lastListener != null) {
                    // Strangely, there doesn't appear to be any sort of insertAfter() call, so we have to fetch the
                    // next sibling.
                    server.insertBefore(aprLifecycleListenerXmlElementInServerXml, lastListener.getNextSibling());
                } else {
                    Element globalNamingResources = DomUtils.getChildElementByTagName(server, "GlobalNamingResources");
                    server.insertBefore(aprLifecycleListenerXmlElementInServerXml, globalNamingResources);
                }

            }
        }
    }

    private void convertJmxListener(Document document, Element server, JmxListener from, Properties catalinaProperties) {
        Element jmxListener = null;
        boolean foundJmxListener = false;
        for (Element listener : DomUtils.getChildElementsByTagName(server, TAG_NAME_LISTENER)) {
            if ("com.springsource.tcserver.serviceability.rmi.JmxSocketListener".equals(listener.getAttribute(ATTRIBUTE_CLASS_NAME))) {
                foundJmxListener = true;
                jmxListener = listener;
                if (!from.getEnabled()) {
                    server.removeChild(jmxListener);
                }
            }
        }
        if (from.getEnabled()) {
            if (!foundJmxListener) {
                jmxListener = document.createElement(TAG_NAME_LISTENER);
                jmxListener.setAttribute(ATTRIBUTE_CLASS_NAME, "com.springsource.tcserver.serviceability.rmi.JmxSocketListener");
            }
            setAttribute(jmxListener, ACCESS_FILE, from.getAccessFile(), catalinaProperties, true);
            if (from.getAuthenticate() != null) {
                setAttribute(jmxListener, AUTHENTICATE, from.getAuthenticate(), catalinaProperties, true);
            }
            setAttribute(jmxListener, BIND, from.getBind(), catalinaProperties, true);
            setAttribute(jmxListener, CIPHER_SUITES, from.getCipherSuites(), catalinaProperties, true);
            if (from.getClientAuth() != null) {
                setAttribute(jmxListener, CLIENT_AUTH, from.getClientAuth(), catalinaProperties, true);
            }
            setAttribute(jmxListener, KEYSTORE_FILE, from.getKeystoreFile(), catalinaProperties, true);
            setAttribute(jmxListener, KEYSTORE_PASS, from.getKeystorePass(), catalinaProperties, true);
            setAttribute(jmxListener, PASSOWRD_FILE, from.getPasswordFile(), catalinaProperties, true);
            if (from.getPort() != null) {
                setAttribute(jmxListener, JMX_PORT, from.getPort(), catalinaProperties, true);
            }
            setAttribute(jmxListener, PROTOCOLS, from.getProtocols(), catalinaProperties, true);
            setAttribute(jmxListener, TRUSTSTORE_FILE, from.getTruststoreFile(), catalinaProperties, true);
            setAttribute(jmxListener, TRUSTSTORE_PASS, from.getTruststorePass(), catalinaProperties, true);
            if (from.getUseJdkClientFactory() != null) {
                setAttribute(jmxListener, USE_JDK_CLIENT_FACTORY, from.getUseJdkClientFactory(), catalinaProperties, true);
            }
            if (from.getUseSSL() != null) {
                setAttribute(jmxListener, USE_SSL, from.getUseSSL(), catalinaProperties, true);
            }
            if (!foundJmxListener) {
                server.appendChild(jmxListener);
            }
        }
    }

    private ServerProperties createServerProperties(final Element server, final Properties catalinaProperties) {
        final ServerProperties serverProperties = new ServerProperties();
        String port = parseProperties(server.getAttribute(PORT), catalinaProperties);
        if (!(EMPTY_STRING.equals(port))) {
            try {
                serverProperties.setPort(Long.valueOf(port));
            } catch (NumberFormatException e) {
                logNonNumericValue("Server", PORT, port);
            }
        }
        String shutdown = parseProperties(server.getAttribute(SHUTDOWN), catalinaProperties);
        if (!(EMPTY_STRING.equals(shutdown))) {
            serverProperties.setShutdown(shutdown);
        }
        return serverProperties;
    }

    private JmxListener createJmxListener(final Element server, final Properties catalinaProperties) {
        JmxListener jmxListener = new JmxListener();
        for (Element listener : DomUtils.getChildElementsByTagName(server, TAG_NAME_LISTENER)) {
            if ("com.springsource.tcserver.serviceability.rmi.JmxSocketListener".equals(listener.getAttribute(ATTRIBUTE_CLASS_NAME))) {
                jmxListener.setEnabled(true);
                String accessFile = parseProperties(listener.getAttribute(ACCESS_FILE), catalinaProperties);
                if (!EMPTY_STRING.equals(accessFile)) {
                    jmxListener.setAccessFile(accessFile);
                }
                String authenticate = parseProperties(listener.getAttribute(AUTHENTICATE), catalinaProperties);
                if (!EMPTY_STRING.equals(authenticate)) {
                    jmxListener.setAuthenticate(Boolean.valueOf(authenticate));
                }
                String bind = parseProperties(listener.getAttribute(BIND), catalinaProperties);
                if (!EMPTY_STRING.equals(bind)) {
                    jmxListener.setBind(bind);
                }
                String cipherSuites = parseProperties(listener.getAttribute(CIPHER_SUITES), catalinaProperties);
                if (!EMPTY_STRING.equals(cipherSuites)) {
                    jmxListener.setCipherSuites(cipherSuites);
                }
                String clientAuth = parseProperties(listener.getAttribute(CLIENT_AUTH), catalinaProperties);
                if (!EMPTY_STRING.equals(clientAuth)) {
                    jmxListener.setClientAuth(Boolean.valueOf(clientAuth));
                }
                String keystoreFile = parseProperties(listener.getAttribute(KEYSTORE_FILE), catalinaProperties);
                if (!EMPTY_STRING.equals(keystoreFile)) {
                    jmxListener.setKeystoreFile(keystoreFile);
                }
                String keystorePass = parseProperties(listener.getAttribute(KEYSTORE_PASS), catalinaProperties);
                if (!EMPTY_STRING.equals(keystorePass)) {
                    jmxListener.setKeystorePass(keystorePass);
                }
                String passwordFile = parseProperties(listener.getAttribute(PASSOWRD_FILE), catalinaProperties);
                if (!EMPTY_STRING.equals(passwordFile)) {
                    jmxListener.setPasswordFile(passwordFile);
                }
                String port = parseProperties(listener.getAttribute(JMX_PORT), catalinaProperties);
                if (!EMPTY_STRING.equals(port)) {
                    try {
                        jmxListener.setPort(Long.valueOf(port));
                    } catch (NumberFormatException e) {
                        logNonNumericValue("JMX Listener", JMX_PORT, port);
                    }
                }
                String protocols = parseProperties(listener.getAttribute(PROTOCOLS), catalinaProperties);
                if (!EMPTY_STRING.equals(protocols)) {
                    jmxListener.setProtocols(protocols);
                }
                String truststoreFile = parseProperties(listener.getAttribute(TRUSTSTORE_FILE), catalinaProperties);
                if (!EMPTY_STRING.equals(truststoreFile)) {
                    jmxListener.setTruststoreFile(truststoreFile);
                }
                String truststorePass = parseProperties(listener.getAttribute(TRUSTSTORE_PASS), catalinaProperties);
                if (!EMPTY_STRING.equals(truststorePass)) {
                    jmxListener.setTruststorePass(truststorePass);
                }
                String useJdkClientFactory = parseProperties(listener.getAttribute(USE_JDK_CLIENT_FACTORY), catalinaProperties);
                if (!EMPTY_STRING.equals(useJdkClientFactory)) {
                    jmxListener.setUseJdkClientFactory(Boolean.valueOf(useJdkClientFactory));
                }
                String useSSL = parseProperties(listener.getAttribute(USE_SSL), catalinaProperties);
                if (!EMPTY_STRING.equals(useSSL)) {
                    jmxListener.setUseSSL(Boolean.valueOf(useSSL));
                }
                return jmxListener;
            }
        }
        jmxListener.setEnabled(false);
        return jmxListener;
    }
}
