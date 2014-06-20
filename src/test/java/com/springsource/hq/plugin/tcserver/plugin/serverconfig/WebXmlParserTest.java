// Copyright (c) 2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults.ServerDefaultsConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

public class WebXmlParserTest {

    @Test
    public void testDefaultWebXml() throws Exception {
        String fileName = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/default-web.xml";

        WebXmlParser parser = new WebXmlParser();

        Document document = parser.createDocument(fileName);

        Element element = parser.parse(document);

        ServerDefaultsConverter converter = new ServerDefaultsConverter();
        ServerDefaults serverDefaults = converter.convert(element, null);

        Assert.assertEquals(0, serverDefaults.getJspDefaults().getCheckInterval().longValue());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getClassdebuginfo());
        Assert.assertEquals(null, serverDefaults.getJspDefaults().getCompiler());
        Assert.assertEquals(null, serverDefaults.getJspDefaults().getCompilerSourceVM());
        Assert.assertEquals(null, serverDefaults.getJspDefaults().getCompilerTargetVM());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getDevelopment());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getDisplaySourceFragment());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getDumpSmap());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getEnablePooling());
        Assert.assertEquals(null, serverDefaults.getJspDefaults().getEngineOptionsClass());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getErrorOnUseBeanInvalidClassAttribute());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getFork());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getGenStrAsCharArray());
        Assert.assertEquals("clsid:8AD9C840-044E-11D1-B3E9-00805F499D93", serverDefaults.getJspDefaults().getIeClassId());
        Assert.assertEquals("UTF8", serverDefaults.getJspDefaults().getJavaEncoding());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getKeepgenerated());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getMappedfile());
        Assert.assertEquals(0, serverDefaults.getJspDefaults().getModificationTestInterval().longValue());
        Assert.assertEquals(null, serverDefaults.getJspDefaults().getScratchdir());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getSuppressSmap());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getTrimSpaces());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getXpoweredBy());

        Assert.assertEquals(0, serverDefaults.getStaticDefaults().getDebug().longValue());
        Assert.assertEquals(null, serverDefaults.getStaticDefaults().getFileEncoding());
        Assert.assertEquals(2048, serverDefaults.getStaticDefaults().getInput().longValue());
        Assert.assertEquals(false, serverDefaults.getStaticDefaults().getListings().booleanValue());
        Assert.assertEquals(2048, serverDefaults.getStaticDefaults().getOutput().longValue());
        Assert.assertEquals(null, serverDefaults.getStaticDefaults().getReadmeFile());
        Assert.assertEquals(true, serverDefaults.getStaticDefaults().getReadonly().booleanValue());
        Assert.assertEquals(48, serverDefaults.getStaticDefaults().getSendfileSize().longValue());
    }

    @Test
    public void testingAlteredWebXml() throws Exception {
        String fileName = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/altered-web.xml";

        WebXmlParser parser = new WebXmlParser();

        Document document = parser.createDocument(fileName);

        Element element = parser.parse(document);

        ServerDefaultsConverter converter = new ServerDefaultsConverter();
        ServerDefaults serverDefaults = converter.convert(element, null);

        Assert.assertEquals(5, serverDefaults.getJspDefaults().getCheckInterval().longValue());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getClassdebuginfo());
        Assert.assertEquals("alteredCompiler", serverDefaults.getJspDefaults().getCompiler());
        Assert.assertEquals("alteredCompilerSourceVM", serverDefaults.getJspDefaults().getCompilerSourceVM());
        Assert.assertEquals("alteredCompilerTargetVM", serverDefaults.getJspDefaults().getCompilerTargetVM());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getDevelopment());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getDisplaySourceFragment());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getDumpSmap());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getEnablePooling());
        Assert.assertEquals("alteredEngineOptionsClass", serverDefaults.getJspDefaults().getEngineOptionsClass());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getErrorOnUseBeanInvalidClassAttribute());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getFork());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getGenStrAsCharArray());
        Assert.assertEquals("altered", serverDefaults.getJspDefaults().getIeClassId());
        Assert.assertEquals("ASCII", serverDefaults.getJspDefaults().getJavaEncoding());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getKeepgenerated());
        Assert.assertEquals(Boolean.FALSE, serverDefaults.getJspDefaults().getMappedfile());
        Assert.assertEquals(6, serverDefaults.getJspDefaults().getModificationTestInterval().longValue());
        Assert.assertEquals("alteredScratchDir", serverDefaults.getJspDefaults().getScratchdir());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getSuppressSmap());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getTrimSpaces());
        Assert.assertEquals(Boolean.TRUE, serverDefaults.getJspDefaults().getXpoweredBy());

        Assert.assertEquals(2, serverDefaults.getStaticDefaults().getDebug().longValue());
        Assert.assertEquals("alteredFileEncoding", serverDefaults.getStaticDefaults().getFileEncoding());
        Assert.assertEquals(4096, serverDefaults.getStaticDefaults().getInput().longValue());
        Assert.assertEquals(true, serverDefaults.getStaticDefaults().getListings().booleanValue());
        Assert.assertEquals(1024, serverDefaults.getStaticDefaults().getOutput().longValue());
        Assert.assertEquals("alteredReadmeFile", serverDefaults.getStaticDefaults().getReadmeFile());
        Assert.assertEquals(false, serverDefaults.getStaticDefaults().getReadonly().booleanValue());
        Assert.assertEquals(24, serverDefaults.getStaticDefaults().getSendfileSize().longValue());
    }

}
