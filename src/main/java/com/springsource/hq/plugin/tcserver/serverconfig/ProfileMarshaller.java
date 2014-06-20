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

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;

/**
 * Profile marshaller used to convert Profile <-> xml. Faux implementation of org.springframework.oxm.Marshaller and
 * org.springframework.oxm.Unmarshaller. TODO implement these interfaces once they are available in Spring 3.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@Component
public class ProfileMarshaller {

    private JAXBContext jaxbContext;

    private Schema profileSchema;

    public ProfileMarshaller() throws JAXBException, SAXException, IOException {
        jaxbContext = JAXBContext.newInstance(Profile.class, TomcatDataSource.class, DbcpDataSource.class, AjpConnector.class, HttpConnector.class);
        Resource schemaResource = new ClassPathResource("tomcatserverconfig-profile-2.0.xsd", Profile.class);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        profileSchema = schemaFactory.newSchema(schemaResource.getURL());
    }

    public void marshal(Profile graph, Result result) throws JAXBException, IOException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(profileSchema);
        marshaller.marshal(graph, result);
    }

    public Profile unmarshal(Source source) throws JAXBException, IOException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(profileSchema);
        return (Profile) unmarshaller.unmarshal(source);
    }

    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        jaxbContext.generateSchema(outputResolver);
    }

    public boolean supports(Class<?> clazz) {
        return Profile.class.equals(clazz);
    }

}
