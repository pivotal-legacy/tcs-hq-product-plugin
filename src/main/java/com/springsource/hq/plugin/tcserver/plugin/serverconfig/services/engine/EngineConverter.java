// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AttributeNotFoundException;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Logging;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.ThreadDiagnostics;

public class EngineConverter extends AbstractXmlElementConverter implements XmlElementConverter<Engine> {

    private static final String JVM_ROUTE = "jvmRoute";

    private static final String NAME = "name";

    private static final String DEFAULT_HOST = "defaultHost";

    private XmlElementConverter<Logging<Engine>> engineAccessLogValveConverter = new AccessLogValveConverter<Engine>();

    private XmlElementConverter<ThreadDiagnostics> engineThreadDiagnosticsValveConverter = new ThreadDiagnosticsValveConverter();

    private XmlElementConverter<Host> hostConverter = new HostConverter();

    public Engine convert(Element engineNode, Properties catalinaProperties) {
        final Engine engine = new Engine();
        final String defaultHost = parseProperties(engineNode.getAttribute(DEFAULT_HOST), catalinaProperties);
        if (EMPTY_STRING.equals(defaultHost)) {
            throw new AttributeNotFoundException(DEFAULT_HOST, "Engine");
        }
        engine.setDefaultHost(defaultHost);
        final String name = parseProperties(engineNode.getAttribute(NAME), catalinaProperties);
        if (EMPTY_STRING.equals(name)) {
            throw new AttributeNotFoundException(NAME, "Engine");
        }
        engine.setName(name);
        String jvmRoute = parseProperties(engineNode.getAttribute(JVM_ROUTE), catalinaProperties);
        if (!(EMPTY_STRING.equals(jvmRoute))) {
            engine.setJvmRoute(jvmRoute);
        }
        engine.setHosts(createHosts(engineNode, catalinaProperties));
        engine.setLogging(createEngineLogging(engineNode, catalinaProperties));
        engine.setThreadDiagnostics(createEngineThreadDiagnostics(engineNode, catalinaProperties));
        return engine;
    }

    public void convert(Document document, Element engine, Engine from, Properties catalinaProperties) {
        // always converting an existing Engine
        setAttribute(engine, DEFAULT_HOST, from.getDefaultHost(), catalinaProperties, true);
        setAttribute(engine, NAME, from.getName(), catalinaProperties, true);
        setAttribute(engine, JVM_ROUTE, from.getJvmRoute(), catalinaProperties, false);
        convertHosts(document, engine, from.getHosts(), catalinaProperties);
        convertLogging(document, engine, from.getLogging(), catalinaProperties);
        convertThreadDiagnostics(document, engine, from.getThreadDiagnostics(), catalinaProperties);
    }

    private void convertHosts(Document document, Element engineNode, Set<Host> hosts, Properties catalinaProperties) {
        final List<Element> hostNodes = getChildElements(engineNode, "Host");
        // remove services that were removed via GUI
        final Set<String> hostIds = new HashSet<String>(hosts.size());
        for (Host host : hosts) {
            hostIds.add(host.getId());
        }
        final Map<String, Element> hostElements = new HashMap<String, Element>();
        for (int i = 0; i < hostNodes.size(); i++) {
            final String elementName = parseProperties(((Element) hostNodes.get(i)).getAttribute(NAME), catalinaProperties);
            if (hostIds.contains(elementName)) {
                hostElements.put(elementName, (Element) hostNodes.get(i));
            } else {
                engineNode.removeChild(hostNodes.get(i));
            }
        }
        // add new ones or update existing ones
        for (Host host : hosts) {
            if (hostElements.get(host.getId()) == null) {
                final Element hostElement = document.createElement("Host");
                hostConverter.convert(document, hostElement, host, catalinaProperties);
                engineNode.appendChild(hostElement);
            } else {
                hostConverter.convert(document, hostElements.get(host.getId()), host, catalinaProperties);
            }
        }
    }

    private void convertLogging(Document document, Element engineNode, Logging<Engine> logging, Properties catalinaProperties) {
        Element accessLogValve = null;
        List<Element> valveNodes = getChildElements(engineNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("org.apache.catalina.valves.AccessLogValve".equals(valve.getAttribute("className"))) {
                if (!(logging.getEnabled())) {
                    engineNode.removeChild(valve);
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
                engineNode.appendChild(accessLogValve);
            }
            engineAccessLogValveConverter.convert(document, accessLogValve, logging, catalinaProperties);
        }
    }

    private Logging<Engine> createEngineLogging(Element engineNode, Properties catalinaProperties) {
        List<Element> valveNodes = getChildElements(engineNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("org.apache.catalina.valves.AccessLogValve".equals(valve.getAttribute("className"))) {
                return engineAccessLogValveConverter.convert(valve, catalinaProperties);
            }
        }
        return new Logging<Engine>();
    }

    private void convertThreadDiagnostics(Document document, Element engineNode, ThreadDiagnostics threadDiagnositcs, Properties catalinaProperties) {
        Element threadDiagnositcsValve = null;
        List<Element> valveNodes = getChildElements(engineNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("com.springsource.tcserver.serviceability.request.ThreadDiagnosticsValve".equals(valve.getAttribute("className"))) {
                if (!(threadDiagnositcs.getEnabled())) {
                    engineNode.removeChild(valve);
                    return;
                } else {
                    threadDiagnositcsValve = valve;
                    break;
                }
            }
        }
        if (threadDiagnositcs.getEnabled()) {
            if (threadDiagnositcsValve == null) {
                threadDiagnositcsValve = document.createElement("Valve");
                threadDiagnositcsValve.setAttribute("className", "com.springsource.tcserver.serviceability.request.ThreadDiagnosticsValve");
                engineNode.appendChild(threadDiagnositcsValve);
            }
            engineThreadDiagnosticsValveConverter.convert(document, threadDiagnositcsValve, threadDiagnositcs, catalinaProperties);
        }
    }

    private ThreadDiagnostics createEngineThreadDiagnostics(Element engineNode, Properties catalinaProperties) {
        List<Element> valveNodes = getChildElements(engineNode, "Valve");
        for (int i = 0; i < valveNodes.size(); i++) {
            final Element valve = (Element) valveNodes.get(i);
            if ("com.springsource.tcserver.serviceability.request.ThreadDiagnosticsValve".equals(valve.getAttribute("className"))) {
                return engineThreadDiagnosticsValveConverter.convert(valve, catalinaProperties);
            }
        }
        return new ThreadDiagnostics();
    }

    private Set<Host> createHosts(final Element engine, Properties catalinaProperties) {
        Set<Host> hosts = new HashSet<Host>();
        List<Element> hostNodes = getChildElements(engine, "Host");
        for (int i = 0; i < hostNodes.size(); i++) {
            final Host host = hostConverter.convert((Element) hostNodes.get(i), catalinaProperties);
            hosts.add(host);
        }
        return hosts;
    }

}
