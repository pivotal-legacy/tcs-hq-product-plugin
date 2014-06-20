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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;

public class StaticDefaultsConverter extends AbstractXmlElementConverter implements XmlElementConverter<StaticDefaults> {

    private static final String STATIC_DEFAULTS = "Static Defaults";

    private static final String SENDFILE_SIZE = "sendfileSize";

    private static final String README_FILE = "readmeFile";

    private static final String READONLY = "readonly";

    private static final String OUTPUT = "output";

    private static final String LISTINGS = "listings";

    private static final String INPUT = "input";

    private static final String FILE_ENCODING = "fileEncoding";

    private static final String DEBUG = "debug";

    public void convert(Document document, Element servlet, StaticDefaults from, Properties catalinaProperties) {
        updateServletInitParam(document, servlet, DEBUG, from.getDebug(), catalinaProperties);
        updateServletInitParam(document, servlet, FILE_ENCODING, from.getFileEncoding(), catalinaProperties);
        updateServletInitParam(document, servlet, INPUT, from.getInput(), catalinaProperties);
        updateServletInitParam(document, servlet, LISTINGS, from.getListings(), catalinaProperties);
        updateServletInitParam(document, servlet, OUTPUT, from.getOutput(), catalinaProperties);
        updateServletInitParam(document, servlet, README_FILE, from.getReadmeFile(), catalinaProperties);
        updateServletInitParam(document, servlet, READONLY, from.getReadonly(), catalinaProperties);
        updateServletInitParam(document, servlet, SENDFILE_SIZE, from.getSendfileSize(), catalinaProperties);
    }

    public StaticDefaults convert(Element servlet, Properties catalinaProperties) {
        StaticDefaults staticDefaults = new StaticDefaults();
        List<Element> initParams = getChildElements(servlet, "init-param");
        for (Element initParam : initParams) {
            final String paramName = getChildElements(initParam, "param-name").get(0).getTextContent();
            updateParameter(staticDefaults, paramName, initParam, catalinaProperties);
        }
        return staticDefaults;
    }

    private void updateParameter(StaticDefaults staticDefaults, String paramName, Element initParam, Properties catalinaProperties) {
        final String paramValue = parseProperties(getChildElements(initParam, "param-value").get(0).getTextContent(), catalinaProperties);
        if (!(EMPTY_STRING.equals(paramValue))) {
            if (DEBUG.equals(paramName)) {
                try {
                    staticDefaults.setDebug(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, DEBUG, paramValue);
                }
            } else if (FILE_ENCODING.equals(paramName)) {
                staticDefaults.setFileEncoding(paramValue);
            } else if (INPUT.equals(paramName)) {
                try {
                    staticDefaults.setInput(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, INPUT, paramValue);
                }
            } else if (LISTINGS.equals(paramName)) {
                staticDefaults.setListings(Boolean.valueOf(paramValue));
            } else if (OUTPUT.equals(paramName)) {
                try {
                    staticDefaults.setOutput(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, OUTPUT, paramValue);
                }
            } else if (READONLY.equals(paramName)) {
                staticDefaults.setReadonly(Boolean.valueOf(paramValue));
            } else if (README_FILE.equals(paramName)) {
                staticDefaults.setReadmeFile(paramValue);
            } else if (SENDFILE_SIZE.equals(paramName)) {
                try {
                    staticDefaults.setSendfileSize(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, SENDFILE_SIZE, paramValue);
                }
            }
        }
    }
}
