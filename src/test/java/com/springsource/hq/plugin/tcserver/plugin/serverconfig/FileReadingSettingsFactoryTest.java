// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.StaticResourceCache;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.WebApplicationLogger;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.JmxListener;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.ServerProperties;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Advanced;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Debug;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.GarbageCollection;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Memory;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;

public class FileReadingSettingsFactoryTest extends TestCase {

    private FileReadingSettingsFactory settingsFactory;

    private FileUtility fileUtility;

    private ConfigResponse config;

    public void setUp() throws Exception {
        super.setUp();
        settingsFactory = new FileReadingSettingsFactory();
        fileUtility = new FileUtility();
        config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));
        config.setValue("catalina.home", System.getProperty("java.io.tmpdir"));
        config.setValue("catalina.base", System.getProperty("java.io.tmpdir"));
        final File tmpConfDir = new File(System.getProperty("java.io.tmpdir") + "/conf");
        tmpConfDir.mkdir();
        final File tmpBinDir = new File(System.getProperty("java.io.tmpdir") + "/bin");
        tmpBinDir.mkdir();
        fileUtility.copyFiles(
            FileReadingSettingsFactoryTest.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-server-with-propertyplaceholders.xml"),
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
    }

    private Settings getExpectedSettings() {
        Settings expected = new Settings();
        Configuration configuration = new Configuration();

        ContextContainer contextContainer = new ContextContainer();
        StaticResourceCache staticResourceCache = new StaticResourceCache();
        staticResourceCache.setCacheMaxSize(200l);
        staticResourceCache.setCacheTTL(1000l);
        staticResourceCache.setCachingAllowed(true);
        contextContainer.setStaticResourceCache(staticResourceCache);
        WebApplicationLogger webAppLogger = new WebApplicationLogger();
        webAppLogger.setSwallowOutput(true);
        contextContainer.setWebApplicationLogger(webAppLogger);
        configuration.setContextContainer(contextContainer);

        GeneralConfig generalConfig = new GeneralConfig();
        ServerProperties serverProperties = new ServerProperties();
        serverProperties.setPort(4323l);
        serverProperties.setShutdown("GOAWAY");
        generalConfig.setServerProperties(serverProperties);
        JmxListener jmxListener = new JmxListener();
        jmxListener.setEnabled(true);
        jmxListener.setPort(6969L);
        jmxListener.setBind("127.0.0.1");
        jmxListener.setUseSSL(false);
        jmxListener.setPasswordFile(System.getProperty("java.io.tmpdir") + "/conf/jmxremote.password");
        jmxListener.setAccessFile(System.getProperty("java.io.tmpdir") + "/conf/jmxremote.access");
        jmxListener.setAuthenticate(true);
        generalConfig.setJmxListener(jmxListener);
        configuration.setGeneralConfig(generalConfig);

        ServerDefaults serverDefaults = new ServerDefaults();
        JspDefaults jspDefaults = new JspDefaults();
        jspDefaults.setFork(false);
        jspDefaults.setXpoweredBy(false);
        serverDefaults.setJspDefaults(jspDefaults);
        StaticDefaults staticDefaults = new StaticDefaults();
        staticDefaults.setDebug(0l);
        staticDefaults.setListings(false);
        serverDefaults.setStaticDefaults(staticDefaults);
        configuration.setServerDefaults(serverDefaults);

        expected.setConfiguration(configuration);

        Service service = new Service();
        service.setName("Catalina");
        Engine engine = new Engine();
        engine.setDefaultHost("localhost");
        engine.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        host.setAppBase("webapps");
        host.setAutoDeploy(true);
        host.setDeployOnStartup(true);
        host.setUnpackWARs(true);
        host.setDeployXML(true);
        Set<Host> hosts = new HashSet<Host>();
        hosts.add(host);
        engine.setHosts(hosts);
        service.setEngine(engine);
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.setPort(8080l);
        httpConnector.setProtocol("org.apache.coyote.http11.Http11Protocol");
        httpConnector.setConnectionTimeout(20000l);
        httpConnector.setRedirectPort(8443l);
        httpConnector.setAcceptCount(100l);
        httpConnector.setMaxKeepAliveRequests(15l);
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(httpConnector);
        service.setConnectors(connectors);
        Set<Service> services = new HashSet<Service>();
        services.add(service);
        expected.setServices(services);

        Set<DataSource> dataSources = new HashSet<DataSource>();
        TomcatDataSource dataSource = new TomcatDataSource();
        final Connection connection = new Connection();
        connection.setDriverClassName("org.hsql.jdbcDriver");
        connection.setPassword("dbpassword");
        connection.setUrl("jdbc:HypersonicSQL:database");
        connection.setUsername("dbusername");
        dataSource.setConnection(connection);
        com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.General general = new com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.General();
        general.setJndiName("jdbc/EmployeeDB");
        dataSource.setGeneral(general);
        TomcatConnectionPool connectionPool = new TomcatConnectionPool();
        connectionPool.setMaxActive(8l);
        connectionPool.setMaxIdle(4l);
        dataSource.setConnectionPool(connectionPool);
        dataSources.add(dataSource);
        expected.setDataSources(dataSources);

        Environment environment = new Environment();
        environment.setJavaHome("/path/to/jvm");

        JvmOptions jvmOptions = new JvmOptions();
        Debug debug = new Debug();
        debug.setHeapDumpOnOutOfMemoryError(true);
        debug.setLoggc("gc.log");
        debug.setPrintGCApplicationStoppedTime(true);
        debug.setPrintGCDetails(true);
        debug.setPrintGCTimeStamps(true);
        debug.setPrintHeapAtGC(true);
        jvmOptions.setDebug(debug);

        GarbageCollection garbageCollection = new GarbageCollection();
        garbageCollection.setMaxGCPauseMillis(500l);
        jvmOptions.setGarbageCollection(garbageCollection);

        com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.General generalJvm = new com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.General();
        generalJvm.setServer(true);
        jvmOptions.setGeneral(generalJvm);

        Memory memory = new Memory();
        memory.setMaxNewSize(256l);
        memory.setMaxPermSize(192l);
        memory.setMx(512l);
        memory.setSs(192l);
        memory.setNewSize(128l);
        memory.setMs(128l);
        jvmOptions.setMemory(memory);

        Advanced advanced = new Advanced();
        advanced.setCliArgs("-something=somethingelse");
        jvmOptions.setAdvanced(advanced);
        environment.setJvmOptions(jvmOptions);
        configuration.setEnvironment(environment);
        return expected;
    }

    public void testCreate() throws PluginException {
        final Settings settings = settingsFactory.create(config);
        // assertEquals(getExpectedSettings(), settings);
    }

    public void testGetFile() throws Exception {
        config.setValue("FILE_NAME", "/conf/readme");
        String response = settingsFactory.getFile(config);
        assertEquals("nothing to see here, move along", new String(Base64.decodeBase64(response.getBytes())));
    }

    public void testGetFile_invalidFileName() throws Exception {
        config.setValue("FILE_NAME", "/../server.xml");
        try {
            settingsFactory.getFile(config);
            fail("PluginException expected");
        } catch (PluginException e) {
            // we want this
        }
        assertNull(config.getValue("FILE_DATA"));
    }

}
