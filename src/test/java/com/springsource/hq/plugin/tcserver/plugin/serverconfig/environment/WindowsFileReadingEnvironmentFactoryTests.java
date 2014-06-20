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

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.WindowsFileReadingEnvironmentFactory;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public class WindowsFileReadingEnvironmentFactoryTests extends TestCase {

    private WindowsFileReadingEnvironmentFactory environmentFactory;

    public void setUp() throws Exception {
        environmentFactory = new WindowsFileReadingEnvironmentFactory();

        final File tmpConfDir = new File(System.getProperty("java.io.tmpdir") + "/conf");
        tmpConfDir.mkdir();
        copy(File.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/test-wrapper.conf"),
            new FileOutputStream(tmpConfDir.toString() + "/wrapper.conf"));
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

    public void testCreate() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));

        Environment environment = environmentFactory.create(config);

        assertEquals("c:\\path\\to\\java", environment.getJavaHome());

        assertTrue(environment.getJvmOptions().getGeneral().getServer());
        assertEquals(Long.valueOf(512), environment.getJvmOptions().getMemory().getMx());
        assertEquals(Long.valueOf(192), environment.getJvmOptions().getMemory().getSs());

        assertEquals("-Dmy.totally.random.jvm.opt=blah", environment.getJvmOptions().getAdvanced().getCliArgs());
    }

}
