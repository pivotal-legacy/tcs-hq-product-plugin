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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for static and dynamic server defaults
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "server-defaults", propOrder = { "jspDefaults", "staticDefaults" })
public class ServerDefaults implements Validator, Hierarchical<Configuration> {

    private JspDefaults jspDefaults;

    private StaticDefaults staticDefaults;

    private Configuration parent;

    public ServerDefaults() {
        jspDefaults = new JspDefaults();
        staticDefaults = new StaticDefaults();
    }

    @XmlElement(name = "jsp-defaults", required = true)
    public JspDefaults getJspDefaults() {
        return jspDefaults;
    }

    public void setJspDefaults(JspDefaults jspDefaults) {
        this.jspDefaults = jspDefaults;
    }

    @XmlElement(name = "static-defaults", required = true)
    public StaticDefaults getStaticDefaults() {
        return staticDefaults;
    }

    public void setStaticDefaults(StaticDefaults staticDefaults) {
        this.staticDefaults = staticDefaults;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ServerDefaults serverDefaults = (ServerDefaults) target;
        errors.pushNestedPath("jspDefaults");
        serverDefaults.getJspDefaults().validate(serverDefaults.getJspDefaults(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("staticDefaults");
        serverDefaults.getStaticDefaults().validate(serverDefaults.getStaticDefaults(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        jspDefaults.setParent(this);
        jspDefaults.applyParentToChildren();
        staticDefaults.setParent(this);
        staticDefaults.applyParentToChildren();
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
        if (!(obj instanceof ServerDefaults)) {
            return false;
        }
        ServerDefaults serverDefaults = (ServerDefaults) obj;
        return ObjectUtils.nullSafeEquals(this.getJspDefaults(), serverDefaults.getJspDefaults())
            && ObjectUtils.nullSafeEquals(this.getStaticDefaults(), serverDefaults.getStaticDefaults());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.jspDefaults) * 29 + ObjectUtils.nullSafeHashCode(this.staticDefaults) * 29;
    }

}
