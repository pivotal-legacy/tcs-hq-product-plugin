
package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import static org.junit.Assert.assertEquals;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Advanced;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Debug;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.GarbageCollection;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Memory;

/**
 * TODO Document UnixFileReadingSettingsFactory
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of UnixFileReadingSettingsFactory
 * 
 */
public class UnixFileReadingSettingsFactoryTests {

    private Environment largeEnvironment;

    private Environment multipleLinesEnvironment;

    private UnixFileReadingEnvironmentFactory factory = new UnixFileReadingEnvironmentFactory();

    @Before
    public void setup() {
        setupLargeEnvironment();
        setupMultipleLinesEnvironment();
    }

    private void setupLargeEnvironment() {
        this.largeEnvironment = new Environment();
        JvmOptions jvmOptions = new JvmOptions();
        this.largeEnvironment.setJvmOptions(jvmOptions);
        Advanced advanced = new Advanced();
        advanced.setCliArgs("-d64 -XX:NewRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseTLAB -XX:+DisableExplicitGC -XX:+CMSIncrementalMode -XX:+CMSClassUnloadingEnabled -XX:+UseCompressedOops -Djava.awt.headless=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dcom.services.logging.disableRemoteList=true -Dcom.services.logging.disableRemoteLogging=true -Dcom.log.config.ignoreContextClassLoader=true -Dwebreportstudio.file.cleanup.interval=60 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL -Dcom.log.config.url=\"file:////install/cfg1/config/Lev1/Web/Common/LogConfig\" -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dmulticast_udp_ip_ttl=0 -Dmulticast.address=239.1.1.1 -Dmulticast.port=31001 -Djms.authentication.decorator=false -Dscs.host=rdcesx05058.race.com -Dscs.port=8080 -Dcs.repository.dir=\"/install/Server1_1/Repository\" -Dcom.server.isclustered=false -Dcontainer.identifier=vfabrictcsvr -Dcache.locators=rdcesx05058.race.com[41415] -Dspring.profiles.active=locators -Dauto.publish.port=8080 -Dappserver.instance.id=Server1_1_rdcesx05058.race.com -Dconfig.lev.web.appserver.logs.dir=\"/install/cfg1/config/Lev1/Web/Logs/Server1_1\" -Djava.security.auth.login.config=\"/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/conf/jaas.config\" -Dmetadata.use.cluster.properties=true -Ddeploy.dir=\"/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/_webapps\" -Ddeployment.agent.client.config=\"/install/cfg1/Home/RemoteDeploymentAgentClient/2.1/deployagtclt.properties\"");
        jvmOptions.setAdvanced(advanced);
        Memory memory = new Memory();
        memory.setMx(4096L);
        memory.setMs(1024l);
        memory.setPermSize(768l);
        memory.setMaxPermSize(1024l);
        memory.setSs(256l);
        jvmOptions.setMemory(memory);
    }

    private void setupMultipleLinesEnvironment() {
        this.multipleLinesEnvironment = new Environment();
        JvmOptions jvmOptions = new JvmOptions();
        this.multipleLinesEnvironment.setJvmOptions(jvmOptions);
        Memory memory = new Memory();
        memory.setMx(512L);
        memory.setMaxPermSize(192l);
        memory.setSs(192l);
        memory.setMs(64l);
        jvmOptions.setMemory(memory);
        GarbageCollection gc = new GarbageCollection();
        gc.setMaxGCPauseMillis(500l);
        jvmOptions.setGarbageCollection(gc);
        Debug debug = new Debug();
        debug.setHeapDumpOnOutOfMemoryError(true);
        jvmOptions.setDebug(debug);
    }

    @Test
    public void setenvWithDoubleQuotedProperties() throws PluginException {
        String path = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/test_quotes";
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", Metric.encode(path));
        Environment environment = factory.create(config);

        assertEquals(largeEnvironment.getJvmOptions().getAdvanced().getCliArgs(), environment.getJvmOptions().getAdvanced().getCliArgs());
        assertEquals(largeEnvironment.getJvmOptions().getMemory().getMaxPermSize(), environment.getJvmOptions().getMemory().getMaxPermSize());
        assertEquals(largeEnvironment.getJvmOptions().getMemory().getMx(), environment.getJvmOptions().getMemory().getMx());
        assertEquals(largeEnvironment.getJvmOptions().getMemory().getMs(), environment.getJvmOptions().getMemory().getMs());
        assertEquals(largeEnvironment.getJvmOptions().getMemory().getSs(), environment.getJvmOptions().getMemory().getSs());
        assertEquals(largeEnvironment.getJvmOptions().getMemory().getPermSize(), environment.getJvmOptions().getMemory().getPermSize());
    }

    @Test
    public void testMultipleLineValues() throws PluginException {
        String path = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/wrapped_lines";
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", Metric.encode(path));
        Environment fromEnvironmentFile = factory.create(config);

        assertEquals(multipleLinesEnvironment.getJvmOptions().getDebug().getHeapDumpOnOutOfMemoryError(),
            fromEnvironmentFile.getJvmOptions().getDebug().getHeapDumpOnOutOfMemoryError());
        assertEquals(multipleLinesEnvironment.getJvmOptions().getMemory().getMaxPermSize(),
            fromEnvironmentFile.getJvmOptions().getMemory().getMaxPermSize());
        assertEquals(multipleLinesEnvironment.getJvmOptions().getMemory().getMx(), fromEnvironmentFile.getJvmOptions().getMemory().getMx());
        assertEquals(multipleLinesEnvironment.getJvmOptions().getMemory().getSs(), fromEnvironmentFile.getJvmOptions().getMemory().getSs());
        assertEquals(multipleLinesEnvironment.getJvmOptions().getMemory().getPermSize(),
            fromEnvironmentFile.getJvmOptions().getMemory().getPermSize());
        assertEquals(multipleLinesEnvironment.getJvmOptions().getMemory().getMs(), fromEnvironmentFile.getJvmOptions().getMemory().getMs());
    }
}
