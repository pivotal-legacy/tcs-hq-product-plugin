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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults;

import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;

public class ServerDefaultsConverter extends AbstractXmlElementConverter implements XmlElementConverter<ServerDefaults> {

    private XmlElementConverter<JspDefaults> jspDefaultsConverter = new JspDefaultsConverter();

    private XmlElementConverter<StaticDefaults> staticDefaultsConverter = new StaticDefaultsConverter();

    public ServerDefaults convert(Element webApp, Properties catalinaProperties) {
        final ServerDefaults serverDefaults = new ServerDefaults();
        final List<Element> servlets = getChildElements(webApp, "servlet");
        for (int i = 0; i < servlets.size(); i++) {
            Element servlet = (Element) servlets.get(i);
            final String servletName = getChildElements(servlet, "servlet-name").get(0).getTextContent();
            if ("jsp".equals(servletName)) {
                serverDefaults.setJspDefaults(jspDefaultsConverter.convert(servlet, catalinaProperties));
            } else if ("default".equals(servletName)) {
                serverDefaults.setStaticDefaults(staticDefaultsConverter.convert(servlet, catalinaProperties));
            }
        }
        return serverDefaults;
    }

    public void convert(Document document, Element webApp, ServerDefaults from, Properties catalinaProperties) {
        final List<Element> servlets = getChildElements(webApp, "servlet");
        for (int i = 0; i < servlets.size(); i++) {
            Element servlet = (Element) servlets.get(i);
            final String servletName = getChildElements(servlet, "servlet-name").get(0).getTextContent();
            if ("jsp".equals(servletName)) {
                jspDefaultsConverter.convert(document, servlet, from.getJspDefaults(), catalinaProperties);
            } else if ("default".equals(servletName)) {
                staticDefaultsConverter.convert(document, servlet, from.getStaticDefaults(), catalinaProperties);
            }
        }
    }

}
