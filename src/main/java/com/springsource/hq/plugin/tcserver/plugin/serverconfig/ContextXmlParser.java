/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ContextXmlParser extends AbstractXmlParser {

    public Element parse(Document document) throws PluginException {
        final NodeList contexts = document.getElementsByTagName("Context");
        if (contexts.getLength() > 1) {
            throw new PluginException(
                "Unable to read existing tc Runtime configuration.  An error occurred parsing server.xml. Multiple Context elements found.");
        }
        if (contexts.getLength() == 0) {
            throw new PluginException(
                "Unable to read existing tc Runtime configuration.  An error occurred parsing context.xml.  No Context element found.");
        }
        final Element context = (Element) contexts.item(0);
        return context;
    }

    public void writeDocument(Document document, ConfigResponse config) throws TransformerException, IOException {
        writeDocument(document, Metric.decode(config.getValue("installpath")) + "/conf/context.xml");
    }

    public Document createDocument(ConfigResponse config) throws ParserConfigurationException, SAXException, IOException {
        return createDocument(Metric.decode(config.getValue("installpath")) + "/conf/context.xml");
    }

}
