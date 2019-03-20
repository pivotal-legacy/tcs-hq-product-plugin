// Copyright (c) 2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hyperic.util.config.ConfigResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.FileReadingSettingsFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.ServerXmlParser;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.SettingsFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlParser;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.general.GeneralConfigConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.resources.jdbc.DataSourcesConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.ServicesConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

public class HttpConnectorConverterTest {

    private static final String TC_RUNTIME_PATH = "com/springsource/hq/plugin/tcserver/plugin/serverconfig/springsource-tc-server-standard-2.0.0/";

    protected Settings settings;

    protected SettingsFactory settingsFactory;

    protected Document serverXml;

    protected Element server;

    protected Configuration configuration;

    protected XmlElementConverter<GeneralConfig> generalConfigConverter;

    protected GeneralConfig generalConfig;

    protected DataSourcesConverter dataSourcesConverter;

    protected XmlElementConverter<Set<Service>> servicesConverter;

    protected HttpConnectorConverter httpConnCon = new HttpConnectorConverter();

    @Before
    public void setUp() throws Exception {
        settingsFactory = new FileReadingSettingsFactory();

        ConfigResponse config = createConfigResponse();
        settings = settingsFactory.create(config);
        assertNotNull(settings);

        /*
         * See the FileSettingsRepository.updateServerConfiguration method
         * 
         * final Document serverXml = serverXmlParser.createDocument(config); final Element server =
         * serverXmlParser.parse(serverXml); generalConfigConverter.convert(serverXml, server,
         * settings.getConfiguration().getGeneralConfig(), catalinaProperties); dataSourcesConverter.convert(serverXml,
         * server, settings.getDataSources(), catalinaProperties); servicesConverter.convert(serverXml, server,
         * settings.getServices(), catalinaProperties);
         */

        XmlParser serverXmlParser = new ServerXmlParser();
        assertNotNull(serverXmlParser);
        serverXml = serverXmlParser.createDocument(config);
        assertNotNull(serverXml);
        Properties catalinaProperties = loadCatalinaProperties(config);
        assertNotNull(catalinaProperties);
        server = serverXmlParser.parse(serverXml);
        assertNotNull(server);
        // System.out.println("########" + convertXmlToString(serverXml));

        configuration = createConfiguration(catalinaProperties, server);
        settings.setConfiguration(configuration);

        generalConfigConverter = new GeneralConfigConverter();
        generalConfig = settings.getConfiguration().getGeneralConfig();
        generalConfigConverter.convert(serverXml, server, generalConfig, catalinaProperties);
        // System.out.println("########" + convertXmlToString(serverXml));

        dataSourcesConverter = new DataSourcesConverter();
        dataSourcesConverter.convert(serverXml, server, settings.getDataSources(), catalinaProperties);
        // System.out.println("########" + convertXmlToString(serverXml));

        servicesConverter = new ServicesConverter();
        servicesConverter.convert(serverXml, server, settings.getServices(), catalinaProperties);
        // System.out.println("########" + convertXmlToString(serverXml));
    }

    @Test
    public void testConnectorForIncorrectEntries() throws Exception {
        NodeList listeners = serverXml.getElementsByTagName("Listener");
        assertNotNull(listeners);
        assertEquals("The list of listeners should contain 6 items", 6, listeners.getLength());
        boolean isAprConfigured = false;

        // Is the AprLifecycleListener configured? The only reason that the
        // APR SSL attrs should be added is if this listener is present.
        for (int i = 0; i < listeners.getLength(); ++i) {
            Element listener = (Element) listeners.item(i);
            NamedNodeMap listenerAttrs = listener.getAttributes();
            for (int j = 0; j < listenerAttrs.getLength(); ++j) {
                Node attr = listenerAttrs.item(j);
                if ("className".equals(attr.getNodeName())) {
                    if ("org.apache.catalina.core.AprLifecycleListener".equals(attr.getNodeValue())) {
                        isAprConfigured = true;
                    }
                }
            }
        }

        NodeList connectors = serverXml.getElementsByTagName("Connector");
        assertNotNull(connectors);
        assertEquals("The list of connectors should contain 1 item", 1, connectors.getLength());
        Element connector = (Element) connectors.item(0);
        assertTrue(connector.hasAttributes());
        NamedNodeMap connectorAttrs = connector.getAttributes();

        if (isAprConfigured) {
            for (int i = 0; i < connectorAttrs.getLength(); ++i) {
                Node node = connectorAttrs.item(i);
                compareAprSslConnectorNodeValues(node.getNodeName());
            }
        } else {
            for (int i = 0; i < connectorAttrs.getLength(); ++i) {
                Node node = connectorAttrs.item(i);
                compareBioSslConnectorNodeValues(node.getNodeName());
            }

        }
    }

    private void compareBioSslConnectorNodeValues(String nodeName) {

        // TODO: Verify that the protocol attribute = org.apache.coyote.http11.Http11Protocol

        String[] bioExpectedAttrs = {
        /* Begin common attrs */
        "allowTrace", "emptySessionPath", "enableLookups", "maxPostSize", "maxSavePostSize", "protocol", "proxyName", "proxyPort", "redirectPort",
            "SSLEnabled", "scheme", "secure", "URIEncoding", "useBodyEncodingForURI", "useIPVHosts", "xpoweredBy",
            /* End common attrs */
            "acceptCount", "address", "bufferSize", "compressableMimeType", "compression", "connectionLinger", "connectionTimeout", "executor",
            "keepAliveTimeout", "disableUploadTimeout", "maxHttpHeaderSize", "maxKeepAliveRequests", "maxThreads", "noCompressionUserAgents", "port",
            "restrictedUserAgents", "server", "socketBuffer", "tcpNoDelay", "threadPriority" };

        List<String> http11ProtcolExpectedAttrsList = Arrays.asList(bioExpectedAttrs);

        String[] bioSslExpectedAttrs = {
        /* From the docs (https://tomcat.apache.org/tomcat-6.0-doc/config/http.html#SSL Support) */
        "algorithm", "clientAuth", "keystoreFile", "keystorePass", "keystoreType", "keystoreProvider", "sslProtocol", "ciphers", "keyAlias",
            "truststoreFile", "truststorePass", "truststoreType", "truststoreProvider", "sessionCacheSize", "sessionTimeout", "crlFile",
            "allowUnsafeLegacyRenegotiation" };

        List<String> bioSslExpectedAttrsList = Arrays.asList(bioSslExpectedAttrs);

        boolean isBioAttr = http11ProtcolExpectedAttrsList.contains(nodeName);
        boolean isBioSslAttr = bioSslExpectedAttrsList.contains(nodeName);

        assertTrue("The attribute [" + nodeName + "] is not expected in the BIO+SSL config", isBioAttr || isBioSslAttr);
    }

    public void compareAprSslConnectorNodeValues(String nodeName) {

        String[] aprExpectedAttrs = {
        /* Begin common attrs */
        "allowTrace", "emptySessionPath", "enableLookups", "maxPostSize", "maxSavePostSize", "protocol", "proxyName", "proxyPort", "redirectPort",
            "SSLEnabled", "scheme", "secure", "URIEncoding", "useBodyEncodingForURI", "useIPVHosts", "xpoweredBy",
            /* End common attrs */
            "keepAliveTimeout", "pollTime", "pollerSize", "pollerThreadCount", "useSendfile", "sendfileSize", "sendFileThreadCount" };

        List<String> aprExpectedAttrsList = Arrays.asList(aprExpectedAttrs);

        // TODO: Verify that the protocol attribute = org.apache.coyote.http11.Http11AprProtocol

        String[] aprSslExpectedAttrs = {
        /* From the docs (https://tomcat.apache.org/tomcat-6.0-doc/apr.html#HTTPS) */
        "SSLEnabled", "SSLProtocol", "SSLCipherSuite", "SSLCertificateFile", /*
                                                                              * The only required attr. This can contain
                                                                              * cert and key, not password encoded, and
                                                                              * if that's the case, then that's the only
                                                                              * attribute needed when SSLEnabled=true
                                                                              * --Filip
                                                                              */
        "SSLCertificateKeyFile", "SSLPassword", "SSLVerifyClient", "SSLVerifyDepth", "SSLCACertificateFile", "SSLCACertificatePath",
            "SSLCertificateChainFile", "SSLCARevocationFile", "SSLCARevocationPath" };

        List<String> aprSslExpectedAttrsList = Arrays.asList(aprSslExpectedAttrs);

        boolean isAprAttr = aprExpectedAttrsList.contains(nodeName);
        boolean isAprSslAttr = aprSslExpectedAttrsList.contains(nodeName);

        assertTrue("The attribute [" + nodeName + "] is not expected APR config", isAprAttr || isAprSslAttr);
    }

    private Configuration createConfiguration(Properties catalinaProperties, Element server) {
        XmlElementConverter<GeneralConfig> generalConfigConverter = new GeneralConfigConverter();

        Configuration configuration = new Configuration();
        GeneralConfig generalConfig = generalConfigConverter.convert(server, catalinaProperties);
        configuration.setGeneralConfig(generalConfig);
        return configuration;
    }

    private ConfigResponse createConfigResponse() {
        String tcRuntimePath = getTcRuntimePath();

        int index = tcRuntimePath.indexOf(':') + 1;
        tcRuntimePath = tcRuntimePath.substring(index);

        String tcRuntimeHome = tcRuntimePath + "tomcat-6.0.25.A-RELEASE";
        String tcRuntimeInstance = tcRuntimeHome + "tcs-instance1";

        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", tcRuntimeHome);
        config.setValue("catalina.home", tcRuntimeHome);
        config.setValue("catalina.base", tcRuntimeInstance);
        return config;
    }

    private String getTcRuntimePath() {
        ClassPathResource tcRuntimeLocation = new ClassPathResource(TC_RUNTIME_PATH);
        String fullPath = null;
        try {
            fullPath = tcRuntimeLocation.getURI().toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fullPath;
    }

    private Properties loadCatalinaProperties(ConfigResponse config) throws IOException {

        String catalinaPropertiesFilePath = config.getValue("installpath") + "/conf/tcruntime-2.0-catalina.properties";
        Resource propResource = new FileSystemResource(catalinaPropertiesFilePath);
        Properties catalinaProperties;
        if (propResource.exists()) {
            catalinaProperties = PropertiesLoaderUtils.loadProperties(propResource);
        } else {
            catalinaProperties = new Properties();
        }
        // Add catalina.home and catalina.base which are not in the props file
        catalinaProperties.put("catalina.home", config.getValue("catalina.home"));
        catalinaProperties.put("catalina.base", config.getValue("installpath"));
        return catalinaProperties;
    }
}
