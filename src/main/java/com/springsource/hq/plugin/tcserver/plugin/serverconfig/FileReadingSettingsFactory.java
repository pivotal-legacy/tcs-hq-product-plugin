/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PlatformDetector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.context.ContextContainerConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.EnvironmentFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.UnixFileReadingEnvironmentFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.WindowsFileReadingEnvironmentFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.general.GeneralConfigConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.resources.jdbc.DataSourcesConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults.ServerDefaultsConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.ServicesConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Implementation of {@link SettingsFactory} that loads Settings by reading tc Runtime config files from the file system,
 * relative to the tc Runtime instance's installpath, which should be specified in the config parameter
 * 
 * @author jhickey
 * 
 */
public class FileReadingSettingsFactory implements SettingsFactory {

    private XmlElementConverter<ContextContainer> contextContainerConverter = new ContextContainerConverter();

    private XmlElementConverter<Set<DataSource>> dataSourcesConverter = new DataSourcesConverter();

    private XmlElementConverter<GeneralConfig> generalConfigConverter = new GeneralConfigConverter();

    private EnvironmentFactory environmentFactory = PlatformDetector.IS_WIN32 ? new WindowsFileReadingEnvironmentFactory()
        : new UnixFileReadingEnvironmentFactory();

    private XmlElementConverter<ServerDefaults> serverDefaultsConverter = new ServerDefaultsConverter();

    private XmlElementConverter<Set<Service>> servicesConverter = new ServicesConverter();

    private XmlParser serverXmlParser = new ServerXmlParser();

    private XmlParser webXmlParser = new WebXmlParser();

    private XmlParser contextXmlParser = new ContextXmlParser();

    public Settings create(final ConfigResponse config) throws PluginException {
        try {
            final Settings settings = new Settings();
            Properties catalinaProperties = loadCatalinaProperties(config);
            loadServerXml(config, settings, catalinaProperties);
            loadContextXml(config, settings.getConfiguration(), catalinaProperties);
            loadWebXml(config, settings.getConfiguration(), catalinaProperties);
            settings.getConfiguration().setEnvironment(environmentFactory.create(config));
            return settings;
        } catch (Exception e) {
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e.getMessage(), e);
        }
    }

    public Environment createEnvironment(final ConfigResponse config) throws PluginException {
        try {
            return environmentFactory.create(config);
        } catch (Exception e) {
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e.getMessage(), e);
        }
    }

    public String getFile(ConfigResponse config) throws PluginException {
        final String filename = createSanitizedPath(Metric.decode(config.getValue("installpath")), config.getValue("FILE_NAME"));
        try {
            return readFileAsBase64(filename);
        } catch (Exception e) {
            throw new PluginException("Unable to read configuration file.  Cause: " + e.getMessage());
        }
    }

    private Configuration createConfiguration(final Element server, final Properties catalinaProperties) {
        final Configuration configuration = new Configuration();
        configuration.setGeneralConfig(generalConfigConverter.convert(server, catalinaProperties));
        return configuration;
    }

    private String createSanitizedPath(final String path, final String file) throws PluginException {
        if (file.contains("..")) {
            throw new PluginException();
        }
        StringBuilder fullPath = new StringBuilder();
        fullPath.append(path);
        if (!file.startsWith("/")) {
            fullPath.append("/");
        }
        fullPath.append(file);
        return fullPath.toString();
    }

    private Properties loadCatalinaProperties(final ConfigResponse config) throws IOException {
        Resource propResource = new FileSystemResource(Metric.decode(config.getValue("installpath")) + "/conf/catalina.properties");
        Properties catalinaProperties;
        if (propResource.exists()) {
            catalinaProperties = PropertiesLoaderUtils.loadProperties(propResource);
        } else {
            catalinaProperties = new Properties();
        }
        // add catalina.home and catalina.base which are not in the props file
        catalinaProperties.put("catalina.home", Metric.decode(config.getValue("catalina.home")));
        catalinaProperties.put("catalina.base", Metric.decode(config.getValue("installpath")));
        return catalinaProperties;
    }

    private void loadContextXml(final ConfigResponse config, final Configuration configuration, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, PluginException {
        final Element context = contextXmlParser.parse(contextXmlParser.createDocument(config));
        configuration.setContextContainer(contextContainerConverter.convert(context, catalinaProperties));
    }

    private void loadServerXml(final ConfigResponse config, final Settings settings, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, PluginException {
        final Element server = serverXmlParser.parse(serverXmlParser.createDocument(config));
        settings.setConfiguration(createConfiguration(server, catalinaProperties));
        settings.setDataSources(dataSourcesConverter.convert(server, catalinaProperties));
        settings.setServices(servicesConverter.convert(server, catalinaProperties));
    }

    private void loadWebXml(final ConfigResponse config, final Configuration configuration, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, PluginException {
        final Element webApp = webXmlParser.parse(webXmlParser.createDocument(config));
        configuration.setServerDefaults(serverDefaultsConverter.convert(webApp, catalinaProperties));
    }

    private String readFileAsBase64(final String destinationFile) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(new File(destinationFile));
            int len = 0;
            int chunk;
            byte[] buf = new byte[1024];
            while ((chunk = in.read(buf)) > 0) {
                len += chunk;
            }
            try {
                in.close();
            } catch (IOException e) {
                // ignore
            }
            in = new FileInputStream(new File(destinationFile));
            int pos = 0;
            byte[] data = new byte[len];
            while ((chunk = in.read(buf)) > 0) {
                for (int i = 0; i < chunk; i++) {
                    data[pos++] = buf[i];
                }
            }
            return new String(Base64.encodeBase64(data));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
