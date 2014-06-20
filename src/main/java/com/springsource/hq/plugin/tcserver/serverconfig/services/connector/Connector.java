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

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Base class for connector implementations.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
public abstract class Connector implements Validator, Hierarchical<Service>, Identity {

    private static final long DEFAULT_CONNECTION_TIMEOUT = 20000L;

    private static final long DEFAULT_MAX_THREADS = 200L;

    private static final long DEFAULT_REDIRECT_PORT = 8443l;

    private static final String DEFAULT_SCHEME = "http";

    /**
     * For servers with more than one IP address, this attribute specifies which address will be used for listening on
     * the specified port. By default, this port will be used on all IP addresses associated with the server. A value of
     * 127.0.0.1 indicates that the Connector will only listen on the loopback interface.
     */
    private String address;

    /**
     * The number of milliseconds this Connector will wait, after accepting a connection, for the request URI line to be
     * presented. The default value is 20000 (per Filip)
     */
    private Long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    /**
     * The maximum number of request processing threads to be created by this Connector, which therefore determines the
     * maximum number of simultaneous requests that can be handled. If not specified, this attribute is set to 200. If
     * an executor is associated with this connector, this attribute is ignored as the connector will execute tasks
     * using the executor rather than an internal thread pool.
     */
    private Long maxThreads = DEFAULT_MAX_THREADS;

    private Service parent;

    /**
     * The TCP port number on which this Connector will create a server socket and await incoming connections. Your
     * operating system will allow only one server application to listen to a particular port number on a particular IP
     * address.
     */
    private Long port;

    /**
     * If this Connector is being used in a proxy configuration, configure this attribute to specify the server name to
     * be returned for calls to request.getServerName(). See Proxy Support for more information.
     */
    private String proxyName;

    /**
     * If this Connector is being used in a proxy configuration, configure this attribute to specify the server port to
     * be returned for calls to request.getServerPort(). See Proxy Support for more information.
     */
    private Long proxyPort;

    /**
     * If this Connector is supporting non-SSL requests, and a request is received for which a matching
     * <security-constraint> requires SSL transport, Catalina will automatically redirect the request to the port number
     * specified here.
     */
    private Long redirectPort = DEFAULT_REDIRECT_PORT;

    /**
     * Set this attribute to the name of the protocol you wish to have returned by calls to request.getScheme(). For
     * example, you would set this attribute to "https" for an SSL Connector. The default value is "http". See SSL
     * Support for more information.
     */
    private String scheme = DEFAULT_SCHEME;

    private String id;

    public void applyParentToChildren() {
        // no-op, no children
    }

    @XmlAttribute(name = "address")
    public String getAddress() {
        return address;
    }

    @XmlAttribute(name = "connection-timeout")
    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    @XmlTransient
    public String getConnectorName() {
        return ((address != null) ? address : "") + ":" + port;
    }

    @XmlTransient
    public String getHumanId() {
        return (((address != null) ? address : "") + ":" + port).replace("/", "");
    }

    @XmlAttribute(name = "max-threads")
    public Long getMaxThreads() {
        return maxThreads;
    }

    @XmlAttribute(name = "port", required = true)
    public Long getPort() {
        return port;
    }

    @XmlAttribute(name = "proxy-name")
    public String getProxyName() {
        return proxyName;
    }

    @XmlAttribute(name = "proxy-port")
    public Long getProxyPort() {
        return proxyPort;
    }

    @XmlAttribute(name = "redirect-port")
    public Long getRedirectPort() {
        return redirectPort;
    }

    @XmlAttribute(name = "scheme")
    public String getScheme() {
        return scheme;
    }

    public Service parent() {
        return parent;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setMaxThreads(Long maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setParent(Service parent) {
        this.parent = parent;
    }

    // helper methods

    public void setPort(Long port) {
        this.port = port;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public void setProxyPort(Long proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setRedirectPort(Long redirectPort) {
        this.redirectPort = redirectPort;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Connector connector = (Connector) target;
        if (!errors.hasFieldErrors("port")) {
            if (connector.getPort() == null) {
                errors.rejectValue("port", "service.connector.port.required");
            } else if (connector.getPort() < 0 || connector.getPort() > 65535) {
                errors.rejectValue("port", "invalid.port");
            }
        }

        if (!errors.hasFieldErrors("proxyPort")) {
            if (connector.getProxyPort() != null) {
                if (connector.getProxyPort() < 0 || connector.getProxyPort() > 65535) {
                    errors.rejectValue("proxyPort", "invalid.port");
                }
            }
        }
        if (!errors.hasFieldErrors("redirectPort")) {
            if (connector.getRedirectPort() != null) {
                if (connector.getRedirectPort() < 0 || connector.getRedirectPort() > 65535) {
                    errors.rejectValue("redirectPort", "invalid.port");
                }
            }
        }
        if (!errors.hasFieldErrors("connectionTimeout")) {
            if (connector.getConnectionTimeout() != null) {
                if (connector.getConnectionTimeout() < 0) {
                    errors.rejectValue("connectionTimeout", "service.connector.connectionTimeout.tooLow");
                }
            }
        }
        if (!errors.hasFieldErrors("maxThreads")) {
            if (connector.getMaxThreads() != null) {
                if (connector.getMaxThreads() < 1) {
                    errors.rejectValue("maxThreads", "service.connector.maxThreads.tooLow");
                }
            }
        }
        if (connector.parent() != null) {
            for (Service service : connector.parent().parent().getServices()) {
                for (Connector c : service.getConnectors()) {
                    if (c != connector && ObjectUtils.nullSafeEquals(c.getAddress(), connector.getAddress()) && (connector.getPort() != null)
                        && ObjectUtils.nullSafeEquals(c.getPort(), connector.getPort())) {
                        errors.reject("service.connector.unique", new Object[] { connector.getAddress(), connector.getPort().toString() }, null);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Connector)) {
            return false;
        }
        Connector connector = (Connector) obj;
        return ObjectUtils.nullSafeEquals(this.getAddress(), connector.getAddress())
            && ObjectUtils.nullSafeEquals(this.getConnectionTimeout(), connector.getConnectionTimeout())
            && ObjectUtils.nullSafeEquals(this.getMaxThreads(), connector.getMaxThreads())
            && ObjectUtils.nullSafeEquals(this.getPort(), connector.getPort())
            && ObjectUtils.nullSafeEquals(this.getProxyName(), connector.getProxyName())
            && ObjectUtils.nullSafeEquals(this.getProxyPort(), connector.getProxyPort())
            && ObjectUtils.nullSafeEquals(this.getRedirectPort(), connector.getRedirectPort())
            && ObjectUtils.nullSafeEquals(this.getScheme(), connector.getScheme());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.address) * 29 + ObjectUtils.nullSafeHashCode(this.connectionTimeout) * 29
            + ObjectUtils.nullSafeHashCode(this.maxThreads) * 29 + ObjectUtils.nullSafeHashCode(this.port) * 29
            + ObjectUtils.nullSafeHashCode(this.proxyName) * 29 + ObjectUtils.nullSafeHashCode(this.proxyPort) * 29
            + ObjectUtils.nullSafeHashCode(this.redirectPort) * 29 + ObjectUtils.nullSafeHashCode(this.scheme) * 29;
    }

}
