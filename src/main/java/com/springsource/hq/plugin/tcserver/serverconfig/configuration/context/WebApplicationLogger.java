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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.context;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for web app logging (http://tomcat.apache.org/tomcat-6.0-doc/config/context.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "web-application-logger")
public class WebApplicationLogger implements Validator, Hierarchical<ContextContainer> {

    private static final boolean DEFAULT_SWALLOW_OUTPUT = false;

    private Boolean swallowOutput = DEFAULT_SWALLOW_OUTPUT;

    private ContextContainer parent;

    @XmlAttribute(name = "swallow-output")
    public Boolean getSwallowOutput() {
        return swallowOutput;
    }

    public void setSwallowOutput(Boolean swallowOutput) {
        this.swallowOutput = swallowOutput;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // WebApplicationLogger webApplicationLogger = (WebApplicationLogger) target;
        // TODO define validation rules
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public ContextContainer parent() {
        return parent;
    }

    public void setParent(ContextContainer parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WebApplicationLogger)) {
            return false;
        }
        WebApplicationLogger webApplicationLogger = (WebApplicationLogger) obj;
        return ObjectUtils.nullSafeEquals(this.getSwallowOutput(), webApplicationLogger.getSwallowOutput());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.swallowOutput) * 29;
    }

}
