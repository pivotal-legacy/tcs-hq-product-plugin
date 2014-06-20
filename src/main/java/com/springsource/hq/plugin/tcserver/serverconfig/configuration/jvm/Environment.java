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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for environmental configuration options
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "environment", propOrder = { "javaHome", "jvmOptions" })
public class Environment implements Validator, Hierarchical<Configuration> {

    private String javaHome;

    private JvmOptions jvmOptions;

    private Configuration parent;

    public Environment() {
        javaHome = null;
        jvmOptions = new JvmOptions();
    }

    @XmlAttribute(name = "java-home")
    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    @XmlElement(name = "jvm-options", required = true)
    public JvmOptions getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(JvmOptions jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Environment environment = (Environment) target;
        errors.pushNestedPath("jvmOptions");
        environment.getJvmOptions().validate(environment.getJvmOptions(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        jvmOptions.setParent(this);
        jvmOptions.applyParentToChildren();
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
        if (!(obj instanceof Environment)) {
            return false;
        }
        Environment environment = (Environment) obj;
        return ObjectUtils.nullSafeEquals(this.getJavaHome(), environment.getJavaHome())
            && ObjectUtils.nullSafeEquals(this.getJvmOptions(), environment.getJvmOptions());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.javaHome) * 29 + ObjectUtils.nullSafeHashCode(this.jvmOptions) * 29;
    }

}
