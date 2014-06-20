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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.general;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for general configuration settings
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "general-config", propOrder = { "serverProperties", "jmxListener", "aprLifecycleListener" })
public class GeneralConfig implements Validator, Hierarchical<Configuration> {

    private ServerProperties serverProperties;

    private JmxListener jmxListener;

    private AprLifecycleListener aprLifecycleListener;

    @XmlElement(name = "apr-lifecycle-listener", required = false)
    public AprLifecycleListener getAprLifecycleListener() {
        return aprLifecycleListener;
    }

    public void setAprLifecycleListener(AprLifecycleListener aprLifecycleListener) {
        this.aprLifecycleListener = aprLifecycleListener;
    }

    private Configuration parent;

    public GeneralConfig() {
        serverProperties = new ServerProperties();
        jmxListener = new JmxListener();
    }

    @XmlElement(name = "server-properties", required = true)
    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @XmlElement(name = "jmx-listener", required = true)
    public JmxListener getJmxListener() {
        return jmxListener;
    }

    public void setJmxListener(JmxListener jmxListener) {
        this.jmxListener = jmxListener;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        GeneralConfig generalConfig = (GeneralConfig) target;
        errors.pushNestedPath("serverProperties");
        generalConfig.getServerProperties().validate(generalConfig.getServerProperties(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("jmxListener");
        generalConfig.getJmxListener().validate(generalConfig.getJmxListener(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        serverProperties.setParent(this);
        serverProperties.applyParentToChildren();
        jmxListener.setParent(this);
        jmxListener.applyParentToChildren();
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
        if (!(obj instanceof GeneralConfig)) {
            return false;
        }
        GeneralConfig generalConfig = (GeneralConfig) obj;
        return ObjectUtils.nullSafeEquals(this.getServerProperties(), generalConfig.getServerProperties())
            && ObjectUtils.nullSafeEquals(this.getJmxListener(), generalConfig.getJmxListener())
            && ObjectUtils.nullSafeEquals(this.getAprLifecycleListener(), generalConfig.getAprLifecycleListener());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.serverProperties) * 29 + ObjectUtils.nullSafeHashCode(this.jmxListener) * 29
            + ObjectUtils.nullSafeHashCode(this.aprLifecycleListener) * 29;
    }

}
