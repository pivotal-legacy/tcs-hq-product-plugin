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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.general;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * JMX Socket Listener
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jmx-listener")
public class JmxListener implements Validator, Hierarchical<GeneralConfig> {

    private static final Long DEFAULT_PORT = 6969L;

    private static final String DEFAULT_BIND = "localhost";

    private static final Boolean DEFAULT_USE_JDK_CLIENT_FACTORY = true;

    private Boolean enabled;

    private Long port = DEFAULT_PORT;

    private String bind = DEFAULT_BIND;

    private String accessFile;

    private Boolean authenticate;

    private String cipherSuites;

    private Boolean clientAuth;

    private String keystoreFile;

    private String keystorePass;

    private String passwordFile;

    private String protocols;

    private String truststoreFile;

    private String truststorePass;

    private Boolean useJdkClientFactory = DEFAULT_USE_JDK_CLIENT_FACTORY;

    private Boolean useSSL;

    private GeneralConfig parent;

    @XmlAttribute(name = "enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @XmlAttribute(name = "port")
    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    @XmlAttribute(name = "bind")
    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    @XmlAttribute(name = "access-file")
    public String getAccessFile() {
        return accessFile;
    }

    public void setAccessFile(String accessFile) {
        this.accessFile = accessFile;
    }

    @XmlAttribute(name = "authenticate")
    public Boolean getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(Boolean authenticate) {
        this.authenticate = authenticate;
    }

    @XmlAttribute(name = "cipher-suites")
    public String getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(String cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    @XmlAttribute(name = "client-auth")
    public Boolean getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(Boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    @XmlAttribute(name = "keystore-file")
    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    @XmlAttribute(name = "keystore-pass")
    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    @XmlAttribute(name = "password-file")
    public String getPasswordFile() {
        return passwordFile;
    }

    public void setPasswordFile(String passwordFile) {
        this.passwordFile = passwordFile;
    }

    @XmlAttribute(name = "protocols")
    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    @XmlAttribute(name = "truststore-file")
    public String getTruststoreFile() {
        return truststoreFile;
    }

    public void setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    @XmlAttribute(name = "truststore-pass")
    public String getTruststorePass() {
        return truststorePass;
    }

    public void setTruststorePass(String truststorePass) {
        this.truststorePass = truststorePass;
    }

    @XmlAttribute(name = "use-jdk-client-factory")
    public Boolean getUseJdkClientFactory() {
        return useJdkClientFactory;
    }

    public void setUseJdkClientFactory(Boolean useJdkClientFactory) {
        this.useJdkClientFactory = useJdkClientFactory;
    }

    @XmlAttribute(name = "use-ssl")
    public Boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        JmxListener jmxListener = (JmxListener) target;

        if (jmxListener.getEnabled() != null && jmxListener.getEnabled()) {
            if (jmxListener.getPort() < 0 || jmxListener.getPort() > 65535) {
                errors.rejectValue("port", "invalid.port");
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
        if (!(obj instanceof JmxListener)) {
            return false;
        }
        JmxListener serverProperties = (JmxListener) obj;
        return ObjectUtils.nullSafeEquals(this.getAccessFile(), serverProperties.getAccessFile())
            && ObjectUtils.nullSafeEquals(this.getAuthenticate(), serverProperties.getAuthenticate())
            && ObjectUtils.nullSafeEquals(this.getBind(), serverProperties.getBind())
            && ObjectUtils.nullSafeEquals(this.getCipherSuites(), serverProperties.getCipherSuites())
            && ObjectUtils.nullSafeEquals(this.getClientAuth(), serverProperties.getClientAuth())
            && ObjectUtils.nullSafeEquals(this.getKeystoreFile(), serverProperties.getKeystoreFile())
            && ObjectUtils.nullSafeEquals(this.getKeystorePass(), serverProperties.getKeystorePass())
            && ObjectUtils.nullSafeEquals(this.getPasswordFile(), serverProperties.getPasswordFile())
            && ObjectUtils.nullSafeEquals(this.getPort(), serverProperties.getPort())
            && ObjectUtils.nullSafeEquals(this.getProtocols(), serverProperties.getProtocols())
            && ObjectUtils.nullSafeEquals(this.getTruststoreFile(), serverProperties.getTruststoreFile())
            && ObjectUtils.nullSafeEquals(this.getTruststorePass(), serverProperties.getTruststorePass())
            && ObjectUtils.nullSafeEquals(this.getUseJdkClientFactory(), serverProperties.getUseJdkClientFactory())
            && ObjectUtils.nullSafeEquals(this.getUseSSL(), serverProperties.getUseSSL());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.accessFile) * 29 + ObjectUtils.nullSafeHashCode(this.authenticate) * 29
            + ObjectUtils.nullSafeHashCode(this.bind) * 29 + ObjectUtils.nullSafeHashCode(this.cipherSuites) * 29
            + ObjectUtils.nullSafeHashCode(this.clientAuth) * 29 + ObjectUtils.nullSafeHashCode(this.keystoreFile) * 29
            + ObjectUtils.nullSafeHashCode(this.keystorePass) * 29 + ObjectUtils.nullSafeHashCode(this.passwordFile) * 29
            + ObjectUtils.nullSafeHashCode(this.port) * 29 + ObjectUtils.nullSafeHashCode(this.protocols) * 29
            + ObjectUtils.nullSafeHashCode(this.truststoreFile) * 29 + ObjectUtils.nullSafeHashCode(this.truststorePass) * 29
            + ObjectUtils.nullSafeHashCode(this.useJdkClientFactory) * 29 + ObjectUtils.nullSafeHashCode(this.useSSL) * 29;
    }

}
