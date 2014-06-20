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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General server properties (http://tomcat.apache.org/tomcat-6.0-doc/config/server.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "server-properties")
public class ServerProperties implements Validator, Hierarchical<GeneralConfig> {

    private static final long DEFAULT_PORT = 8005l;

    private static final String DEFAULT_SHUTDOWN = "SHUTDOWN";

    private Long port = DEFAULT_PORT;

    private String shutdown = DEFAULT_SHUTDOWN;

    private GeneralConfig parent;

    @XmlAttribute(name = "port")
    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    @XmlAttribute(name = "shutdown")
    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ServerProperties serverProperties = (ServerProperties) target;
        if (!errors.hasFieldErrors("port")) {
            if (serverProperties.getPort() == null) {
                errors.rejectValue("port", "configuration.generalConfig.serverProperties.port.required");
            } else if (serverProperties.getPort() < -1 || serverProperties.getPort() > 65535) {
                errors.rejectValue("port", "invalid.shutdownPort");
            }
        }
        if (!errors.hasFieldErrors("shutdown")) {
            if (!StringUtils.hasText(serverProperties.getShutdown())) {
                errors.rejectValue("shutdown", "configuration.generalConfig.serverProperties.shutdown.required");
            }
        }
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public GeneralConfig parent() {
        return parent;
    }

    public void setParent(GeneralConfig parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServerProperties)) {
            return false;
        }
        ServerProperties serverProperties = (ServerProperties) obj;
        return ObjectUtils.nullSafeEquals(this.getPort(), serverProperties.getPort())
            && ObjectUtils.nullSafeEquals(this.getShutdown(), serverProperties.getShutdown());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.port) * 29 + ObjectUtils.nullSafeHashCode(this.shutdown) * 29;
    }

}
