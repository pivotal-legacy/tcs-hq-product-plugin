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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.context;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for context container settings
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "context-container", propOrder = { "staticResourceCache", "webApplicationLogger" })
public class ContextContainer implements Validator, Hierarchical<Configuration> {

    private StaticResourceCache staticResourceCache;

    private WebApplicationLogger webApplicationLogger;

    private Configuration parent;

    public ContextContainer() {
        staticResourceCache = new StaticResourceCache();
        webApplicationLogger = new WebApplicationLogger();
    }

    @XmlElement(name = "static-resource-cache", required = true)
    public StaticResourceCache getStaticResourceCache() {
        return staticResourceCache;
    }

    public void setStaticResourceCache(StaticResourceCache staticResourceCache) {
        this.staticResourceCache = staticResourceCache;
    }

    @XmlElement(name = "web-application-logger", required = true)
    public WebApplicationLogger getWebApplicationLogger() {
        return webApplicationLogger;
    }

    public void setWebApplicationLogger(WebApplicationLogger webApplicationLogger) {
        this.webApplicationLogger = webApplicationLogger;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ContextContainer contextContainer = (ContextContainer) target;
        errors.pushNestedPath("staticResourceCache");
        contextContainer.getStaticResourceCache().validate(contextContainer.getStaticResourceCache(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("webApplicationLogger");
        contextContainer.getWebApplicationLogger().validate(contextContainer.getWebApplicationLogger(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        staticResourceCache.setParent(this);
        staticResourceCache.applyParentToChildren();
        webApplicationLogger.setParent(this);
        webApplicationLogger.applyParentToChildren();
    }

    public Configuration parent() {
        return parent;
    }

    public void setParent(Configuration parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContextContainer)) {
            return false;
        }
        ContextContainer contextContainer = (ContextContainer) obj;
        return ObjectUtils.nullSafeEquals(this.getStaticResourceCache(), contextContainer.getStaticResourceCache())
            && ObjectUtils.nullSafeEquals(this.getWebApplicationLogger(), contextContainer.getWebApplicationLogger());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.staticResourceCache) * 29 + ObjectUtils.nullSafeHashCode(this.webApplicationLogger) * 29;
    }

}
