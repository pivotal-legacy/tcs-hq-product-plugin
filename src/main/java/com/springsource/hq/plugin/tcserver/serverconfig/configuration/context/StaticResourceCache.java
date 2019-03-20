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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for cacheing of static resources (https://tomcat.apache.org/tomcat-6.0-doc/config/context.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "static-resource-cache")
public class StaticResourceCache implements Validator, Hierarchical<ContextContainer> {

    private static final long DEFAULT_CACHE_MAX_SIZE = 10240L;

    private static final long DEFAULT_CACHE_TTL = 5000L;

    private static final boolean DEFAULT_CACHING_ALLOWED = true;

    private Long cacheMaxSize = DEFAULT_CACHE_MAX_SIZE;

    private Long cacheTTL = DEFAULT_CACHE_TTL;

    private Boolean cachingAllowed = DEFAULT_CACHING_ALLOWED;

    private ContextContainer parent;

    public void applyParentToChildren() {
        // no-op, no children
    }

    @XmlAttribute(name = "cache-max-size")
    public Long getCacheMaxSize() {
        return cacheMaxSize;
    }

    @XmlAttribute(name = "cache-ttl")
    public Long getCacheTTL() {
        return cacheTTL;
    }

    @XmlAttribute(name = "caching-allowed")
    public Boolean getCachingAllowed() {
        return cachingAllowed;
    }

    public ContextContainer parent() {
        return parent;
    }

    public void setCacheMaxSize(Long cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public void setCacheTTL(Long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public void setCachingAllowed(Boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
    }

    public void setParent(ContextContainer parent) {
        this.parent = parent;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        StaticResourceCache staticResourceCache = (StaticResourceCache) target;

        if (staticResourceCache.getCachingAllowed()) {
            if (staticResourceCache.getCacheMaxSize() < 1) {
                errors.rejectValue("cacheMaxSize", "configuration.context.static.cacheMaxSize.tooLow");
            }

            if (staticResourceCache.getCacheTTL() < 1) {
                errors.rejectValue("cacheTTL", "configuration.context.static.cacheTTL.tooLow");
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StaticResourceCache)) {
            return false;
        }
        StaticResourceCache staticResourceCache = (StaticResourceCache) obj;
        return ObjectUtils.nullSafeEquals(this.getCacheMaxSize(), staticResourceCache.getCacheMaxSize())
            && ObjectUtils.nullSafeEquals(this.getCacheTTL(), staticResourceCache.getCacheTTL())
            && ObjectUtils.nullSafeEquals(this.getCachingAllowed(), staticResourceCache.getCachingAllowed());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.cacheMaxSize) * 29 + ObjectUtils.nullSafeHashCode(this.cacheTTL) * 29
            + ObjectUtils.nullSafeHashCode(this.cachingAllowed) * 29;
    }

}
