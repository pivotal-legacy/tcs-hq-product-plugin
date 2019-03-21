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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import static com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils.convertVersionToPaddedString;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMRuntimeException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.common.ApplicationException;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.plugin.wrapper.JmxUtils;
import com.springsource.hq.plugin.tcserver.util.application.ApplicationIdentifier;
import com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

/**
 * Interacts with the JMX application management mbean.
 * 
 * @author jkonicki
 * 
 */
public final class TomcatJmxApplicationManager implements ApplicationManager {

    private static final String OVERALL_STATUS_RUNNING = "Running";

    private static final String OVERALL_STATUS_STOPPED = "Stopped";

    private static final String APPLICATION = "APPLICATION";

    private static final String DEPLOY_PATH = "DEPLOY_PATH";

    private static final String HOST_NAME = "HOST_NAME";

    private static final String SERVICE_NAME = "SERVICE_NAME";

    private static final String WAR_FILE_LOCATION = "WAR_FILE_LOCATION";

    private static final String MULTI_REVISION_CAPABLE = "MULTI_REVISION_CAPABLE";

    private static final String INSTANCE_USERNAME = "process.username";

    private static final String INSTANCE_GROUP = "process.group";

    private final Log LOGGER = LogFactory.getLog(TomcatJmxApplicationManager.class);

    private final JmxUtils mxUtil;

    private final FilePermissionsChanger filePermissionsChanger;

    private final FileOwnershipChanger fileOwnershipChanger;

    public TomcatJmxApplicationManager(JmxUtils jmxUtils, FilePermissionsChanger filePermissionsChanger, FileOwnershipChanger fileOwnershipChanger) {
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

    private Set<Application> createApplicationStatusMapping(Properties configProperties, String serviceName, String hostName, boolean tomcat7)
        throws PluginException {

        SortedSet<Application> applications = fetchApplications(configProperties, serviceName, hostName);

        Iterator<Application> applicationsIterator = applications.iterator();

        while (applicationsIterator.hasNext()) {
            Application application = applicationsIterator.next();
            try {
                fetchAndApplyApplicationStatus(configProperties, serviceName, hostName, application);
                fetchAndApplySessionCount(configProperties, hostName, application, tomcat7);
            } catch (PluginException pe) {
                LOGGER.warn(String.format("Failed to collect details for application '%s' deployed on service '%s' and host '%s'",
                    application.getName(), serviceName, hostName), pe);
                applicationsIterator.remove();
            }
        }

        return applications;
    }

    private void fetchAndApplySessionCount(Properties configProperties, String hostName, Application application, boolean tomcat7)
        throws PluginException {
        Integer sessionCount = 0;

        String appObjectName = ObjectNameUtils.getManagerMBeanObjectNameForApplication(hostName, application, tomcat7);

        try {
            MBeanServerConnection mbsc = mxUtil.getMBeanServer(configProperties);
            boolean mbeanIsRegistered = mbsc.isRegistered(new ObjectName(appObjectName));

            if (mbeanIsRegistered) {
                sessionCount = (Integer) mxUtil.getValue(configProperties, appObjectName, "activeSessions");
            }

            application.setSessionCount(sessionCount);
        } catch (PluginException pe) {
            throw pe;
        } catch (Exception e) {
            throw createPluginException(e);
        }
    }

    private void fetchAndApplyApplicationStatus(Properties configProperties, String serviceName, String hostName, Application application)
        throws PluginException {

        String status = fetchApplicationStatus(configProperties, serviceName, hostName, application.getName(), application.getVersion());
        application.setStatus(status);
    }

    private String fetchApplicationStatus(Properties configProperties, String serviceName, String hostName, String applicationName,
        int applicationVersion) throws PluginException {

        String applicationPath = TomcatNameUtils.convertNameToPath(applicationName);

        String objectName = getObjectName();

        String[] types;
        Object[] arguments;

        if (isTcRuntime250OrLater(objectName, "getApplicationState", 4, configProperties)) {
            types = new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() };
            arguments = new Object[] { serviceName, hostName, applicationPath, convertVersionToPaddedString(applicationVersion) };
        } else {
            types = new String[] { String.class.getName(), String.class.getName(), String.class.getName() };
            arguments = new Object[] { serviceName, hostName, applicationPath };
        }

        try {
            String status = (String) mxUtil.invoke(configProperties, objectName, "getApplicationState", arguments, types);
            return convertDetailedStatusToOverallStatus(status);
        } catch (PluginException pe) {
            throw pe;
        } catch (Exception e) {
            throw createPluginException(e);
        }
    }

    private SortedSet<Application> fetchApplications(Properties configProperties, String serviceName, String hostName) throws PluginException {

        try {
            Object applicationsObj = mxUtil.invoke(configProperties, getObjectName(), "listApplications", new Object[] { serviceName, hostName },
                new String[] { String.class.getName(), String.class.getName() });
            return extractApplications(applicationsObj);
        } catch (PluginException pe) {
            throw pe;
        } catch (Exception e) {
            throw createPluginException(e);
        }
    }

    private SortedSet<Application> extractApplications(Object applicationsObject) {

        SortedSet<Application> applications = new TreeSet<Application>();

        if (isTcRuntime25OrLater(applicationsObject)) {
            @SuppressWarnings("unchecked")
            Set<? extends Map<String, String>> applicationsSet = (Set<? extends Map<String, String>>) applicationsObject;

            for (Map<String, String> item : applicationsSet) {
                Application application = new Application();
                application.setName(TomcatNameUtils.convertPathToName(item.get("path")));
                String versionString = item.get("version");
                if (versionString.length() > 0) {
                    application.setVersion(Integer.parseInt(versionString));
                } else {
                    application.setVersion(0);
                }
                applications.add(application);
            }
        } else {
            for (String applicationPath : (String[]) applicationsObject) {
                Application application = new Application();
                application.setName(TomcatNameUtils.convertPathToName(applicationPath));
                application.setVersion(0);
                applications.add(application);
            }
        }

        return applications;
    }

    private boolean isTcRuntime25OrLater(Object applicationsObj) {
        return applicationsObj instanceof Set<?>;
    }

    private PluginException createPluginException(Throwable throwable) {
        PluginException pluginException;
        if (throwable instanceof JMRuntimeException) {
            final Throwable cause = throwable.getCause();
            pluginException = new PluginException(cause.getMessage(), cause);
        } else {
            pluginException = new PluginException(throwable.getMessage(), throwable);
        }
        return pluginException;
    }

    public void removeTemporaryWarFile(final ConfigResponse config) {
        try {
            File file = new File(config.getValue("fileName"));
            file.delete();
        } catch (Exception e) {
            LOGGER.debug("Deleting temporary file failed: " + e.getMessage());
        }
    }

    public Object deploy(ConfigResponse config) throws PluginException {

        final Map<String, String> connectionInformation = getConnectionInformation(config);
        final String deployPathString = config.getValue(DEPLOY_PATH);
        final String warFileLocation = config.getValue(WAR_FILE_LOCATION);

        LOGGER.debug("deploypath - " + deployPathString + "  war -  " + warFileLocation);

        String resultMessage = null;
        String deployPath = TomcatNameUtils.convertNameToPath(deployPathString);

        boolean fileExists = checkFileExists(warFileLocation);

        if (fileExists) {
            File warFile = new File(warFileLocation);
            this.filePermissionsChanger.changeFilePermissions(warFile);
            this.fileOwnershipChanger.changeFileOwnership(warFile, config.getValue(INSTANCE_USERNAME), config.getValue(INSTANCE_GROUP));
            resultMessage = doDeploy(config, connectionInformation, warFileLocation, resultMessage, deployPath);
        } else {
            resultMessage = "Failure - Application " + deployPathString + " failed to deploy - No file exists at location: " + warFileLocation;
        }

        return resultMessage;
    }

    private String doDeploy(ConfigResponse config, Map<String, String> connectionInformation, String warFileLocation, String resultMessage,
        String deployPath) throws PluginException {
        String tempDeployPath = deployPath;
        if (deployPath.indexOf("##") > 0) {
            tempDeployPath = deployPath.substring(0, deployPath.indexOf("##"));
        }

        LOGGER.debug("About to deploy " + config);

        String objectName = getObjectName();

        String serviceName = (String) connectionInformation.get(SERVICE_NAME);
        String hostName = (String) connectionInformation.get(HOST_NAME);

        String newRevision = getNewRevisionForApplication(config, tempDeployPath, serviceName, hostName);

        String[] types;
        Object[] arguments;

        if (newRevision != null) {
            types = new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName(),
                String.class.getName() };
            arguments = new Object[] { serviceName, hostName, tempDeployPath, newRevision, warFileLocation };
        } else {
            types = new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() };
            arguments = new Object[] { serviceName, hostName, tempDeployPath, warFileLocation };
        }

        try {
            mxUtil.invoke(config.toProperties(), objectName, "deployApplication", arguments, types);
            resultMessage = "Ok - Application " + tempDeployPath + " has deployed.";
        } catch (ApplicationException e) {
            LOGGER.debug("ApplicationException = " + e);
            throw createPluginException(e);
        } catch (RuntimeException e) {
            LOGGER.debug("RuntimeException = " + e);
            LOGGER.debug(e.getLocalizedMessage());

            resultMessage = "Failure - " + createPluginException(e).getMessage();
        }
        LOGGER.debug("Results = " + resultMessage);

        return resultMessage;
    }

    private String getNewRevisionForApplication(ConfigResponse config, String deployPath, String serviceName, String hostName) throws PluginException {
        String newRevision = null;

        if (Boolean.valueOf(config.getValue(MULTI_REVISION_CAPABLE))) {
            try {
                SortedSet<Application> applications = fetchApplications(config.toProperties(), serviceName, hostName);
                newRevision = ApplicationUtils.getNewRevisionForApplication(applications, deployPath);
            } catch (Exception e) {
                throw createPluginException(e);
            }
        }
        return newRevision;
    }

    private List<ApplicationIdentifier> getApplicationIdentifiers(ConfigResponse config) {
        final Set<String> keys = config.getKeys();
        final List<ApplicationIdentifier> applicationIdentifiers = new ArrayList<ApplicationIdentifier>();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.contains(APPLICATION + i)) {
                String applicationIdentifierString = config.getValue(APPLICATION + i);
                applicationIdentifiers.add(new ApplicationIdentifier(applicationIdentifierString));
            }
        }
        return applicationIdentifiers;
    }

    private Map<String, String> getConnectionInformation(ConfigResponse config) throws PluginException {

        Map<String, String> connectionInformation = new LinkedHashMap<String, String>();

        if (this.mxUtil.checkConnection(config)) {
            String serviceName = config.getValue(SERVICE_NAME, "Catalina");
            connectionInformation.put(SERVICE_NAME, serviceName);
            String hostName = config.getValue(HOST_NAME, "localhost");
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

    protected String getObjectName() {
        return "tcServer:type=Serviceability,name=Deployer";
    }

    private String convertDetailedStatusToOverallStatus(String detailedStatus) {
        String overallStatus = OVERALL_STATUS_STOPPED;
        LOGGER.debug("DETAILED STATUS = " + detailedStatus);
        if (detailedStatus.equals("AVAILABLE") || detailedStatus.equals("STARTED")) {
            overallStatus = OVERALL_STATUS_RUNNING;
        }
        return overallStatus;
    }

    private boolean isApplicationStopped(ApplicationIdentifier applicationIdentifier, String service, String host, Properties configProperties)
        throws PluginException {
        return OVERALL_STATUS_STOPPED.equals(fetchApplicationStatus(configProperties, service, host, applicationIdentifier.getName(),
            applicationIdentifier.getVersion()));
    }

    private boolean isApplicationRunning(ApplicationIdentifier applicationIdentifier, String service, String host, Properties configProperties)
        throws PluginException {
        return OVERALL_STATUS_RUNNING.equals(fetchApplicationStatus(configProperties, service, host, applicationIdentifier.getName(),
            applicationIdentifier.getVersion()));
    }

    public Map<String, List<String>> getServiceHostMappings(ConfigResponse config) throws PluginException {
        Map<String, List<String>> serviceHostMapping = new LinkedHashMap<String, List<String>>();
        if (this.mxUtil.checkConnection(config)) {
            String[] services;
            try {
                services = (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getServices", new Object[0], new String[0]);
                for (String service : services) {
                    String[] hosts = (String[]) mxUtil.invoke(config.toProperties(), getObjectName(), "getHosts", new Object[] { service },
                        new String[] { String.class.getName() });
                    serviceHostMapping.put(service, Arrays.asList(hosts));
                }
            } catch (final ApplicationException e) {
                throw createPluginException(e);
            }
        } else {
            throw new PluginException(
                "Unable to connect to the instance. Please verify the instance is running and whether the JMX configuration is correct.");
        }
        return serviceHostMapping;
    }

    public Set<Application> list(ConfigResponse config) throws PluginException {

        LOGGER.debug("CONFIG = " + config.toString());

        Set<Application> applicationSet;

        Map<String, String> connectionInformation = getConnectionInformation(config);

        boolean tomcat7 = Boolean.valueOf(config.getValue(MULTI_REVISION_CAPABLE));

        applicationSet = createApplicationStatusMapping(config.toProperties(), connectionInformation.get(SERVICE_NAME),
            connectionInformation.get(HOST_NAME), tomcat7);

        LOGGER.debug("APPSET: " + applicationSet);

        return applicationSet;
    }

    private String performJmxOperation(String operationName, ApplicationIdentifier applicationIdentifier, Properties configProperties,
        Map<String, String> connectionInformation, String resultMessageTemplate) throws PluginException {
        String resultMessage;
        try {
            String applicationPath = TomcatNameUtils.convertNameToPath(applicationIdentifier.getName());

            String objectName = getObjectName();

            boolean tcRuntime250OrLater = isTcRuntime250OrLater(objectName, operationName, 4, configProperties);

            String[] types;
            Object[] arguments;

            if (tcRuntime250OrLater) {
                types = new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() };
                arguments = new Object[] { connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), applicationPath,
                    convertVersionToPaddedString(applicationIdentifier.getVersion()) };
            } else {
                types = new String[] { String.class.getName(), String.class.getName(), String.class.getName() };
                arguments = new Object[] { connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), applicationPath };
            }

            mxUtil.invoke(configProperties, objectName, operationName, arguments, types);

            resultMessage = String.format(resultMessageTemplate, applicationIdentifier);
        } catch (RuntimeException e) {
            resultMessage = "Failure - " + createPluginException(e).getMessage();
        } catch (Exception e) {
            throw createPluginException(e);
        }
        return resultMessage;
    }

    private boolean isTcRuntime250OrLater(String objectName, String operationName, int expected25OrLaterArgumentCount, Properties config)
        throws PluginException {

        try {
            MBeanServerConnection mBeanServer = mxUtil.getMBeanServer(config);
            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(new ObjectName(objectName));
            for (MBeanOperationInfo operationInfo : mBeanInfo.getOperations()) {
                if (operationName.equals(operationInfo.getName()) && expected25OrLaterArgumentCount == operationInfo.getSignature().length) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw createPluginException(e);
        }
    }

    public Map<String, Object> reload(ConfigResponse config) throws PluginException {
        Map<String, String> connectionInformation = getConnectionInformation(config);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        String service = connectionInformation.get(SERVICE_NAME);
        String host = connectionInformation.get(HOST_NAME);

        Properties configProperties = config.toProperties();

        for (ApplicationIdentifier applicationIdentifier : getApplicationIdentifiers(config)) {
            String resultMessage;
            if (isApplicationRunning(applicationIdentifier, service, host, configProperties)) {
                resultMessage = performJmxOperation("reloadApplication", applicationIdentifier, configProperties, connectionInformation,
                    "Ok - Application %s has reloaded.");
            } else {
                resultMessage = String.format("Failure - Application %s is not running.", applicationIdentifier);
            }
            resultMap.put(applicationIdentifier.toString(), resultMessage);
        }

        return resultMap;
    }

    public Map<String, Object> start(ConfigResponse config) throws PluginException {
        Map<String, String> connectionInformation = getConnectionInformation(config);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        Properties configProperties = config.toProperties();

        for (ApplicationIdentifier applicationIdentifier : getApplicationIdentifiers(config)) {

            String service = connectionInformation.get(SERVICE_NAME);
            String host = connectionInformation.get(HOST_NAME);

            String resultMessage;

            if (isApplicationStopped(applicationIdentifier, service, host, configProperties)) {
                resultMessage = performJmxOperation("startApplication", applicationIdentifier, configProperties, connectionInformation,
                    "Ok - Application %s has started.");
            } else {
                resultMessage = "Ok - Application " + applicationIdentifier + " is already running.";
            }

            resultMap.put(applicationIdentifier.toString(), resultMessage);
        }

        return resultMap;
    }

    public Map<String, Object> stop(ConfigResponse config) throws PluginException {
        Map<String, String> connectionInformation = getConnectionInformation(config);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        Properties configProperties = config.toProperties();

        for (ApplicationIdentifier applicationIdentifier : getApplicationIdentifiers(config)) {
            String service = connectionInformation.get(SERVICE_NAME);
            String host = connectionInformation.get(HOST_NAME);

            String resultMessage;

            if (isApplicationRunning(applicationIdentifier, service, host, configProperties)) {
                resultMessage = performJmxOperation("stopApplication", applicationIdentifier, configProperties, connectionInformation,
                    "Ok - Application %s has stopped.");
            } else {
                resultMessage = "Ok - Application " + applicationIdentifier + " is already stopped.";
            }

            resultMap.put(applicationIdentifier.toString(), resultMessage);
        }

        return resultMap;
    }

    public Map<String, Object> undeploy(ConfigResponse config) throws PluginException {

        final Map<String, String> connectionInformation = getConnectionInformation(config);
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        String resultMessage = null;

        List<ApplicationIdentifier> applicationIdentifiers = getApplicationIdentifiers(config);

        for (ApplicationIdentifier applicationIdentifier : applicationIdentifiers) {

            try {
                // Ensure that the app has truly been undeployed as Windoze
                // aggressive file locking strategy can prevent app undeployment.
                // (https://issuetracker.springsource.com/browse/TCS-61)
                //
                // The real solution here is to configure the antiJARLocking and
                // antiResourceLocking features in tc Runtime's conf/context.xml file.
                // More info is available here:
                // http://tomcat.apache.org/tomcat-6.0-doc/config/context.html

                // Number of times to retry the undeploy
                int numRetries = 3;
                // Amount of time to sleep b/t undeploy attempts
                int sleepTime = 3000;

                String applicationPath = TomcatNameUtils.convertNameToPath(applicationIdentifier.getName());

                String objectName = getObjectName();
                String[] types;
                Object[] arguments;

                try {
                    if (isTcRuntime250OrLater(objectName, "undeployApplication", 4, config.toProperties())) {
                        types = new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName() };
                        arguments = new Object[] { connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), applicationPath,
                            convertVersionToPaddedString(applicationIdentifier.getVersion()) };
                    } else {
                        types = new String[] { String.class.getName(), String.class.getName(), String.class.getName() };
                        arguments = new Object[] { connectionInformation.get(SERVICE_NAME), connectionInformation.get(HOST_NAME), applicationPath };
                    }
                } catch (Exception e) {
                    throw new PluginException(e);
                }

                for (int i = 0; i < numRetries; ++i) {

                    LOGGER.debug("Undeploying app: " + applicationIdentifier);

                    // Attempt to undeploy the app

                    mxUtil.invoke(config.toProperties(), objectName, "undeployApplication", arguments, types);
                    LOGGER.debug("The undeployApplication command has been executed");

                    // Grab a list of the deployed apps from tc Server on the host
                    Set<Application> applicationList = list(config);

                    LOGGER.debug("Checking if the app actually was undeployed");

                    // Create a list of apps that are still deployed
                    List<ApplicationIdentifier> deployedAppsList = new ArrayList<ApplicationIdentifier>();
                    for (Application application : applicationList) {
                        deployedAppsList.add(new ApplicationIdentifier(application.getName(), application.getVersion()));
                    }

                    // Is the target app name still listed as still being deployed?
                    if (!deployedAppsList.contains(applicationIdentifier)) {
                        // Target app is not listed so it has been successfully undeployed
                        resultMessage = "Ok - Application " + applicationIdentifier + " has undeployed.";

                        // Do not retry; break the loop
                        numRetries = 0;
                    } else {
                        LOGGER.debug("The app named [" + applicationIdentifier + "] is still deployed");
                        // Target app is still listed so it has not been undeployed
                        // Retry again/loop again
                        --numRetries;

                        LOGGER.debug("Sleeping for " + sleepTime);
                        // Sleep for a bit to allow the Windoze file locking to settle
                        Thread.currentThread();
                        Thread.sleep(sleepTime);
                    }
                }
            } catch (ApplicationException e) {
                throw createPluginException(e);
            } catch (RuntimeException e) {
                resultMessage = "Failure - " + createPluginException(e).getMessage();
            } catch (InterruptedException e) {
                throw createPluginException(e);
            }

            resultMap.put(applicationIdentifier.toString(), resultMessage);
        }

        return resultMap;
    }

    /**
     * Using the info in the <tt>config</tt> parameter, grab the fully qualified appBase path (the combination of the
     * <tt>baseDir</tt> attribute from the Catalina Engine MBean and the <tt>appBase</tt> attribute from the Catalina
     * Host MBean).
     * 
     * @param config
     */
    public String getAppBase(ConfigResponse config) throws PluginException {
        String baseDir = null;
        String appBase = null;

        if (this.mxUtil.checkConnection(config)) {
            try {
                Map<String, String> connectionInformation = getConnectionInformation(config);

                String objectName = connectionInformation.get(SERVICE_NAME) + ":type=Engine";
                baseDir = (String) mxUtil.getValue(config.toProperties(), objectName, "baseDir");

                objectName = connectionInformation.get(SERVICE_NAME) + ":type=Host,host=" + connectionInformation.get(HOST_NAME);
                appBase = (String) mxUtil.getValue(config.toProperties(), objectName, "appBase");

            } catch (MalformedObjectNameException e) {
                throw createPluginException(e);
            } catch (AttributeNotFoundException e) {
                throw createPluginException(e);
            } catch (InstanceNotFoundException e) {
                throw createPluginException(e);
            } catch (MalformedURLException e) {
                throw createPluginException(e);
            } catch (MBeanException e) {
                throw createPluginException(e);
            } catch (ReflectionException e) {
                throw createPluginException(e);
            } catch (IOException e) {
                throw createPluginException(e);
            }
        }

        if (null == baseDir || null == appBase) {
            StringBuilder message = new StringBuilder("The fully qualified appBase directory could not be assembled");

            message.append(" (baseDir=");
            if (null == baseDir) {
                message.append("null");
            } else {
                message.append(baseDir);
            }

            message.append(" appBase=");
            if (null == appBase) {
                message.append("null");
            } else {
                message.append(appBase);
            }
            message.append(")");

            throw new PluginException(message.toString());
        }

        return baseDir + "/" + appBase;
    }
}
