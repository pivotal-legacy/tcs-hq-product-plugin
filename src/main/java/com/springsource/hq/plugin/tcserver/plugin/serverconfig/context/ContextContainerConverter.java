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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.context;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.StaticResourceCache;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.WebApplicationLogger;

public class ContextContainerConverter extends AbstractXmlElementConverter implements XmlElementConverter<ContextContainer> {

    private static final String CONTEXT = "Context";

    private static final String CACHING_ALLOWED = "cachingAllowed";

    private static final String CACHE_TTL = "cacheTTL";

    private static final String CACHE_MAX_SIZE = "cacheMaxSize";

    private static final String SWALLOW_OUTPUT = "swallowOutput";

    public ContextContainer convert(final Element context, final Properties catalinaProperties) {
        final ContextContainer contextContainer = new ContextContainer();
        contextContainer.setStaticResourceCache(createStaticResourceCache(context, catalinaProperties));
        contextContainer.setWebApplicationLogger(createWebApplicationLogger(context, catalinaProperties));
        return contextContainer;
    }

    private WebApplicationLogger createWebApplicationLogger(final Element context, final Properties catalinaProperties) {
        final WebApplicationLogger webApplicationLogger = new WebApplicationLogger();
        String swallowOutput = parseProperties(context.getAttribute(SWALLOW_OUTPUT), catalinaProperties);
        if (!(EMPTY_STRING.equals(swallowOutput))) {
            webApplicationLogger.setSwallowOutput(Boolean.valueOf(swallowOutput));
        }
        return webApplicationLogger;
    }

    private StaticResourceCache createStaticResourceCache(final Element context, final Properties catalinaProperties) {
        final StaticResourceCache staticResourceCache = new StaticResourceCache();
        String cacheMaxSize = parseProperties(context.getAttribute(CACHE_MAX_SIZE), catalinaProperties);
        if (!(EMPTY_STRING.equals(cacheMaxSize))) {
            try {
                staticResourceCache.setCacheMaxSize(Long.valueOf(cacheMaxSize));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONTEXT, CACHE_MAX_SIZE, cacheMaxSize);
            }
        }
        String cacheTTL = parseProperties(context.getAttribute(CACHE_TTL), catalinaProperties);
        if (!(EMPTY_STRING.equals(cacheTTL))) {
            try {
                staticResourceCache.setCacheTTL(Long.valueOf(cacheTTL));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONTEXT, CACHE_TTL, cacheTTL);
            }
        }
        String cachingAllowed = parseProperties(context.getAttribute(CACHING_ALLOWED), catalinaProperties);
        if (!(EMPTY_STRING.equals(cachingAllowed))) {
            staticResourceCache.setCachingAllowed(Boolean.valueOf(cachingAllowed));
        }
        return staticResourceCache;
    }

    public void convert(Document document, Element context, ContextContainer from, Properties catalinaProperties) {
        convertStaticResourceCache(context, from.getStaticResourceCache(), catalinaProperties);
        convertWebApplicationLogger(context, from.getWebApplicationLogger(), catalinaProperties);
    }

    private void convertStaticResourceCache(Element context, StaticResourceCache staticResourceCache, Properties catalinaProperties) {
        setAttribute(context, CACHE_MAX_SIZE, staticResourceCache.getCacheMaxSize(), catalinaProperties, false);
        setAttribute(context, CACHE_TTL, staticResourceCache.getCacheTTL(), catalinaProperties, false);
        setAttribute(context, CACHING_ALLOWED, staticResourceCache.getCachingAllowed(), catalinaProperties, false);
    }

    private void convertWebApplicationLogger(Element context, WebApplicationLogger webAppLogger, Properties catalinaProperties) {
        setAttribute(context, SWALLOW_OUTPUT, webAppLogger.getSwallowOutput(), catalinaProperties, false);
    }

}
