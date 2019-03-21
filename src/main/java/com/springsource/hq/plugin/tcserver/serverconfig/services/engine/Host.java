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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Settings for hosts. (http://tomcat.apache.org/tomcat-6.0-doc/config/host.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
public class Host implements Validator, Hierarchical<Engine>, Identity {

    private static final boolean DEFAULT_AUTO_DEPLOY = true;

    private static final boolean DEFAULT_DEPLOY_ON_STARTUP = true;

    private static final boolean DEFAULT_DEPLOY_XML = true;

    private static final boolean DEFAULT_UNPACK_WARS = true;

    /**
     * The Application Base directory for this virtual host. This is the pathname of a directory that may contain web
     * applications to be deployed on this virtual host. You may specify an absolute pathname for this directory, or a
     * pathname that is relative to the $CATALINA_BASE directory. See Automatic Application Deployment for more
     * information on automatic recognition and deployment of web applications to be deployed automatically.
     */
    private String appBase;

    /**
     * This flag value indicates if new web applications, dropped in to the appBase directory while tc Runtime is running,
     * should be automatically deployed. The flag's value defaults to true. See Automatic Application Deployment for
     * more information.
     */
    private Boolean autoDeploy = DEFAULT_AUTO_DEPLOY;

    /**
     * This flag value indicates if web applications from this host should be automatically deployed by the host
     * configurator. The flag's value defaults to true. See Automatic Application Deployment for more information.
     */
    private Boolean deployOnStartup = DEFAULT_DEPLOY_ON_STARTUP;

    /**
     * Set to false if you want to disable parsing the context.xml file embedded inside the application (located at
     * /META-INF/context.xml). Security consious environments should set this to false to prevent applications from
     * interacting with the container's configuration. The administrator will then be responsible for providing an
     * external context configuration file, and put it in $CATALINA_BASE/conf/[enginename]/[hostname]/. The flag's value
     * defaults to true.
     */
    private Boolean deployXML = DEFAULT_DEPLOY_XML;

    private Logging<Host> logging;

    /**
     * Network name of this virtual host, as registered in your Domain Name Service server. One of the Hosts nested
     * within an Engine MUST have a name that matches the defaultHost setting for that Engine. See Host Name Aliases for
     * information on how to assign more than one network name to the same virtual host.
     */
    private String name;

    private Engine parent;

    /**
     * Set to true if you want web applications that are placed in the appBase directory as web application archive
     * (WAR) files to be unpacked into a corresponding disk directory structure, false to run such web applications
     * directly from a WAR file. See Automatic Application Deployment for more information.
     */
    private Boolean unpackWARs = DEFAULT_UNPACK_WARS;

    /**
     * Pathname to a scratch directory to be used by applications for this Host. Each application will have its own sub
     * directory with temporary read-write use. Configuring a Context workDir will override use of the Host workDir
     * configuration. This directory will be made visible to servlets in the web application by a servlet context
     * attribute (of type java.io.File) named javax.servlet.context.tempdir as described in the Servlet Specification.
     * If not specified, a suitable directory underneath $CATALINA_BASE/work will be provided.
     */
    private String workDir;

    private String id;

    public Host() {
        logging = new Logging<Host>();
    }

    public void applyParentToChildren() {
        logging.setParent(this);
        logging.applyParentToChildren();
    }

    @XmlAttribute(name = "app-base", required = true)
    public String getAppBase() {
        return appBase;
    }

    @XmlAttribute(name = "auto-deploy")
    public Boolean getAutoDeploy() {
        return autoDeploy;
    }

    @XmlAttribute(name = "deploy-on-startup")
    public Boolean getDeployOnStartup() {
        return deployOnStartup;
    }

    @XmlAttribute(name = "deploy-xml")
    public Boolean getDeployXML() {
        return deployXML;
    }

    @XmlTransient
    public String getHumanId() {
        return name.replace("/", "");
    }

    public Logging<Host> getLogging() {
        return logging;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    @XmlAttribute(name = "unpack-wars")
    public Boolean getUnpackWARs() {
        return unpackWARs;
    }

    @XmlAttribute(name = "work-dir")
    public String getWorkDir() {
        return workDir;
    }

    public Engine parent() {
        return parent;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public void setAutoDeploy(Boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public void setDeployOnStartup(Boolean deployOnStartup) {
        this.deployOnStartup = deployOnStartup;
    }

    public void setDeployXML(Boolean deployXML) {
        this.deployXML = deployXML;
    }

    public void setLogging(Logging<Host> logging) {
        this.logging = logging;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Engine parent) {
        this.parent = parent;
    }

    public void setUnpackWARs(Boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
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

    @Override
    public String toString() {
        return this.getName();
    }

    public void validate(Object target, Errors errors) {
        Host host = (Host) target;
        if (!errors.hasFieldErrors("name")) {
            if (!StringUtils.hasText(host.getName())) {
                errors.rejectValue("name", "service.engine.host.name.required");
            } else {
                if (host.parent() != null) {
                    // detect duplicate host names
                    Service service = host.parent().parent();
                    for (Host h : service.getEngine().getHosts()) {
                        if (h != host && ObjectUtils.nullSafeEquals(host.getName(), h.getName())) {
                            errors.reject("service.engine.host.name.unique", new Object[] { host.getName(), service.getName() }, null);
                        }
                    }
                }
            }
        }
        if (!errors.hasFieldErrors("appBase")) {
            if (!StringUtils.hasText(host.getAppBase())) {
                errors.rejectValue("appBase", "service.engine.host.appBase.required");
            }
        }
        errors.pushNestedPath("logging");
        host.getLogging().validate(host.getLogging(), errors);
        errors.popNestedPath();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Host)) {
            return false;
        }
        Host host = (Host) obj;
        return ObjectUtils.nullSafeEquals(this.getAppBase(), host.getAppBase())
            && ObjectUtils.nullSafeEquals(this.getAutoDeploy(), host.getAutoDeploy())
            && ObjectUtils.nullSafeEquals(this.getDeployOnStartup(), host.getDeployOnStartup())
            && ObjectUtils.nullSafeEquals(this.getDeployXML(), host.getDeployXML())
            && ObjectUtils.nullSafeEquals(this.getLogging(), host.getLogging()) && ObjectUtils.nullSafeEquals(this.getName(), host.getName())
            && ObjectUtils.nullSafeEquals(this.getUnpackWARs(), host.getUnpackWARs())
            && ObjectUtils.nullSafeEquals(this.getWorkDir(), host.getWorkDir());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.appBase) * 29 + ObjectUtils.nullSafeHashCode(this.autoDeploy) * 29
            + ObjectUtils.nullSafeHashCode(this.deployOnStartup) * 29 + ObjectUtils.nullSafeHashCode(this.deployXML) * 29
            + ObjectUtils.nullSafeHashCode(this.logging) * 29 + ObjectUtils.nullSafeHashCode(this.name) * 29
            + ObjectUtils.nullSafeHashCode(this.unpackWARs) * 29 + ObjectUtils.nullSafeHashCode(this.workDir) * 29;
    }

}
