// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.engine;

import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AttributeNotFoundException;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Logging;

public class HostConverter extends AbstractXmlElementConverter implements XmlElementConverter<Host> {

    private static final String WORK_DIR = "workDir";

    private static final String UNPACK_WARS = "unpackWARs";

    private static final String NAME = "name";

    private static final String DEPLOY_XML = "deployXML";

    private static final String DEPLOY_ON_STARTUP = "deployOnStartup";

    private static final String AUTO_DEPLOY = "autoDeploy";

    private static final String APP_BASE = "appBase";

    private AccessLogValveConverter<Host> hostAccessLogValveConverter = new AccessLogValveConverter<Host>();

    public Host convert(Element hostNode, Properties catalinaProperties) {
        // per Filip, assume default impl even if className is set to something
        // different while parsing
        Host host = new Host();
        String appBase = parseProperties(hostNode.getAttribute(APP_BASE), catalinaProperties);
        if (!(EMPTY_STRING.equals(appBase))) {
            host.setAppBase(appBase);
        }
        String autoDeploy = parseProperties(hostNode.getAttribute(AUTO_DEPLOY), catalinaProperties);
        if (!(EMPTY_STRING.equals(autoDeploy))) {
            host.setAutoDeploy(Boolean.valueOf(autoDeploy));
        }
        String deployOnStartup = parseProperties(hostNode.getAttribute(DEPLOY_ON_STARTUP), catalinaProperties);
        if (!(EMPTY_STRING.equals(deployOnStartup))) {
            host.setDeployOnStartup(Boolean.valueOf(deployOnStartup));
        }
        String deployXML = parseProperties(hostNode.getAttribute(DEPLOY_XML), catalinaProperties);
        if (!(EMPTY_STRING.equals(deployXML))) {
            host.setDeployXML(Boolean.valueOf(deployXML));
        }
        host.setLogging(createHostLogging(hostNode, catalinaProperties));
        final String name = parseProperties(hostNode.getAttribute(NAME), catalinaProperties);
        if (EMPTY_STRING.equals(name)) {
            throw new AttributeNotFoundException(NAME, "Host");
        }
        host.setName(name);
        host.setId(name);
        String unpackWARs = parseProperties(hostNode.getAttribute(UNPACK_WARS), catalinaProperties);
        if (!(EMPTY_STRING.equals(unpackWARs))) {
            host.setUnpackWARs(Boolean.valueOf(unpackWARs));
        }
        String workDir = parseProperties(hostNode.getAttribute(WORK_DIR), catalinaProperties);
        if (!(EMPTY_STRING.equals(workDir))) {
            host.setWorkDir(workDir);
        }
        return host;
    }

    public void convert(Document document, Element hostNode, Host from, Properties catalinaProperties) {
        // TODO create app base and work dir? tc Runtime doesn't do that?
        setAttribute(hostNode, APP_BASE, from.getAppBase(), catalinaProperties, false);
        setAttribute(hostNode, AUTO_DEPLOY, from.getAutoDeploy(), catalinaProperties, false);
        setAttribute(hostNode, DEPLOY_ON_STARTUP, from.getDeployOnStartup(), catalinaProperties, false);
        setAttribute(hostNode, DEPLOY_XML, from.getDeployXML(), catalinaProperties, false);
        setAttribute(hostNode, NAME, from.getName(), catalinaProperties, true);
        setAttribute(hostNode, UNPACK_WARS, from.getUnpackWARs(), catalinaProperties, false);
        setAttribute(hostNode, WORK_DIR, from.getWorkDir(), catalinaProperties, false);
        convertLogging(document, hostNode, from.getLogging(), catalinaProperties);
    }

    private void convertLogging(Document document, Element hostNode, Logging<Host> logging, Properties catalinaProperties) {
        Element accessLogValve = null;
        List<Element> valveNodes = getChildElements(hostNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("org.apache.catalina.valves.AccessLogValve".equals(valve.getAttribute("className"))) {
                if (!(logging.getEnabled())) {
                    hostNode.removeChild(valve);
                    return;
                } else {
                    accessLogValve = valve;
                    break;
                }
            }
        }
        if (logging.getEnabled()) {
            if (accessLogValve == null) {
                accessLogValve = document.createElement("Valve");
                accessLogValve.setAttribute("className", "org.apache.catalina.valves.AccessLogValve");
                hostNode.appendChild(accessLogValve);
            }
            hostAccessLogValveConverter.convert(document, accessLogValve, logging, catalinaProperties);
        }
    }

    private Logging<Host> createHostLogging(Element hostNode, Properties catalinaProperties) {
        List<Element> valveNodes = getChildElements(hostNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("org.apache.catalina.valves.AccessLogValve".equals(valve.getAttribute("className"))) {
                return hostAccessLogValveConverter.convert(valve, catalinaProperties);
            }
        }
        return new Logging<Host>();
    }

}
