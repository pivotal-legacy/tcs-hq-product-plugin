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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.resources.jdbc;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AttributeNotFoundException;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.General;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;

public class DataSourceConverter extends AbstractXmlElementConverter implements XmlElementConverter<DataSource> {

    private static final String ATTRIBUTE_FACTORY = "factory";

    private static final String ATTRIBUTE_CONNECTION_PROPERTIES = "connectionProperties";

    private static final String ATTRIBUTE_URL = "url";

    private static final String ATTRIBUTE_USERNAME = "username";

    private static final String ATTRIBUTE_PASSWORD = "password";

    private static final String ATTRIBUTE_DRIVER_CLASS_NAME = "driverClassName";

    private static final String ATTRIBUTE_NAME = "name";

    private static final String DATA_SOURCE_FACTORY_DIAGNOSTICS = "com.springsource.tcserver.serviceability.request.DataSourceFactory";

    private static final String DATA_SOURCE_FACTORY_BASIC = "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory";

    private static final String DATA_SOURCE_FACTORY_HIGH_CONCURRENCY = "org.apache.tomcat.jdbc.pool.DataSourceFactory";

    private final XmlElementConverter<TomcatConnectionPool> tomcatConnectionPoolConverter = new TomcatConnectionPoolConverter();

    private final XmlElementConverter<DbcpConnectionPool> dbcpConnectionPoolConverter = new DbcpConnectionPoolConverter();

    public DataSource convert(final Element resource, final Properties catalinaProperties) {
        final String factory = resource.getAttribute(ATTRIBUTE_FACTORY);
        if (DATA_SOURCE_FACTORY_BASIC.equals(factory)) {
            return createDbcpDataSource(resource, catalinaProperties);
        } else if (EMPTY_STRING.equals(factory) || DATA_SOURCE_FACTORY_HIGH_CONCURRENCY.equals(factory)
            || DATA_SOURCE_FACTORY_DIAGNOSTICS.equals(factory)) {
            boolean gatherDiagnostics = DATA_SOURCE_FACTORY_DIAGNOSTICS.equals(factory);
            return createTomcatDataSource(resource, catalinaProperties, gatherDiagnostics);
        }
        return null;
    }

    private DataSource createDbcpDataSource(final Element resource, final Properties catalinaProperties) {
        final DbcpDataSource dataSource = new DbcpDataSource();
        dataSource.setGeneral(createGeneral(resource, catalinaProperties));
        dataSource.setId(dataSource.getGeneral().getJndiName());
        dataSource.setConnection(createConnection(resource, catalinaProperties));
        dataSource.setConnectionPool(dbcpConnectionPoolConverter.convert(resource, catalinaProperties));
        return dataSource;
    }

    private DataSource createTomcatDataSource(Element resource, Properties catalinaProperties, boolean gatherDiagnostics) {
        final TomcatDataSource dataSource = new TomcatDataSource();
        dataSource.setGeneral(createGeneral(resource, catalinaProperties));
        dataSource.setId(dataSource.getGeneral().getJndiName());
        dataSource.setConnection(createConnection(resource, catalinaProperties));
        dataSource.setConnectionPool(tomcatConnectionPoolConverter.convert(resource, catalinaProperties));
        dataSource.setGatherDiagnostics(gatherDiagnostics);
        return dataSource;
    }

    public void convert(Document document, Element resource, DataSource from, Properties catalinaProperties) {
        setAttribute(resource, ATTRIBUTE_NAME, from.getGeneral().getJndiName(), catalinaProperties, true);
        setAttribute(resource, ATTRIBUTE_DRIVER_CLASS_NAME, from.getConnection().getDriverClassName(), catalinaProperties, true);
        setAttribute(resource, ATTRIBUTE_PASSWORD, from.getConnection().getPassword(), catalinaProperties, true);
        setAttribute(resource, ATTRIBUTE_USERNAME, from.getConnection().getUsername(), catalinaProperties, true);
        setAttribute(resource, ATTRIBUTE_URL, from.getConnection().getUrl(), catalinaProperties, true);
        setAttribute(resource, ATTRIBUTE_CONNECTION_PROPERTIES, from.getConnection().getConnectionProperties(), catalinaProperties, false);
        if (from instanceof DbcpDataSource) {
            setAttribute(resource, ATTRIBUTE_FACTORY, DATA_SOURCE_FACTORY_BASIC, catalinaProperties, true);
            dbcpConnectionPoolConverter.convert(document, resource, ((DbcpDataSource) from).getConnectionPool(), catalinaProperties);
        } else {
            TomcatDataSource tomcatDataSource = (TomcatDataSource) from;
            String dataSourceFactory = getDataSourceFactory(tomcatDataSource);

            setAttribute(resource, ATTRIBUTE_FACTORY, dataSourceFactory, catalinaProperties, true);
            tomcatConnectionPoolConverter.convert(document, resource, tomcatDataSource.getConnectionPool(), catalinaProperties);
        }
    }

    private String getDataSourceFactory(TomcatDataSource tomcatDataSource) {
        if (tomcatDataSource.isGatherDiagnostics()) {
            return DATA_SOURCE_FACTORY_DIAGNOSTICS;
        } else {
            return DATA_SOURCE_FACTORY_HIGH_CONCURRENCY;
        }
    }

    private Connection createConnection(final Element resource, final Properties catalinaProperties) {
        final Connection connection = new Connection();
        final String driverClassName = parseProperties(resource.getAttribute(ATTRIBUTE_DRIVER_CLASS_NAME), catalinaProperties);
        if (EMPTY_STRING.equals(driverClassName)) {
            throw new AttributeNotFoundException(ATTRIBUTE_DRIVER_CLASS_NAME, "Data Source");
        }
        connection.setDriverClassName(driverClassName);
        final String password = parseProperties(resource.getAttribute(ATTRIBUTE_PASSWORD), catalinaProperties);
        connection.setPassword(password);
        final String username = parseProperties(resource.getAttribute(ATTRIBUTE_USERNAME), catalinaProperties);
        connection.setUsername(username);
        final String url = parseProperties(resource.getAttribute(ATTRIBUTE_URL), catalinaProperties);
        if (EMPTY_STRING.equals(url)) {
            throw new AttributeNotFoundException(ATTRIBUTE_URL, "Data Source");
        }
        connection.setUrl(url);
        final String connectionProperties = parseProperties(resource.getAttribute(ATTRIBUTE_CONNECTION_PROPERTIES), catalinaProperties);
        if (!(EMPTY_STRING.equals(connectionProperties))) {
            connection.setConnectionProperties(connectionProperties);
        }
        return connection;
    }

    private General createGeneral(final Element resource, final Properties catalinaProperties) {
        final General general = new General();
        final String jndiName = parseProperties(resource.getAttribute(ATTRIBUTE_NAME), catalinaProperties);
        if (EMPTY_STRING.equals(jndiName)) {
            throw new AttributeNotFoundException(ATTRIBUTE_NAME, "Data Source");
        }
        general.setJndiName(jndiName);
        return general;
    }

    public boolean isDataSourceFactorySupported(final Element dataSource, final Properties catalinaProperties) {
        String factory = this.parseProperties(dataSource.getAttribute(ATTRIBUTE_FACTORY), catalinaProperties).trim();
        return factory.equals(DATA_SOURCE_FACTORY_BASIC) || factory.equals(DATA_SOURCE_FACTORY_HIGH_CONCURRENCY) || factory.equals(EMPTY_STRING)
            || factory.equals(DATA_SOURCE_FACTORY_DIAGNOSTICS);
    }

}
