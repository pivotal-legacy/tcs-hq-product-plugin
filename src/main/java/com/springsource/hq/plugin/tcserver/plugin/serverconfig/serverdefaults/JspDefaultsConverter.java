/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;

public class JspDefaultsConverter extends AbstractXmlElementConverter implements XmlElementConverter<JspDefaults> {

    private static final String JSP_DEFAULTS = "JSP Defaults";

    private static final int CHECK_INTERVAL = 0;

    private static final int CLASS_DEBUG_INFO = 1;

    private static final int COMPILER = 2;

    private static final int COMPILER_SOURCE_VM = 3;

    private static final int COMPILER_TARGET_VM = 4;

    private static final int DEVELOPMENT = 5;

    private static final int DISPLAY_SOURCE_FRAGMENT = 6;

    private static final int DUMP_SMAP = 7;

    private static final int ENABLE_POOLING = 8;

    private static final int ENGINE_OPTIONS_CLASS = 9;

    private static final int ERROR_ON_USE_BEAN_INVALID_CLASS = 10;

    private static final int FORK = 11;

    private static final int GEN_STR_AS_CHAR_ARRAY = 12;

    private static final int IE_CLASS_ID = 13;

    private static final int JAVA_ENCODING = 14;

    private static final int KEEP_GENERATED = 15;

    private static final int MAPPED_FILE = 16;

    private static final int MODIFICATION_TEST_INTERVAL = 17;

    private static final List<String> PARAM_NAMES;

    private static final String[] PARAM_NAMES_ARRAY = new String[] { "checkInterval", "classdebuginfo", "compiler", "compilerSourceVM",
        "compilerTargetVM", "development", "displaySourceFragment", "dumpSmap", "enablePooling", "engineOptionsClass",
        "errorOnUseBeanInvalidClassAttribute", "fork", "genStrAsCharArray", "ieClassId", "javaEncoding", "keepgenerated", "mappedfile",
        "modificationTestInterval", "scratchdir", "suppressSmap", "trimSpaces", "xpoweredBy" };

    private static final int SCRATCH_DIR = 18;

    private static final int SUPPRESS_SMAP = 19;

    private static final int TRIM_SPACES = 20;

    private static final int X_POWERED_BY = 21;

    static {
        PARAM_NAMES = new ArrayList<String>(Arrays.asList(PARAM_NAMES_ARRAY));
    }

    public JspDefaults convert(Element servlet, Properties catalinaProperties) {
        final JspDefaults jspDefaults = new JspDefaults();
        List<Element> initParams = getChildElements(servlet, "init-param");
        for (Element initParam : initParams) {
            final String paramName = getChildElements(initParam, "param-name").get(0).getTextContent();
            updateParameter(jspDefaults, paramName, initParam, catalinaProperties);
        }
        return jspDefaults;
    }

    public void convert(Document document, Element servlet, JspDefaults from, Properties catalinaProperties) {
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[CHECK_INTERVAL], from.getCheckInterval(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[CLASS_DEBUG_INFO], from.getClassdebuginfo(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[COMPILER], from.getCompiler(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[COMPILER_SOURCE_VM], from.getCompilerSourceVM(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[COMPILER_TARGET_VM], from.getCompilerTargetVM(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[DEVELOPMENT], from.getDevelopment(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[DISPLAY_SOURCE_FRAGMENT], from.getDisplaySourceFragment(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[DUMP_SMAP], from.getDumpSmap(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[ENABLE_POOLING], from.getEnablePooling(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[ENGINE_OPTIONS_CLASS], from.getEngineOptionsClass(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[ERROR_ON_USE_BEAN_INVALID_CLASS], from.getErrorOnUseBeanInvalidClassAttribute(),
            catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[FORK], from.getFork(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[GEN_STR_AS_CHAR_ARRAY], from.getGenStrAsCharArray(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[IE_CLASS_ID], from.getIeClassId(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[JAVA_ENCODING], from.getJavaEncoding(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[KEEP_GENERATED], from.getKeepgenerated(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[MAPPED_FILE], from.getMappedfile(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[MODIFICATION_TEST_INTERVAL], from.getModificationTestInterval(),
            catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[SCRATCH_DIR], from.getScratchdir(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[SUPPRESS_SMAP], from.getSuppressSmap(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[TRIM_SPACES], from.getTrimSpaces(), catalinaProperties);
        updateServletInitParam(document, servlet, PARAM_NAMES_ARRAY[X_POWERED_BY], from.getXpoweredBy(), catalinaProperties);
    }

    private void updateParameter(JspDefaults jspDefaults, String paramName, Element initParam, Properties catalinaProperties) {
        final int index = PARAM_NAMES.indexOf(paramName);
        if (index != -1) {
            final String paramValue = parseProperties(getChildElements(initParam, "param-value").get(0).getTextContent(), catalinaProperties);
            if (!(EMPTY_STRING.equals(paramValue))) {
                switch (index) {
                    case CHECK_INTERVAL:
                        try {
                            jspDefaults.setCheckInterval(Long.valueOf(paramValue));
                        } catch (NumberFormatException e) {
                            logNonNumericValue(JSP_DEFAULTS, PARAM_NAMES_ARRAY[CHECK_INTERVAL], paramValue);
                        }
                        break;
                    case CLASS_DEBUG_INFO:
                        jspDefaults.setClassdebuginfo(Boolean.valueOf(paramValue));
                        break;
                    case COMPILER:
                        jspDefaults.setCompiler(paramValue);
                        break;
                    case COMPILER_SOURCE_VM:
                        jspDefaults.setCompilerSourceVM(paramValue);
                        break;
                    case COMPILER_TARGET_VM:
                        jspDefaults.setCompilerTargetVM(paramValue);
                        break;
                    case DEVELOPMENT:
                        jspDefaults.setDevelopment(Boolean.valueOf(paramValue));
                        break;
                    case DISPLAY_SOURCE_FRAGMENT:
                        jspDefaults.setDisplaySourceFragment(Boolean.valueOf(paramValue));
                        break;
                    case DUMP_SMAP:
                        jspDefaults.setDumpSmap(Boolean.valueOf(paramValue));
                        break;
                    case ENABLE_POOLING:
                        jspDefaults.setEnablePooling(Boolean.valueOf(paramValue));
                        break;
                    case ENGINE_OPTIONS_CLASS:
                        jspDefaults.setEngineOptionsClass(paramValue);
                        break;
                    case ERROR_ON_USE_BEAN_INVALID_CLASS:
                        jspDefaults.setErrorOnUseBeanInvalidClassAttribute(Boolean.valueOf(paramValue));
                        break;
                    case FORK:
                        jspDefaults.setFork(Boolean.valueOf(paramValue));
                        break;
                    case GEN_STR_AS_CHAR_ARRAY:
                        jspDefaults.setGenStrAsCharArray(Boolean.valueOf(paramValue));
                        break;
                    case IE_CLASS_ID:
                        jspDefaults.setIeClassId(paramValue);
                        break;
                    case JAVA_ENCODING:
                        jspDefaults.setJavaEncoding(paramValue);
                        break;
                    case KEEP_GENERATED:
                        jspDefaults.setKeepgenerated(Boolean.valueOf(paramValue));
                        break;
                    case MAPPED_FILE:
                        jspDefaults.setMappedfile(Boolean.valueOf(paramValue));
                        break;
                    case MODIFICATION_TEST_INTERVAL:
                        try {
                            jspDefaults.setModificationTestInterval(Long.valueOf(paramValue));
                        } catch (NumberFormatException e) {
                            logNonNumericValue(JSP_DEFAULTS, PARAM_NAMES_ARRAY[MODIFICATION_TEST_INTERVAL], paramValue);
                        }
                        break;
                    case SCRATCH_DIR:
                        jspDefaults.setScratchdir(paramValue);
                        break;
                    case SUPPRESS_SMAP:
                        jspDefaults.setSuppressSmap(Boolean.valueOf(paramValue));
                        break;
                    case TRIM_SPACES:
                        jspDefaults.setTrimSpaces(Boolean.valueOf(paramValue));
                        break;
                    case X_POWERED_BY:
                        jspDefaults.setXpoweredBy(Boolean.valueOf(paramValue));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
