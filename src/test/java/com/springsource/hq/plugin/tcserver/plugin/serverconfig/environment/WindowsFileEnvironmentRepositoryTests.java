// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public class WindowsFileEnvironmentRepositoryTests extends TestCase {

    private WindowsFileEnvironmentRepository environmentRepository;

    private WindowsFileReadingEnvironmentFactory environmentFactory;

    String backupFileName;

    public void setUp() throws Exception {
        environmentRepository = new WindowsFileEnvironmentRepository();
        environmentFactory = new WindowsFileReadingEnvironmentFactory();

        final File tmpConfDir = new File(System.getProperty("java.io.tmpdir") + "/conf");
        tmpConfDir.mkdir();
        copy(File.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/test-wrapper.conf"),
            new FileOutputStream(tmpConfDir.toString() + "/wrapper.conf"));
        backupFileName = tmpConfDir.toString() + "/wrapper.conf.bak";
        copy(File.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/test-wrapper.conf"),
            new FileOutputStream(backupFileName));
    }

    void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void testSave_javaHomeNotNull() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));
        Environment newEnvironment = new Environment();
        newEnvironment.setJavaHome("c:\\my\\java\\home");

        environmentRepository.save(backupFileName, config, newEnvironment);

        Environment reflectiveEnvironment = environmentFactory.create(config);
        assertEquals("c:\\my\\java\\home", reflectiveEnvironment.getJavaHome());
    }

    public void testSave_javaHomeNull() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));
        Environment newEnvironment = new Environment();
        newEnvironment.setJavaHome(null);

        environmentRepository.save(backupFileName, config, newEnvironment);

        Environment reflectiveEnvironment = environmentFactory.create(config);
        assertNull(reflectiveEnvironment.getJavaHome());
    }

    public void testSave_jvmOpts() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));
        Environment newEnvironment = new Environment();
        newEnvironment.getJvmOptions().getMemory().setMx(1024L);
        newEnvironment.getJvmOptions().getMemory().setMs(2048L);
        newEnvironment.getJvmOptions().getMemory().setSs(256L);
        StringBuilder commandLineOptsBuilder = new StringBuilder();
        commandLineOptsBuilder.append("-Dmy.java.opts=\"foo bar\" ");
        commandLineOptsBuilder.append("-Dmy.other.java.opts=\"c:\\Program Files\\directory\\location\"");
        newEnvironment.getJvmOptions().getAdvanced().setCliArgs(commandLineOptsBuilder.toString());

        environmentRepository.save(backupFileName, config, newEnvironment);

        Environment reflectiveEnvironment = environmentFactory.create(config);
        assertEquals(Long.valueOf(1024L), reflectiveEnvironment.getJvmOptions().getMemory().getMx());
        assertEquals(Long.valueOf(2048L), reflectiveEnvironment.getJvmOptions().getMemory().getMs());
        assertEquals(Long.valueOf(256L), reflectiveEnvironment.getJvmOptions().getMemory().getSs());
        assertNull(reflectiveEnvironment.getJvmOptions().getMemory().getNewSize());
        assertEquals(commandLineOptsBuilder.toString(), reflectiveEnvironment.getJvmOptions().getAdvanced().getCliArgs());
    }

    public void testSave_reflective() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));

        Environment environment = environmentFactory.create(config);
        environment.getJvmOptions().getGarbageCollection().setMaxGCPauseMillis(1L);
        environmentRepository.save(backupFileName, config, environment);
        Environment newEnvironment = environmentFactory.create(config);

        assertEquals(environment, newEnvironment);
        assertEquals(Long.valueOf(1L), newEnvironment.getJvmOptions().getGarbageCollection().getMaxGCPauseMillis());
    }

}
