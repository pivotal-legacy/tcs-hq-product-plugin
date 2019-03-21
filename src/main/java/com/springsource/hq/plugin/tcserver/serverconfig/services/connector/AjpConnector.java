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

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Settings for AJP based connectors (http://tomcat.apache.org/tomcat-6.0-doc/config/ajp.html)
 * 
 * <p>
 * Java and APR AJP connectors are supported
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "ajp-connector")
public class AjpConnector extends Connector implements Validator {

    private static final String DEFAULT_PROTOCOL = "org.apache.coyote.ajp.AjpProtocol";

    private static final Boolean DEFAULT_REQUEST_USE_SECRET = Boolean.FALSE;

    /**
     * This attribute value must be AJP/1.3 to use the AJP handler.
     */
    private String protocol = DEFAULT_PROTOCOL;

    /**
     * Only requests from workers with this secret keyword will be accepted.
     */
    private String requestSecret;

    /**
     * If set to true, then a random value for request.secret will be generated. It is for use with
     * request.shutdownEnabled. This is set to false by default.
     */
    private Boolean requestUseSecret = DEFAULT_REQUEST_USE_SECRET;

    @XmlAttribute(name = "protocol")
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @XmlAttribute(name = "request-secret")
    public String getRequestSecret() {
        return requestSecret;
    }

    public void setRequestSecret(String requestSecret) {
        this.requestSecret = requestSecret;
    }

    @XmlAttribute(name = "request-use-secret")
    public Boolean getRequestUseSecret() {
        return requestUseSecret;
    }

    public void setRequestUseSecret(Boolean requestUseSecret) {
        this.requestUseSecret = requestUseSecret;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AjpConnector ajpConnector = (AjpConnector) target;
        super.validate(ajpConnector, errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AjpConnector)) {
            return false;
        }
        AjpConnector connector = (AjpConnector) obj;
        return ObjectUtils.nullSafeEquals(this.getProtocol(), connector.getProtocol())
            && ObjectUtils.nullSafeEquals(this.getRequestSecret(), connector.getRequestSecret())
            && ObjectUtils.nullSafeEquals(this.getRequestUseSecret(), connector.getRequestUseSecret())
            && ObjectUtils.nullSafeEquals(this.getAddress(), connector.getAddress())
            && ObjectUtils.nullSafeEquals(this.getConnectionTimeout(), connector.getConnectionTimeout())
            && ObjectUtils.nullSafeEquals(this.getConnectorName(), connector.getConnectorName())
            && ObjectUtils.nullSafeEquals(this.getMaxThreads(), connector.getMaxThreads())
            && ObjectUtils.nullSafeEquals(this.getPort(), connector.getPort())
            && ObjectUtils.nullSafeEquals(this.getProxyName(), connector.getProxyName())
            && ObjectUtils.nullSafeEquals(this.getProxyPort(), connector.getProxyPort())
            && ObjectUtils.nullSafeEquals(this.getRedirectPort(), connector.getRedirectPort())
            && ObjectUtils.nullSafeEquals(this.getScheme(), connector.getScheme());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.protocol) + ObjectUtils.nullSafeHashCode(this.requestSecret)
            + ObjectUtils.nullSafeHashCode(this.requestUseSecret) + super.hashCode() * 29;
    }

}
