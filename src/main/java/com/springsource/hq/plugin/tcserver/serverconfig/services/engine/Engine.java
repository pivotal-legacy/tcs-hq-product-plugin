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

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.ValidationUtils;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Settings for service engine. (http://tomcat.apache.org/tomcat-6.0-doc/config/engine.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "engine", propOrder = { "hosts", "threadDiagnostics", "logging" })
public class Engine implements Validator, Hierarchical<Service> {

    /**
     * Logical name of this Engine, used in log and error messages. When using muliple Service elements in the same
     * Server, each Engine MUST be assigned a unique name.
     */
    private String name;

    /**
     * The default host name, which identifies the Host that will process requests directed to host names on this
     * server, but which are not configured in this configuration file. This name MUST match the name attributes of one
     * of the Host elements nested immediately inside.
     */
    private String defaultHost;

    /**
     * Identifier which must be used in load balancing scenarios to enable session affinity. The identifier, which must
     * be unique across all tc Runtime instances which participate in the cluster, will be appended to the generated session
     * identifier, therefore allowing the front end proxy to always forward a particular session to the same tc Runtime
     * instance.
     */
    private String jvmRoute;

    private ThreadDiagnostics threadDiagnostics;

    private Logging<Engine> logging;

    private Set<Host> hosts;

    private Service parent;

    /**
     * Used to create a new engine from form binding
     */

    public Engine() {
        threadDiagnostics = new ThreadDiagnostics();
        logging = new Logging<Engine>();
        hosts = new HashSet<Host>();
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "default-host", required = true)
    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    @XmlAttribute(name = "jvm-route")
    public String getJvmRoute() {
        return jvmRoute;
    }

    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }

    @XmlElement(name = "thread-diagnostics", required = true)
    public ThreadDiagnostics getThreadDiagnostics() {
        return threadDiagnostics;
    }

    public void setThreadDiagnostics(ThreadDiagnostics threadDiagnostics) {
        this.threadDiagnostics = threadDiagnostics;
    }

    @XmlElement(name = "logging", required = true)
    public Logging<Engine> getLogging() {
        return logging;
    }

    public void setLogging(Logging<Engine> logging) {
        this.logging = logging;
    }

    @XmlElementWrapper(name = "hosts", required = true)
    @XmlElement(name = "host")
    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Engine engine = (Engine) target;
        if (!errors.hasFieldErrors("name")) {
            if (!StringUtils.hasText(engine.getName())) {
                errors.rejectValue("name", "service.engine.name.required");
            }
        }
        if (!errors.hasFieldErrors("defaultHost")) {
            if (!StringUtils.hasText(engine.getDefaultHost())) {
                errors.rejectValue("defaultHost", "service.engine.defaultHost.required");
            }
        }

        ValidationUtils.validateCollection(engine.getHosts(), "hosts", errors);

        errors.pushNestedPath("logging");
        engine.getLogging().validate(engine.getLogging(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("threadDiagnostics");
        engine.getThreadDiagnostics().validate(engine.getThreadDiagnostics(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        logging.setParent(this);
        logging.applyParentToChildren();
        threadDiagnostics.setParent(this);
        threadDiagnostics.applyParentToChildren();
        for (Host host : hosts) {
            host.setParent(this);
            host.applyParentToChildren();
        }
    }

    public Service parent() {
        return parent;
    }

    public void setParent(Service parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Engine)) {
            return false;
        }
        Engine host = (Engine) obj;
        return ObjectUtils.nullSafeEquals(this.getDefaultHost(), host.getDefaultHost())
            && ObjectUtils.nullSafeEquals(this.getHosts(), host.getHosts()) && ObjectUtils.nullSafeEquals(this.getJvmRoute(), host.getJvmRoute())
            && ObjectUtils.nullSafeEquals(this.getLogging(), host.getLogging()) && ObjectUtils.nullSafeEquals(this.getName(), host.getName())
            && ObjectUtils.nullSafeEquals(this.getThreadDiagnostics(), host.getThreadDiagnostics());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.defaultHost) * 29 + ObjectUtils.nullSafeHashCode(this.hosts) * 29
            + ObjectUtils.nullSafeHashCode(this.jvmRoute) * 29 + ObjectUtils.nullSafeHashCode(this.logging) * 29
            + ObjectUtils.nullSafeHashCode(this.name) * 29 + ObjectUtils.nullSafeHashCode(this.name) * 29
            + ObjectUtils.nullSafeHashCode(this.threadDiagnostics) * 29;
    }

}
