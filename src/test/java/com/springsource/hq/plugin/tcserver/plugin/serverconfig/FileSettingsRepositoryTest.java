// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.hyperic.hq.product.PlatformDetector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;

public class FileSettingsRepositoryTest extends TestCase {

    private FileSettingsRepository fileSettingsRepository;

    private SettingsFactory settingsFactory = new FileReadingSettingsFactory();

    private ConfigResponse config = new ConfigResponse();

    private FileUtility fileUtility;

    public void setUp() throws Exception {
        super.setUp();
        fileSettingsRepository = new FileSettingsRepository(null, null);
        fileUtility = new FileUtility();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));
        config.setValue("catalina.home", System.getProperty("java.io.tmpdir"));
        config.setValue("catalina.base", System.getProperty("java.io.tmpdir"));
        final File tmpConfDir = new File(System.getProperty("java.io.tmpdir") + "/conf");
        tmpConfDir.mkdir();
        final File tmpBinDir = new File(System.getProperty("java.io.tmpdir") + "/bin");
        tmpBinDir.mkdir();
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-server.xml"),
            new FileOutputStream(tmpConfDir.toString() + "/server.xml"));
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-setenv"),
            new FileOutputStream(tmpBinDir.toString() + "/setenv.sh"));
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-context.xml"),
            new FileOutputStream(tmpConfDir.toString() + "/context.xml"));
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-web.xml"),
            new FileOutputStream(tmpConfDir.toString() + "/web.xml"));
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-catalina.properties"),
            new FileOutputStream(tmpConfDir.toString() + "/catalina.properties"));
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-readme"),
            new FileOutputStream(tmpConfDir.toString() + "/readme"));
        if (PlatformDetector.IS_WIN32) {
            fileUtility.copyFiles(
                FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-wrapper.conf"),
                new FileOutputStream(tmpConfDir.toString() + "/wrapper.conf"));
        }
    }

    public void testSaveConfiguration() throws PluginException {
        final Settings settings = settingsFactory.create(config);
        // settings.getConfiguration().getGeneralConfig().getServerProperties().
        // setPort(1234l);
        // settings.getConfiguration().getGeneralConfig().getServerProperties().
        // setShutdown("SHUTMEDOWN");
        // TomcatDataSource tomcatDataSource = new TomcatDataSource();
        // final Connection connection = new Connection();
        // connection.setDriverClassName("org.hsql.jdbcDriver");
        // connection.setPassword("dbpassword");
        // connection.setUrl("jdbc:HypersonicSQL:database");
        // connection.setUsername("dbusername");
        // tomcatDataSource.setConnection(connection);
        // com.springsource.ams.tcserver.serverconfig.resources.jdbc.General
        // general = new
        // com.springsource.ams.tcserver.serverconfig.resources
        // .jdbc.General();
        // general.setJndiName("jdbc/UserDB");
        // tomcatDataSource.setGeneral(general);
        // TomcatConnectionPool connectionPool = new TomcatConnectionPool();
        // connectionPool.setMaxActive(8l);
        // connectionPool.setMaxIdle(4l);
        // connectionPool.setJdbcInterceptors("interceptor");
        // tomcatDataSource.setConnectionPool(connectionPool);
        // settings.getDataSources().put("jdbcUserDB", tomcatDataSource);
        fileSettingsRepository.saveConfiguration(config, settings);
        final Settings convertedSettings = settingsFactory.create(config);
        assertEquals(settings, convertedSettings);
    }

    public void testPutFile() throws Exception {
        final Settings settings = settingsFactory.create(config);
        final BufferedReader envFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("java.io.tmpdir")
            + "/conf/server.xml")));
        String line = envFileReader.readLine();
        final StringBuilder serverXml = new StringBuilder();
        for (; line != null; line = envFileReader.readLine()) {
            serverXml.append(line).append("\n");
        }
        config.setValue("FILE_DATA", new String(Base64.encodeBase64(serverXml.toString().getBytes())));
        config.setValue("FILE_NAME", "/conf/server.xml");
        fileSettingsRepository.prepareFile(config);
        final Settings convertedSettings = settingsFactory.create(config);
        assertEquals(settings, convertedSettings);
    }

    public void testPutFile_invalidFileName() throws Exception {
        final Settings settings = settingsFactory.create(config);
        final BufferedReader envFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("java.io.tmpdir")
            + "/conf/server.xml")));
        String line = envFileReader.readLine();
        final StringBuilder serverXml = new StringBuilder();
        for (; line != null; line = envFileReader.readLine()) {
            serverXml.append(line).append("\n");
        }
        config.setValue("FILE_DATA", serverXml.toString());
        config.setValue("FILE_NAME", "/../server.xml");
        try {
            fileSettingsRepository.prepareFile(config);
            fail("PluginException expected");
        } catch (PluginException e) {
            // we want this
        }
        final Settings convertedSettings = settingsFactory.create(config);
        assertEquals(settings, convertedSettings);
    }

}
