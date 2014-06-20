// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import junit.framework.TestCase;

import org.hyperic.hq.product.PluginException;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.ServerXmlPropertiesRetriever;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlPropertiesFileRetriever;

public class ServerXmlPropertiesRetrieverTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
        final File tmpConfDir = new File(System.getProperty("java.io.tmpdir") + "/conf");
        tmpConfDir.mkdir();
        copy(File.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-server.xml"), new FileOutputStream(
            tmpConfDir.toString() + "/server.xml"));
        copy(File.class.getResourceAsStream("/com/springsource/hq/plugin/tcserver/plugin/serverconfig/test-server-with-jmx.xml"),
            new FileOutputStream(tmpConfDir.toString() + "/server2.xml"));
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

    public void testGetPropertiesFromFile() throws PluginException {
        XmlPropertiesFileRetriever propertiesRetriever = new ServerXmlPropertiesRetriever();
        Map<String, String> properties = propertiesRetriever.getPropertiesFromFile(System.getProperty("java.io.tmpdir") + "/conf/server2.xml",
            "Listener", "className", "com.springsource.tcserver.serviceability.rmi.JmxSocketListener");
        assertEquals("The bind address should be equal", "192.2.2.200", properties.get("bind"));
        assertEquals("The port should be equal", "7002", properties.get("port"));
    }

    public void testNotFoundGetPropertiesFromFile() throws PluginException {
        XmlPropertiesFileRetriever propertiesRetriever = new ServerXmlPropertiesRetriever();
        Map<String, String> properties = propertiesRetriever.getPropertiesFromFile(System.getProperty("java.io.tmpdir") + "/conf/server.xml",
            "Listener", "className", "com.springsource.tcserver.serviceability.rmi.JmxSocketListener");
        assertTrue("The properties map should be empty.", properties.isEmpty());
    }

}
