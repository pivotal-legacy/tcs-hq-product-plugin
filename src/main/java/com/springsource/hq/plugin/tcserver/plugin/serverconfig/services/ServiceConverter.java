// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services;

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
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.InvalidNodeException;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector.AjpConnectorConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.connector.HttpConnectorConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.engine.EngineConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;

public class ServiceConverter extends AbstractXmlElementConverter implements XmlElementConverter<Service> {

    private XmlElementConverter<AjpConnector> ajpConnectorConverter = new AjpConnectorConverter();

    private XmlElementConverter<Engine> engineConverter = new EngineConverter();

    private XmlElementConverter<HttpConnector> httpConnectorConverter = new HttpConnectorConverter();

    public Service convert(Element serviceNode, Properties catalinaProperties) {
        Service service = new Service();
        final String name = parseProperties(serviceNode.getAttribute("name"), catalinaProperties);
        if (EMPTY_STRING.equals(name)) {
            throw new AttributeNotFoundException("name", "Service");
        }
        service.setName(name);
        service.setId(name);
        service.setEngine(createEngine(serviceNode, catalinaProperties));
        service.setConnectors(createConnectors(serviceNode, catalinaProperties));
        return service;
    }

    public void convert(Document document, Element service, Service from, Properties catalinaProperties) {
        setAttribute(service, "name", from.getName(), catalinaProperties, true);
        convertEngine(document, service, from, catalinaProperties);
        convertConnectors(document, service, from.getConnectors(), catalinaProperties);
    }

    private void convertConnectors(Document document, Element service, Set<Connector> connectors, Properties catalinaProperties) {
        final Set<String> connectorIds = new HashSet<String>(connectors.size());
        for (Connector connector : connectors) {
            connectorIds.add(connector.getId());
        }
        final Map<String, Element> connectorElements = new HashMap<String, Element>();
        List<Element> connectorNodes = getChildElements(service, "Connector");
        for (int i = 0; i < connectorNodes.size(); i++) {
            final String address = parseProperties(((Element) connectorNodes.get(i)).getAttribute("address"), catalinaProperties);
            final String port = parseProperties(((Element) connectorNodes.get(i)).getAttribute("port"), catalinaProperties);
            if (connectorIds.contains(address + ":" + port)) {
                connectorElements.put(address + ":" + port, (Element) connectorNodes.get(i));
            } else if (isAjpConnector(((Element) connectorNodes.get(i)).getAttribute("protocol"))
                || isHttpConnector(((Element) connectorNodes.get(i)).getAttribute("protocol"))) {
                // only remove HTTP or AJP connectors
                service.removeChild(connectorNodes.get(i));
            }
        }
        // add new ones or update existing ones
        for (Connector connector : connectors) {
            Element connectorElement = connectorElements.get(connector.getId());
            if (connectorElement == null) {
                connectorElement = document.createElement("Connector");
                service.appendChild(connectorElement);
            }
            if (connector instanceof HttpConnector) {
                httpConnectorConverter.convert(document, connectorElement, (HttpConnector) connector, catalinaProperties);
            } else if (connector instanceof AjpConnector) {
                ajpConnectorConverter.convert(document, connectorElement, (AjpConnector) connector, catalinaProperties);
            }
        }

    }

    private boolean isHttpConnector(final String protocol) {
        return ("HTTP/1.1".equals(protocol) || "org.apache.coyote.http11.Http11NioProtocol".equals(protocol)
            || "org.apache.coyote.http11.Http11Protocol".equals(protocol) || "org.apache.coyote.http11.Http11AprProtocol".equals(protocol));
    }

    private boolean isAjpConnector(final String protocol) {
        return ("AJP/1.3".equals(protocol) || "org.apache.jk.server.JkCoyoteHandler".equals(protocol)
            || "org.apache.coyote.ajp.AjpAprProtocol".equals(protocol) || "org.apache.coyote.ajp.AjpProtocol".equals(protocol));
    }

    private void convertEngine(Document document, Element service, Service from, Properties catalinaProperties) {
        Element engineElement = null;
        List<Element> engines = getChildElements(service, "Engine");
        if (engines.size() != 1) {
            engineElement = document.createElement("Engine");
            service.appendChild(engineElement);
        } else {
            engineElement = (Element) engines.get(0);
        }
        engineConverter.convert(document, engineElement, from.getEngine(), catalinaProperties);
    }

    private Set<Connector> createConnectors(Element service, Properties catalinaProperties) {
        Set<Connector> connectors = new HashSet<Connector>();
        List<Element> connectorNodes = getChildElements(service, "Connector");
        for (int i = 0; i < connectorNodes.size(); i++) {
            Element connectorNode = (Element) connectorNodes.get(i);
            final String protocol = parseProperties(connectorNode.getAttribute("protocol"), catalinaProperties);
            if (isHttpConnector(protocol)) {
                final HttpConnector httpConnector = httpConnectorConverter.convert(connectorNode, catalinaProperties);
                connectors.add(httpConnector);
            } else if (isAjpConnector(protocol)) {
                final AjpConnector ajpConnector = ajpConnectorConverter.convert(connectorNode, catalinaProperties);
                connectors.add(ajpConnector);
            }
        }
        return connectors;
    }

    private Engine createEngine(Element service, Properties catalinaProperties) {
        List<Element> engines = getChildElements(service, "Engine");
        if (engines.size() != 1) {
            throw new InvalidNodeException("Found 0 or multiple Engine elements under Service " + service.getAttribute("name"));
        }
        final Element engineNode = (Element) engines.get(0);
        return engineConverter.convert(engineNode, catalinaProperties);
    }

}
