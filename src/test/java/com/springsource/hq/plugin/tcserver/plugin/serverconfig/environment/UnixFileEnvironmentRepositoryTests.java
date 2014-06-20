// Copyright (c) 2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Advanced;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Memory;

public final class UnixFileEnvironmentRepositoryTests {

    private final EnvironmentRepository environmentRepository = new UnixFileEnvironmentRepository();

    private Environment smallEnvironment;

    private Environment largeEnvironment;

    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/expected-jvm-opts-setenv.sh";

    private static final String EXPECTED_LARGE_PATH = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/expected-large-setenv.sh";

    public UnixFileEnvironmentRepositoryTests() {
        setupSmallEnvironment();
        setupLargeEnvironment();
    }

    private void setupSmallEnvironment() {
        this.smallEnvironment = new Environment();
        JvmOptions jvmOptions = new JvmOptions();
        this.smallEnvironment.setJvmOptions(jvmOptions);
        Advanced advanced = new Advanced();
        advanced.setCliArgs("-Dalpha=bravo");
        jvmOptions.setAdvanced(advanced);
        Memory memory = new Memory();
        memory.setMx(512L);
        jvmOptions.setMemory(memory);
    }

    private void setupLargeEnvironment() {
        this.largeEnvironment = new Environment();
        JvmOptions jvmOptions = new JvmOptions();
        this.largeEnvironment.setJvmOptions(jvmOptions);
        Advanced advanced = new Advanced();
        advanced.setCliArgs("-d64 -XX:NewRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseTLAB -XX:+DisableExplicitGC -XX:+CMSIncrementalMode -XX:+CMSClassUnloadingEnabled -XX:+UseCompressedOops  -Djava.awt.headless=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dcom.services.logging.disableRemoteList=true -Dcom.services.logging.disableRemoteLogging=true -Dcom.log.config.ignoreContextClassLoader=true -Dwebreportstudio.file.cleanup.interval=60 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL -Dcom.log.config.url=\"file:////install/cfg1/config/Lev1/Web/Common/LogConfig\" -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dmulticast_udp_ip_ttl=0 -Dmulticast.address=239.1.1.1 -Dmulticast.port=31001 -Djms.authentication.decorator=false -Dscs.host=rdcesx05058.race.com -Dscs.port=8080 -Dcs.repository.dir=\"/install/Server1_1/Repository\" -Dcom.server.isclustered=false -Dcontainer.identifier=vfabrictcsvr -Dcache.locators=rdcesx05058.race.com[41415] -Dspring.profiles.active=locators -Dauto.publish.port=8080 -Dappserver.instance.id=Server1_1_rdcesx05058.race.com -Dconfig.lev.web.appserver.logs.dir=\"/install/cfg1/config/Lev1/Web/Logs/Server1_1\" -Djava.security.auth.login.config=\"/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/conf/jaas.config\" -Dmetadata.use.cluster.properties=true -Ddeploy.dir=\"/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/_webapps\" -Ddeployment.agent.client.config=\"/install/cfg1/Home/RemoteDeploymentAgentClient/2.1/deployagtclt.properties\"");
        jvmOptions.setAdvanced(advanced);
        Memory memory = new Memory();
        memory.setMx(4096L);
        memory.setMs(1024l);
        memory.setPermSize(768l);
        memory.setMaxPermSize(1024l);
        memory.setSs(256l);
        jvmOptions.setMemory(memory);
    }

    @Test
    public void saveWithBackupWithSingleLineJvmOpts() throws PluginException {
        doTest("single-line-jvm-opts-setenv.sh", EXPECTED_OUTPUT_PATH, smallEnvironment);
    }

    @Test
    public void saveWithBackupWithNoJvmOpts() throws PluginException {
        doTest("no-jvm-opts-setenv.sh", EXPECTED_OUTPUT_PATH, smallEnvironment);
    }

    @Test
    public void saveWithBackupWithMultiLineJvmOpts() throws PluginException {
        doTest("multi-line-jvm-opts-setenv.sh", EXPECTED_OUTPUT_PATH, smallEnvironment);
    }

    @Test
    public void saveWithBackupWithMultiLineWithBackSlashesJvmOpts() throws PluginException {
        doTest("multi-line-jvm-opts-with-backslashes-setenv.sh", EXPECTED_OUTPUT_PATH, smallEnvironment);
    }

    // @Test
    public void setenvWithDoubleQuotedProperties() throws PluginException {
        doTest("setenv-with-double-quoted-props.sh", EXPECTED_LARGE_PATH, largeEnvironment);
    }

    private void doTest(String inputSetEnvSh, String expectedPath, Environment environment) throws PluginException {
        new File("target/bin/setenv.sh").delete();
        File bin = new File("target/bin");
        bin.mkdirs();

        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", Metric.encode("target"));
        this.environmentRepository.save("src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/" + inputSetEnvSh,
            config, environment);

        assertJvmOpts(expectedPath);

    }

    private void assertJvmOpts(String expected) {
        File setenv = new File("target/bin/setenv.sh");
        assertTrue("setenv.sh does no exist", setenv.exists());

        List<String> actualLines = readLines(setenv);
        List<String> expectedLines = readLines(new File(expected));

        assertEquals(expectedLines, actualLines);
    }

    private List<String> readLines(File file) {

        BufferedReader reader = null;

        List<String> lines = new ArrayList<String>();

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {

                }
            }
        }
        return lines;
    }
}
