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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PlatformDetector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.plugin.Utils;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.FileOwnershipChanger;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.FilePermissionsChanger;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.StandardFileOwnershipChangerFactory;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.StandardFilePermissionsChangerFactory;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.context.ContextContainerConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.EnvironmentRepository;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.UnixFileEnvironmentRepository;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.WindowsFileEnvironmentRepository;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.general.GeneralConfigConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.resources.jdbc.DataSourcesConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults.ServerDefaultsConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.ServicesConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

public class FileSettingsRepository implements SettingsRepository {

    private XmlElementConverter<ContextContainer> contextContainerConverter = new ContextContainerConverter();

    private XmlParser contextXmlParser = new ContextXmlParser();

    private XmlElementConverter<Set<DataSource>> dataSourcesConverter = new DataSourcesConverter();

    private XmlElementConverter<GeneralConfig> generalConfigConverter = new GeneralConfigConverter();

    private final Log logger = LogFactory.getLog(FileSettingsRepository.class);

    private XmlElementConverter<ServerDefaults> serverDefaultsConverter = new ServerDefaultsConverter();

    private XmlParser serverXmlParser = new ServerXmlParser();

    private XmlElementConverter<Set<Service>> servicesConverter = new ServicesConverter();

    private EnvironmentRepository environmentRepository = PlatformDetector.IS_WIN32 ? new WindowsFileEnvironmentRepository()
        : new UnixFileEnvironmentRepository();

    private XmlParser webXmlParser = new WebXmlParser();

    private FileUtility fileUtility = new FileUtility();

    private final FilePermissionsChanger filePermissionsChanger;

    private final FileOwnershipChanger fileOwnershipChanger;

    public FileSettingsRepository() throws PluginException {
        this(new StandardFileOwnershipChangerFactory().getFileOwnershipChanger(),
            new StandardFilePermissionsChangerFactory().getFilePermissionsChanger());
    }

    public FileSettingsRepository(FileOwnershipChanger fileOwnershipChanger, FilePermissionsChanger filePermissionsChanger) throws PluginException {
        this.fileOwnershipChanger = fileOwnershipChanger;
        this.filePermissionsChanger = filePermissionsChanger;
    }

    private boolean checkAndCreateParentDirectories(String installPath, String filePath) throws PluginException {
        boolean canWrite = new File(installPath).canWrite();
        boolean exists = false;
        if (canWrite) {
            String absoluteDirectory = filePath.substring(0, filePath.lastIndexOf('/'));
            File directory = new File(absoluteDirectory);
            exists = directory.exists();

            if (!exists) {
                try {
                    exists = directory.mkdirs();
                } catch (SecurityException e) {
                    throw new PluginException("Unable to create directory structure '" + absoluteDirectory + "': " + e.getMessage());
                }
            } else if (!directory.canWrite()) {
                throw new PluginException("Unable to write to directory: " + absoluteDirectory);
            }
        } else {
            throw new PluginException("Unable to write to directory:" + installPath);
        }
        return exists;
    }

    private String copyBackupEnvironmentFile(ConfigResponse config) throws FileNotFoundException, IOException {
        Date date = new Date();
        if ((PlatformDetector.IS_WIN32)) {
            return fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/conf", "wrapper.conf", date);
        } else {
            final String setenv = Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh";
            if (new File(setenv).exists()) {
                return fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/bin", "setenv.sh", date);
            } else {
                return null;
            }
        }
    }

    private String copyBackupFiles(ConfigResponse config) throws FileNotFoundException, IOException {
        Date date = new Date();
        fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/conf", "server.xml", date);
        fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/conf", "context.xml", date);
        fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/conf", "web.xml", date);
        if ((PlatformDetector.IS_WIN32)) {
            return fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/conf", "wrapper.conf", date);
        } else {
            final String setenv = Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh";
            if (new File(setenv).exists()) {
                return fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), "/bin", "setenv.sh", date);
            } else {
                return null;
            }
        }
    }

    /**
     * Copy a file from one location on the agent's machine to another location on the agent's machine
     */
    public void copyFile(ConfigResponse config) throws PluginException {
        String sourcePath = config.getValue("SOURCE");
        String destinationPath = config.getValue("DESTINATION");

        InputStream in = null;
        FileOutputStream out = null;

        try {
            File sourceFile = new File(sourcePath);
            in = new FileInputStream(sourceFile);

            File destFile = new File(destinationPath);
            out = new FileOutputStream(destFile);

            fileUtility.copyFiles(in, out);

            out.flush();
            out.getFD().sync();

            this.filePermissionsChanger.changeFilePermissions(destFile);
            this.fileOwnershipChanger.changeFileOwnership(destFile, config.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_USERNAME),
                config.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_GROUP));
        } catch (IOException e) {
            logger.warn("An error occured with a stream: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("An error occurred upon closing the input stream: " + e.getMessage(), e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("An error occurred upon closing the output stream: " + e.getMessage());
                }
            }
        }
    }

    public boolean fileExists(ConfigResponse config) throws PluginException {
        return new File(config.getValue("FILENAME")).exists();
    }

    private String createSanitizedPath(final String path, final String file) throws PluginException {
        if (file.contains("..")) {
            throw new PluginException("The file cannot contain '..'.");
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

    public void prepareFile(ConfigResponse config) throws PluginException {
        final String installPath = Metric.decode(config.getValue("installpath"));
        final String filePath = createSanitizedPath(installPath, config.getValue("FILE_NAME"));
        final boolean createBackup = config.getValue("nobackupfile") == null || config.getValue("nobackupfile").equals("false");
        if (checkAndCreateParentDirectories(installPath, filePath)) {
            if (createBackup) {
                try {
                    String baseDirUrl = Metric.decode(config.getValue("installpath"));
                    String fileName = config.getValue("FILE_NAME");
                    fileUtility.copyBackupFile(baseDirUrl, fileName);
                } catch (Exception e) {
                    throw new PluginException("Unable to save file. An error occurred while creating a backup of the existing file.  Cause: "
                        + e.getMessage());
                }
            }
        } else {
            throw new PluginException("Unable to find or create directory structure for file: " + filePath
                + ". Check file and directory permissions.");
        }
    }

    public void putFile(ConfigResponse config) throws PluginException {
        final String fileData = config.getValue("FILE_DATA");
        final String filename = createSanitizedPath(Metric.decode(config.getValue("installpath")), config.getValue("FILE_NAME"));
        try {
            fileUtility.copyBackupFile(Metric.decode(config.getValue("installpath")), config.getValue("FILE_NAME"));
        } catch (Exception e) {
            throw new PluginException("Unable to put file.  An error occurred while creating a backup of the existing file.  Cause: "
                + e.getMessage());
        }
        try {
            writeFileFromBase64(fileData, filename);
        } catch (IOException e) {
            throw new PluginException(
                "Unable to put file.  An error occurred while writing to the file.  A backup file with timestamp was saved.  You may need to manually revert.  Cause: "
                    + e.getMessage());
        }
    }

    public void revertToPreviousConfiguration(ConfigResponse config) throws PluginException {
        TreeSet<String> backupSet = (TreeSet<String>) fileUtility.getLatestBackupDirectories(config.getValue("installpath"));
        if (!backupSet.isEmpty()) {
            try {
                fileUtility.revertToBackupFiles(config.getValue("installpath"), backupSet.last());
            } catch (FileUtilityException e) {
                throw new PluginException(e.getMessage());
            }
        } else {
            throw new PluginException("Unable to revert to backup files: No backup directories found in "
                + new File(config.getValue("installpath"), "backup").getAbsolutePath());
        }
    }

    public void saveConfiguration(ConfigResponse config, Settings settings) throws PluginException {
        String setenvOrWrapperConfBackupFile;
        try {
            setenvOrWrapperConfBackupFile = copyBackupFiles(config);
        } catch (Exception e) {
            throw new PluginException("Error creating backup file.  Configuration was not saved.  Cause: " + e.getMessage());
        }
        Document serverXml;
        Document webXml;
        Document contextXml;
        Properties catalinaProperties;
        try {
            catalinaProperties = loadCatalinaProperties(config);
        } catch (Exception e) {
            throw new PluginException("Error parsing existing catalina.properties.  Configuration was not saved.  Cause: " + e.getMessage(), e);
        }
        try {
            serverXml = updateServerConfiguration(settings, config, catalinaProperties);
        } catch (Exception e) {
            throw new PluginException(
                "Error parsing existing server.xml to perform updates.  Configuration was not saved.  Cause: " + e.getMessage(), e);
        }
        try {
            webXml = updateWebConfiguration(settings, config, catalinaProperties);
        } catch (Exception e) {
            throw new PluginException("Error parsing existing web.xml to perform updates.  Configuration was not saved.  Cause: " + e.getMessage(), e);
        }
        try {
            contextXml = updateContextConfiguration(settings, config, catalinaProperties);
        } catch (Exception e) {
            throw new PluginException("Error parsing existing context.xml to perform updates.  Configuration was not saved.  Cause: "
                + e.getMessage(), e);
        }
        writeFiles(config, serverXml, webXml, contextXml);
        if (setenvOrWrapperConfBackupFile == null) {
            environmentRepository.save(config, settings.getConfiguration().getEnvironment());
        } else {
            environmentRepository.save(setenvOrWrapperConfBackupFile, config, settings.getConfiguration().getEnvironment());
        }
    }

    public void saveEnvironment(ConfigResponse config, Environment environment) throws PluginException {
        String setenvOrWrapperConfBackupFile;
        try {
            setenvOrWrapperConfBackupFile = copyBackupEnvironmentFile(config);
        } catch (Exception e) {
            throw new PluginException("Error creating backup file.  Configuration was not saved.  Cause: " + e.getMessage());
        }
        if (setenvOrWrapperConfBackupFile == null) {
            environmentRepository.save(config, environment);
        } else {
            environmentRepository.save(setenvOrWrapperConfBackupFile, config, environment);
        }
    }

    private Document updateContextConfiguration(final Settings settings, final ConfigResponse config, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, PluginException {
        final Document contextXml = contextXmlParser.createDocument(config);
        final Element context = contextXmlParser.parse(contextXml);
        contextContainerConverter.convert(contextXml, context, settings.getConfiguration().getContextContainer(), catalinaProperties);
        return contextXml;
    }

    private Document updateServerConfiguration(final Settings settings, final ConfigResponse config, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, TransformerException, PluginException {
        final Document serverXml = serverXmlParser.createDocument(config);
        final Element server = serverXmlParser.parse(serverXml);
        generalConfigConverter.convert(serverXml, server, settings.getConfiguration().getGeneralConfig(), catalinaProperties);
        dataSourcesConverter.convert(serverXml, server, settings.getDataSources(), catalinaProperties);
        servicesConverter.convert(serverXml, server, settings.getServices(), catalinaProperties);
        return serverXml;
    }

    private Document updateWebConfiguration(final Settings settings, final ConfigResponse config, final Properties catalinaProperties)
        throws ParserConfigurationException, SAXException, IOException, PluginException {
        final Document webXml = webXmlParser.createDocument(config);
        final Element webApp = webXmlParser.parse(webXml);
        serverDefaultsConverter.convert(webXml, webApp, settings.getConfiguration().getServerDefaults(), catalinaProperties);
        return webXml;
    }

    private void writeFileFromBase64(final String text, final String destinationFile) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(destinationFile));
            final InputStream in = new ByteArrayInputStream(Base64.decodeBase64(text.getBytes()));
            fileUtility.copyFiles(in, out);

            out.flush();
            out.getFD().sync();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Error closing output stream to file: " + destinationFile + ".  Cause: " + e.getMessage());
                }
            }
        }
    }

    private void writeFiles(ConfigResponse config, Document serverXml, Document webXml, Document contextXml) throws PluginException {
        final Map<String, String> failures = new HashMap<String, String>();
        try {
            serverXmlParser.writeDocument(serverXml, config);
        } catch (Exception e) {
            failures.put("server.xml", e.getMessage());
        }
        try {
            webXmlParser.writeDocument(webXml, config);
        } catch (Exception e) {
            failures.put("web.xml", e.getMessage());
        }
        try {
            contextXmlParser.writeDocument(contextXml, config);
        } catch (Exception e) {
            failures.put("context.xml", e.getMessage());
        }
        if (!(failures.isEmpty())) {
            final StringBuilder exceptionText = new StringBuilder();
            for (Map.Entry<String, String> failureEntry : failures.entrySet()) {
                exceptionText.append("Failed to write Configuration to file: ").append(failureEntry.getKey()).append(".  Cause: ").append(
                    failureEntry.getValue());
            }
            throw new PluginException(exceptionText.toString());
        }
    }

}
