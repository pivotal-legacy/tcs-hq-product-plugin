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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

/**
 * Holder for all configuration related settings
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "configuration", propOrder = { "generalConfig", "environment", "contextContainer", "serverDefaults" })
public class Configuration implements Validator, Hierarchical<Settings> {

    private GeneralConfig generalConfig;

    private Environment environment;

    private ContextContainer contextContainer;

    private ServerDefaults serverDefaults;

    private Settings parent;

    public Configuration() {
        generalConfig = new GeneralConfig();
        environment = new Environment();
        contextContainer = new ContextContainer();
        serverDefaults = new ServerDefaults();
    }

    @XmlElement(name = "general-config", required = true)
    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    @XmlElement(name = "environment", required = true)
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @XmlElement(name = "context-container", required = true)
    public ContextContainer getContextContainer() {
        return contextContainer;
    }

    public void setContextContainer(ContextContainer contextContainer) {
        this.contextContainer = contextContainer;
    }

    @XmlElement(name = "server-defaults", required = true)
    public ServerDefaults getServerDefaults() {
        return serverDefaults;
    }

    public void setServerDefaults(ServerDefaults serverDefaults) {
        this.serverDefaults = serverDefaults;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Configuration configuration = (Configuration) target;
        errors.pushNestedPath("generalConfig");
        configuration.getGeneralConfig().validate(configuration.getGeneralConfig(), errors);
        errors.popNestedPath();
        if (configuration.getEnvironment() != null) {
            errors.pushNestedPath("environment");
            configuration.getEnvironment().validate(configuration.getEnvironment(), errors);
            errors.popNestedPath();
        }
        errors.pushNestedPath("contextContainer");
        configuration.getContextContainer().validate(configuration.getContextContainer(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("serverDefaults");
        configuration.getServerDefaults().validate(configuration.getServerDefaults(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        contextContainer.setParent(this);
        contextContainer.applyParentToChildren();
        generalConfig.setParent(this);
        generalConfig.applyParentToChildren();
        if (environment != null) {
            environment.setParent(this);
            environment.applyParentToChildren();
        }
        serverDefaults.setParent(this);
        serverDefaults.applyParentToChildren();
    }

    public Settings parent() {
        return parent;
    }

    public void setParent(Settings parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Configuration)) {
            return false;
        }
        Configuration configuration = (Configuration) obj;
        return ObjectUtils.nullSafeEquals(this.getContextContainer(), configuration.getContextContainer())
            && ObjectUtils.nullSafeEquals(this.getGeneralConfig(), configuration.getGeneralConfig())
            && ObjectUtils.nullSafeEquals(this.getEnvironment(), configuration.getEnvironment())
            && ObjectUtils.nullSafeEquals(this.getServerDefaults(), configuration.getServerDefaults());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.contextContainer) * 29 + ObjectUtils.nullSafeHashCode(this.generalConfig) * 29
            + ObjectUtils.nullSafeHashCode(this.environment) * 29 + ObjectUtils.nullSafeHashCode(this.serverDefaults) * 29;
    }

}
