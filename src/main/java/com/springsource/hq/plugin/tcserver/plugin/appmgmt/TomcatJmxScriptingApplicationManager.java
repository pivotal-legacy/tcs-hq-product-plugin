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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.JMRuntimeException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.common.ApplicationException;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.Utils;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.ApplicationStatus;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Host;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Service;
import com.springsource.hq.plugin.tcserver.plugin.wrapper.JmxUtils;
import com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

public final class TomcatJmxScriptingApplicationManager implements ScriptingApplicationManager {

    private static final String APPLICATION = "application";

    private static final String VERSION = "version";

    private static final String CONTEXT_PATH = "contextpath";

    private static final Set<String> DEFAULT_REQUIRED_VALUES = new HashSet<String>();

    private static final String HOST_NAME = "host";

    private static final String REMOTE_PATH = "remotepath";

    private static final String SERVICE_NAME = "service";

    private static final String APPLICATION_NAME = "name";

    private static final String APPLICATION_PATH = "path";

    private static final String APPLICATION_VERSION = "version";

    private final FilePermissionsChanger filePermissionsChanger;

    private final FileOwnershipChanger fileOwnershipChanger;

    private static final List<String> ROOT_APP_NAMES = new ArrayList<String>();

    static {
        DEFAULT_REQUIRED_VALUES.add(SERVICE_NAME);
        DEFAULT_REQUIRED_VALUES.add(HOST_NAME);
        ROOT_APP_NAMES.add("/");
        ROOT_APP_NAMES.add("/ROOT");
        ROOT_APP_NAMES.add("ROOT");
    }

    private final Log LOGGER = LogFactory.getLog(TomcatJmxScriptingApplicationManager.class);

    private final JmxUtils mxUtil;

    public TomcatJmxScriptingApplicationManager(JmxUtils jmxUtils, FilePermissionsChanger filePermissionsChanger,
        FileOwnershipChanger fileOwnershipChanger) {
        this.mxUtil = jmxUtils;
        this.filePermissionsChanger = filePermissionsChanger;
        this.fileOwnershipChanger = fileOwnershipChanger;
    }

    private boolean checkFileExists(String fileLocation) {
        boolean exists = false;
        try {
            exists = new File(fileLocation).isFile();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return exists;
    }

    private void checkRequiredValues(ConfigResponse config, Set<String> requiredValues) throws PluginException {
        requiredValues.addAll(DEFAULT_REQUIRED_VALUES);
        for (String requiredValue : requiredValues) {
            if (!config.getKeys().contains(requiredValue)) {
                throw new PluginException("Required parameter '" + requiredValue + "' was not specified.");
            }
        }
    }

    private PluginException createPluginException(final Throwable throwable) throws PluginException {
        PluginException pluginException;
        if (throwable instanceof JMRuntimeException) {
            final Throwable cause = throwable.getCause();
            pluginException = new PluginException(cause.getMessage(), cause);
        } else {
            pluginException = new PluginException(throwable.getMessage(), throwable);
        }
        return pluginException;
    }

    private Service createService(String serviceName, List<String> hostNames, ConfigResponse config) throws PluginException {
        Service service = new Service();
        service.setName(serviceName);
        boolean hasApplications = false;
        for (String hostName : hostNames) {
            Host host = new Host();
            host.setName(hostName);
            List<Application> applications = getApplications(serviceName, hostName, config);
            if (!applications.isEmpty()) {
                host.getApplications().addAll(applications);
                service.getHosts().add(host);
                hasApplications = true;
            }
        }
        if (!hasApplications) {
            if (config.getValue(APPLICATION) != null) {
                throw new PluginException("Application name specified was not found: " + config.getValue(APPLICATION));
            }
            service = null;
        }
        return service;
    }

    private String resolveContextPath(String originalContextPath) {
        for (String rootAppString : ROOT_APP_NAMES) {
            if (rootAppString.equalsIgnoreCase(originalContextPath)) {
                // the name for root in Apache Tomcat.
                return "";
            }
        }
        final String contextPath = originalContextPath.startsWith("/") ? originalContextPath : "/" + originalContextPath;
        String tempDeployPath = contextPath;
        if (contextPath.indexOf("##") > 0) {
            tempDeployPath = contextPath.substring(0, contextPath.indexOf("##"));
        }
        return tempDeployPath;
    }

    public List<ApplicationStatus> deploy(ConfigResponse config) throws PluginException {
        Set<String> requiredValues = new HashSet<String>();
        requiredValues.add(CONTEXT_PATH);
        requiredValues.add(REMOTE_PATH);
        checkRequiredValues(config, requiredValues);
        final Map<String, String> connectionInformation = getConnectionInformation(config.getValue(SERVICE_NAME), config.getValue(HOST_NAME), config);
        final List<ApplicationStatus> statusListing = new ArrayList<ApplicationStatus>();
        final String contextPath = resolveContextPath(config.getValue(CONTEXT_PATH));
        final String applicationLocation = config.getValue(REMOTE_PATH);
        LOGGER.debug("contextpath - " + contextPath + "  application -  " + applicationLocation);
        String resultMessage;
        boolean hasError = false;
        if (checkFileExists(applicationLocation)) {
            File warFile = new File(applicationLocation);
            this.filePermissionsChanger.changeFilePermissions(warFile);
            this.fileOwnershipChanger.changeFileOwnership(warFile, config.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_USERNAME),
                config.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_GROUP));
            try {
                String objectName = getObjectName();
                if (isTcRuntime250OrLater(objectName, "deployApplication", 5, config) && (Boolean.valueOf(config.getValue("MULTI_REVISION_CAPABLE")))) {
                    String version = getNextApplicationVersion(contextPath, connectionInformation, config);
                    mxUtil.invoke(config.toProperties(), objectName, "deployApplication", new Object[] { connectionInformation.get(SERVICE_NAME),
                        connectionInformation.get(HOST_NAME), contextPath, version, applicationLocation }, new String[] { String.class.getName(),
                        String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() });
                    resultMessage = String.format("Ok - Application '%s', revision '%s has deployed", contextPath, version);
                } else {
                    mxUtil.invoke(config.toProperties(), objectName, "deployApplication", new Object[] { connectionInformation.get(SERVICE_NAME),
                        connectionInformation.get(HOST_NAME), contextPath, applicationLocation },
                        new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() });
                    resultMessage = String.format("Ok - Application %s has deployed.", contextPath);
                }
            } catch (final ApplicationException e) {
                throw createPluginException(e);
            } catch (JMException e) {
                throw createPluginException(e);
            } catch (IOException e) {
                throw createPluginException(e);
            } catch (final RuntimeException e) {
                resultMessage = String.format("Failure - %s", createPluginException(e).getMessage());
                hasError = true;
            }
        } else {
            resultMessage = String.format("Failure - Application %s failed to deploy - No file exists at location: %s", contextPath,
                applicationLocation);
            hasError = true;
        }
        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setResultMessage(resultMessage);
        applicationStatus.setApplicationName(contextPath);
        applicationStatus.setHasError(hasError);
        statusListing.add(applicationStatus);
        return statusListing;
    }

    @SuppressWarnings("unchecked")
    private String getNextApplicationVersion(String path, Map<String, String> connectionInformation, ConfigResponse config) throws JMException,
        IOException, PluginException, ApplicationException {
        int latestVersion = -1;

        Set<Map<String, String>> applicationMetadatas = (Set<Map<String, String>>) mxUtil.invoke(config.toProperties(), getObjectName(),
            "listApplications", new Object[] { connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME) }, new String[] {
                String.class.getName(), String.class.getName() });
        for (Map<String, String> applicationMetadata : applicationMetadatas) {
            String applicationPath = applicationMetadata.get(APPLICATION_PATH);
            if (path.equals(applicationPath)) {
                String applicationVersion = applicationMetadata.get(APPLICATION_VERSION);
                int version;
                if ("".equals(applicationVersion)) {
                    version = 0;
                } else {
                    version = Integer.parseInt(applicationVersion);
                }

                if (version > latestVersion) {
                    latestVersion = version;
                }
            }
        }

        if (latestVersion == -1) {
            return "";
        }
        return String.format("%06d", latestVersion + 1);
    }

    private List<ApplicationStatus> executeCommand(ConfigResponse config, String command, String messageSuffix, Set<String> requiredValues)
        throws PluginException {
        checkRequiredValues(config, requiredValues);
        final Map<String, String> connectionInformation = getConnectionInformation(config.getValue(SERVICE_NAME), config.getValue(HOST_NAME), config);
        final List<ApplicationStatus> statusListing = new ArrayList<ApplicationStatus>();
        String resultMessage;
        boolean hasError = false;
        for (final String appName : getApplicationNames(config)) {
            String applicationName = appName;
            if (appName.equals("/ROOT") || appName.equals("/")) {
                applicationName = "";
            }

            try {
                String service = connectionInformation.get(SERVICE_NAME);
                String host = connectionInformation.get(HOST_NAME);

                LOGGER.debug(command + " = " + service + " " + host + " " + applicationName);

                String objectName = getObjectName();
                if (isTcRuntime250OrLater(objectName, command, 4, config)) {
                    String version = config.getValue(VERSION);

                    if (isApplicationRevisionAdequatelySpecified(applicationName, version, service, host, config)) {
                        if (version != null && (!"".equals(version))) {
                            version = ApplicationUtils.convertVersionToPaddedString(version);
                        }
                        mxUtil.invoke(config.toProperties(), objectName, command, new Object[] { service, host, applicationName, version },
                            new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() });
                        resultMessage = String.format("Ok - Application '%s', revision '%s has %s", applicationName, version, messageSuffix);
                    } else {
                        resultMessage = "Failure - multiple revisions of '" + appName + "' are present but no revision was specified";
                        hasError = true;
                    }
                } else {
                    mxUtil.invoke(config.toProperties(), objectName, command, new Object[] { service, host, applicationName }, new String[] {
                        String.class.getName(), String.class.getName(), String.class.getName() });
                    resultMessage = String.format("Ok - Application '%s' has %s", applicationName, messageSuffix);
                }

            } catch (ApplicationException e) {
                throw createPluginException(e);
            } catch (IOException e) {
                throw createPluginException(e);
            } catch (JMException e) {
                throw createPluginException(e);
            } catch (RuntimeException e) {
                resultMessage = "Failure - " + createPluginException(e).getMessage();
                hasError = true;
            }

            statusListing.add(createApplicationStatus(applicationName, resultMessage, hasError));
        }
        return statusListing;
    }

    private boolean isApplicationRevisionAdequatelySpecified(String applicationName, String version, String service, String host,
        ConfigResponse config) throws PluginException {
        if (version == null || "".equals(version)) {
            int revisionCount = 0;

            for (Application application : getApplications(service, host, config)) {
                if (applicationName.equals(application.getName())) {
                    revisionCount++;
                }
            }

            return revisionCount <= 1;
        }

        return true;
    }

    private ApplicationStatus createApplicationStatus(String applicationName, String resultMessage, boolean hasError) {
        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setResultMessage(resultMessage);
        applicationStatus.setApplicationName(applicationName);
        applicationStatus.setHasError(hasError);
        return applicationStatus;
    }

    private List<String> getApplicationNames(final ConfigResponse config) {
        final Set<String> keys = config.getKeys();
        LOGGER.debug("KEYS = " + keys);
        final List<String> applicationNames = new ArrayList<String>();
        for (int i = 1; i < keys.size(); i++) {
            if (keys.contains(APPLICATION + i)) {
                LOGGER.debug("FOUND " + i + "." + config.getValue(APPLICATION + i));
                applicationNames.add(validateApplicationName(config.getValue(APPLICATION + i)));
            }
        }
        return applicationNames;
    }

    protected String getApplicationObjectName(final String path, final String host) {
        String applicationPath = path;
        if (path.equals("")) {
            applicationPath = "/";
        }
        return String.format("Catalina:type=Manager,path=%s,host=%s", applicationPath, host);
    }

    @SuppressWarnings("unchecked")
    private List<Application> getApplications(String service, String host, ConfigResponse config) throws PluginException {
        String applicationNameFilter = validateApplicationName(config.getValue(APPLICATION));

        List<Application> applications = new ArrayList<Application>();
        try {
            Map<String, String> connectionInformation = getConnectionInformation(service, host, config);

            Object applicationsObject = mxUtil.invoke(config.toProperties(), getObjectName(), "listApplications", new Object[] {
                connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME) },
                new String[] { String.class.getName(), String.class.getName() });

            if (isTcRuntime25OrLater(applicationsObject)) {
                applications.addAll(getApplications((Set<Map<String, String>>) applicationsObject, applicationNameFilter, connectionInformation,
                    config));
            } else {
                applications.addAll(getApplications((String[]) applicationsObject, applicationNameFilter, connectionInformation, config));
            }
        } catch (final ApplicationException e) {
            createPluginException(e);
        } catch (final PluginException e) {
            throw e;
        } catch (IOException e) {
            createPluginException(e);
        } catch (JMException e) {
            createPluginException(e);
        }
        return applications;
    }

    private List<Application> getApplications(Set<Map<String, String>> applicationMetadatas, String applicationNameFilter,
        Map<String, String> connectionInformation, ConfigResponse config) throws ApplicationException, PluginException, IOException, JMException {
        List<Application> applications = new ArrayList<Application>();

        boolean tomcat7 = Boolean.valueOf(config.getValue("MULTI_REVISION_CAPABLE"));

        for (Map<String, String> applicationMetadata : applicationMetadatas) {
            String applicationName = applicationMetadata.get(APPLICATION_NAME);

            if (applicationNameFilter == null || applicationNameFilter.equals(applicationName)) {
                String path = applicationMetadata.get(APPLICATION_PATH);
                String version = applicationMetadata.get(APPLICATION_VERSION);
                String hostName = connectionInformation.get(HOST_NAME);
                String serviceName = connectionInformation.get(SERVICE_NAME);

                try {
                    Application application = new Application();
                    application.setName(TomcatNameUtils.convertPathToName(path));
                    application.setStatus(getApplicationStatus(connectionInformation, config, path, version));

                    if (version != null && version.length() > 0) {
                        application.setVersion(Integer.parseInt(version));
                    } else {
                        application.setVersion(0);
                    }

                    String objectName = ObjectNameUtils.getManagerMBeanObjectNameForApplication(hostName, application, tomcat7);

                    application.setSessionCount(getSessionCount(objectName, config));

                    applications.add(application);
                } catch (Exception e) {
                    LOGGER.warn(String.format("Failed to collect details for application '%s' deployed on service '%s' and host '%s'",
                        applicationName, serviceName, hostName), e);
                }
            }
        }

        return applications;
    }

    private List<Application> getApplications(String[] applicationNames, String applicationNameFilter, Map<String, String> connectionInformation,
        ConfigResponse config) throws ApplicationException, PluginException, JMException, IOException {
        List<Application> applications = new ArrayList<Application>();
        for (final String applicationName : applicationNames) {
            if (applicationNameFilter == null || applicationNameFilter.equals(applicationName)) {
                Application application = new Application();

                application.setName(applicationName.equals("") ? "ROOT" : applicationName.substring(1));
                application.setStatus(getApplicationStatus(connectionInformation, config, applicationName));

                String objectName = ObjectNameUtils.getManagerMBeanObjectNameForApplication(connectionInformation.get(HOST_NAME), application, false);
                application.setSessionCount(getSessionCount(objectName, config));
                applications.add(application);
            }
        }
        return applications;
    }

    private int getSessionCount(String objectName, ConfigResponse config) throws JMException, PluginException, IOException {
        if (isMBeanRegistered(objectName, config)) {
            return (Integer) mxUtil.getValue(config.toProperties(), objectName, "activeSessions");
        }
        return 0;
    }

    private boolean isMBeanRegistered(String objectName, ConfigResponse config) throws IOException, MalformedObjectNameException {
        return mxUtil.getMBeanServer(config.toProperties()).isRegistered(new ObjectName(objectName));
    }

    private String getApplicationStatus(Map<String, String> connectionInformation, ConfigResponse config, final String applicationName)
        throws ApplicationException, PluginException {
        return getOverallStatus((String) mxUtil.invoke(config.toProperties(), getObjectName(), "getApplicationState", new Object[] {
            connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), applicationName }, new String[] { String.class.getName(),
            String.class.getName(), String.class.getName() }));
    }

    private String getApplicationStatus(Map<String, String> connectionInformation, ConfigResponse config, String path, String version)
        throws ApplicationException, PluginException {
        return getOverallStatus((String) mxUtil.invoke(config.toProperties(), getObjectName(), "getApplicationState", new Object[] {
            connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), path, version }, new String[] { String.class.getName(),
            String.class.getName(), String.class.getName(), String.class.getName() }));
    }

    private Map<String, String> getConnectionInformation(String serviceName, String hostName, final ConfigResponse config) throws PluginException {
        final Map<String, String> connectionInformation = new LinkedHashMap<String, String>();
        if (this.mxUtil.checkConnection(config)) {
            connectionInformation.put(SERVICE_NAME, serviceName);
            connectionInformation.put(HOST_NAME, hostName);
            LOGGER.debug("ConnectionInfo: SERVICE_NAME = " + serviceName + ", HOST_NAME = " + hostName);
            boolean canExecute = false;
            String[] services;
            try {
                services = (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getServices", new Object[0], new String[0]);
                if (Arrays.asList(services).contains(serviceName)) {
                    if (Arrays.asList(
                        (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getHosts", new Object[] { serviceName },
                            new String[] { String.class.getName() })).contains(hostName)) {
                        canExecute = true;
                    }
                }
            } catch (final ApplicationException e) {
                throw createPluginException(e);
            }
            if (!canExecute) {
                throw new PluginException("The service name (" + serviceName + ") and host name (" + hostName
                    + ") does not match any services on this resource. "
                    + "The resource server configuration may be out of sync with the other group members.");
            }
        } else {
            throw new PluginException(
                "Unable to connect to the instance. Please verify the instance is running and whether the JMX configuration is correct.");
        }
        return connectionInformation;
    }

    private List<String> getHosts(String service, ConfigResponse config) throws ApplicationException, PluginException {
        String[] hosts = (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getHosts", new Object[] { service },
            new String[] { String.class.getName() });
        return Arrays.asList(hosts);
    }

    protected String getObjectName() {
        return "tcServer:type=Serviceability,name=Deployer";
    }

    private String getOverallStatus(final String detailedStatus) {
        String overallStatus = "Stopped";
        LOGGER.debug("DETAILED STATUS = " + detailedStatus);
        if (detailedStatus.equals("AVAILABLE") || detailedStatus.equals("STARTED")) {
            overallStatus = "Running";
        }
        return overallStatus;
    }

    public Map<String, List<String>> getServiceHostMappings(ConfigResponse config) throws PluginException {
        if (this.mxUtil.checkConnection(config)) {
            try {
                List<String> allServices = getServices(config);
                List<String> queryServices = new ArrayList<String>();

                if (config.getValue(SERVICE_NAME) == null) {
                    queryServices.addAll(allServices);
                } else if (allServices.contains(config.getValue(SERVICE_NAME))) {
                    queryServices.add(config.getValue(SERVICE_NAME));
                } else {
                    throw new PluginException("Service name specified was not found: " + config.getValue(SERVICE_NAME));
                }

                return getServiceHostMappings(queryServices, config);
            } catch (final ApplicationException e) {
                throw createPluginException(e);
            }
        }

        throw new PluginException(
            "Unable to connect to the instance. Please verify the instance is running and whether the JMX configuration is correct.");
    }

    private Map<String, List<String>> getServiceHostMappings(List<String> services, ConfigResponse config) throws PluginException,
        ApplicationException {
        Map<String, List<String>> serviceHostMapping = new LinkedHashMap<String, List<String>>();
        boolean hostFound = false;
        for (String service : services) {
            List<String> allHosts = getHosts(service, config);
            List<String> hosts = new ArrayList<String>();
            if (config.getValue(HOST_NAME) == null) {
                hosts = allHosts;
            } else {
                if (allHosts.contains(config.getValue(HOST_NAME))) {
                    hosts = new ArrayList<String>(1);
                    hosts.add(config.getValue(HOST_NAME));
                }
            }
            if (!hosts.isEmpty()) {
                serviceHostMapping.put(service, hosts);
                hostFound = true;
            }
        }
        if (!hostFound) {
            throw new PluginException("Host name specified was not found: " + config.getValue(HOST_NAME));
        }
        return serviceHostMapping;
    }

    private List<String> getServices(ConfigResponse config) throws ApplicationException, PluginException {
        String[] services = (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getServices", new Object[0], new String[0]);
        return Arrays.asList(services);
    }

    public List<Service> list(ConfigResponse config) throws PluginException {
        List<Service> serviceListing = new ArrayList<Service>();
        for (Map.Entry<String, List<String>> serviceHostEntry : getServiceHostMappings(config).entrySet()) {
            Service service = createService(serviceHostEntry.getKey(), serviceHostEntry.getValue(), config);
            if (service != null) {
                serviceListing.add(service);
            }
        }
        return serviceListing;
    }

    public List<ApplicationStatus> reload(ConfigResponse config) throws PluginException {
        Set<String> requiredValues = new HashSet<String>();
        requiredValues.add(APPLICATION + 1);
        return executeCommand(config, "reloadApplication", "reloaded", requiredValues);
    }

    public List<ApplicationStatus> start(ConfigResponse config) throws PluginException {
        Set<String> requiredValues = new HashSet<String>();
        requiredValues.add(APPLICATION + 1);
        return executeCommand(config, "startApplication", "started", requiredValues);
    }

    public List<ApplicationStatus> stop(ConfigResponse config) throws PluginException {
        Set<String> requiredValues = new HashSet<String>();
        requiredValues.add(APPLICATION + 1);
        return executeCommand(config, "stopApplication", "stopped", requiredValues);
    }

    public List<ApplicationStatus> undeploy(ConfigResponse config) throws PluginException {
        Set<String> requiredValues = new HashSet<String>();
        requiredValues.add(APPLICATION + 1);
        return executeCommand(config, "undeployApplication", "undeployed", requiredValues);
    }

    private String validateApplicationName(String applicationName) {
        String validatedName = applicationName;
        if (applicationName != null) {
            if (applicationName.equals("/") || applicationName.equals("/ROOT") || applicationName.equals("ROOT")) {
                validatedName = "";
            } else if (!applicationName.startsWith("/")) {
                validatedName = "/" + applicationName;
            }
        }
        return validatedName;
    }

    private boolean isTcRuntime25OrLater(Object applicationsObj) {
        return applicationsObj instanceof Set<?>;
    }

    private boolean isTcRuntime250OrLater(String objectName, String operationName, int expected25OrLaterArgumentCount, ConfigResponse config)
        throws JMException, IOException {
        MBeanServerConnection mBeanServer = mxUtil.getMBeanServer(config.toProperties());
        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(new ObjectName(objectName));
        for (MBeanOperationInfo operationInfo : mBeanInfo.getOperations()) {
            if (operationInfo.getName().equals(operationName) && (expected25OrLaterArgumentCount == operationInfo.getSignature().length)) {
                return true;
            }
        }
        return false;
    }
}
