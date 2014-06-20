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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.resources.jdbc;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;

public class TomcatConnectionPoolConverter extends AbstractXmlElementConverter implements XmlElementConverter<TomcatConnectionPool> {

    private static final String DATA_SOURCE = "Data Source";

    private static final String REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";

    private static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";

    private static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";

    private static final String TEST_ON_BORROW = "testOnBorrow";

    private static final String MAX_WAIT = "maxWait";

    private static final String MIN_IDLE = "minIdle";

    private static final String INITIAL_SIZE = "initialSize";

    private static final String MAX_IDLE = "maxIdle";

    private static final String MAX_ACTIVE = "maxActive";

    private static final String DEFAULT_AUTO_COMMIT = "defaultAutoCommit";

    private static final String LOG_ABANDONED = "logAbandoned";

    private static final String REMOVE_ABANDONED = "removeAbandoned";

    private static final String VALIDATION_QUERY = "validationQuery";

    private static final String TEST_WHILE_IDLE = "testWhileIdle";

    private static final String TEST_ON_RETURN = "testOnReturn";

    private static final String DEFAULT_CATALOG = "defaultCatalog";

    private static final String DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";

    private static final String DEFAULT_TRANSACTION_ISOLATION_DEFAULT = "DEFAULT";

    private static final String DEFAULT_READ_ONLY = "defaultReadOnly";

    private static final String JDBC_INTERCEPTORS = "jdbcInterceptors";

    private static final String VALIDATION_INTERVAL = "validationInterval";

    private static final String USE_EQUALS = "useEquals";

    private static final String JMX_ENABLED = "jmxEnabled";

    private static final String INIT_SQL = "initSQL";

    private static final String FAIR_QUEUE = "fairQueue";

    public void convert(Document document, Element resource, TomcatConnectionPool from, Properties catalinaProperties) {
        setAttribute(resource, FAIR_QUEUE, from.getFairQueue(), catalinaProperties, false);
        setAttribute(resource, INIT_SQL, from.getInitSQL(), catalinaProperties, false);
        setAttribute(resource, JMX_ENABLED, from.getJmxEnabled(), catalinaProperties, false);
        setAttribute(resource, USE_EQUALS, from.getUseEquals(), catalinaProperties, false);
        setAttribute(resource, VALIDATION_INTERVAL, from.getValidationInterval(), catalinaProperties, false);
        setAttribute(resource, JDBC_INTERCEPTORS, from.getJdbcInterceptors(), catalinaProperties, false);
        setAttribute(resource, DEFAULT_AUTO_COMMIT, from.getDefaultAutoCommit(), catalinaProperties, false);
        setAttribute(resource, DEFAULT_CATALOG, from.getDefaultCatalog(), catalinaProperties, false);
        setAttribute(resource, DEFAULT_READ_ONLY, from.getDefaultReadOnly(), catalinaProperties, false);

        if (!DEFAULT_TRANSACTION_ISOLATION_DEFAULT.equals(from.getDefaultTransactionIsolation())) {
            setAttribute(resource, DEFAULT_TRANSACTION_ISOLATION, from.getDefaultTransactionIsolation(), catalinaProperties, false);
        }

        setAttribute(resource, INITIAL_SIZE, from.getInitialSize(), catalinaProperties, false);
        setAttribute(resource, LOG_ABANDONED, from.getLogAbandoned(), catalinaProperties, false);
        setAttribute(resource, MAX_ACTIVE, from.getMaxActive(), catalinaProperties, false);
        setAttribute(resource, MAX_IDLE, from.getMaxIdle(), catalinaProperties, false);
        setAttribute(resource, MAX_WAIT, from.getMaxWait(), catalinaProperties, false);
        setAttribute(resource, MIN_EVICTABLE_IDLE_TIME_MILLIS, from.getMinEvictableIdleTimeMillis(), catalinaProperties, false);
        setAttribute(resource, MIN_IDLE, from.getMinIdle(), catalinaProperties, false);
        setAttribute(resource, REMOVE_ABANDONED, from.getRemoveAbandoned(), catalinaProperties, false);
        setAttribute(resource, REMOVE_ABANDONED_TIMEOUT, from.getRemoveAbandonedTimeout(), catalinaProperties, false);
        setAttribute(resource, TEST_ON_BORROW, from.getTestOnBorrow(), catalinaProperties, false);
        setAttribute(resource, TEST_ON_RETURN, from.getTestOnReturn(), catalinaProperties, false);
        setAttribute(resource, TEST_WHILE_IDLE, from.getTestWhileIdle(), catalinaProperties, false);
        setAttribute(resource, TIME_BETWEEN_EVICTION_RUNS_MILLIS, from.getTimeBetweenEvictionRunsMillis(), catalinaProperties, false);
        setAttribute(resource, VALIDATION_QUERY, from.getValidationQuery(), catalinaProperties, false);
    }

    public TomcatConnectionPool convert(final Element resource, final Properties catalinaProperties) {
        final TomcatConnectionPool connectionPool = new TomcatConnectionPool();
        String fairQueue = parseProperties(resource.getAttribute(FAIR_QUEUE), catalinaProperties);
        if (!(EMPTY_STRING.equals(fairQueue))) {
            connectionPool.setFairQueue(Boolean.valueOf(fairQueue));
        }
        String initSQL = parseProperties(resource.getAttribute(INIT_SQL), catalinaProperties);
        if (!(EMPTY_STRING.equals(initSQL))) {
            connectionPool.setInitSQL(initSQL);
        }
        String jmxEnabled = parseProperties(resource.getAttribute(JMX_ENABLED), catalinaProperties);
        if (!(EMPTY_STRING.equals(jmxEnabled))) {
            connectionPool.setJmxEnabled(Boolean.valueOf(jmxEnabled));
        }
        String useEquals = parseProperties(resource.getAttribute(USE_EQUALS), catalinaProperties);
        if (!(EMPTY_STRING.equals(useEquals))) {
            connectionPool.setUseEquals(Boolean.valueOf(useEquals));
        }
        String validationInterval = parseProperties(resource.getAttribute(VALIDATION_INTERVAL), catalinaProperties);
        if (!(EMPTY_STRING.equals(validationInterval))) {
            try {
                connectionPool.setValidationInterval(Long.valueOf(validationInterval));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, VALIDATION_INTERVAL, validationInterval);
            }
        }
        String jdbcInterceptors = parseProperties(resource.getAttribute(JDBC_INTERCEPTORS), catalinaProperties);
        if (!(EMPTY_STRING.equals(jdbcInterceptors))) {
            connectionPool.setJdbcInterceptors(jdbcInterceptors);
        }
        String defaultReadOnly = parseProperties(resource.getAttribute(DEFAULT_READ_ONLY), catalinaProperties);
        if (!(EMPTY_STRING.equals(defaultReadOnly))) {
            connectionPool.setDefaultReadOnly(Boolean.valueOf(defaultReadOnly));
        }

        String defaultTransactionIsolation = parseProperties(resource.getAttribute(DEFAULT_TRANSACTION_ISOLATION), catalinaProperties);
        if (EMPTY_STRING.equals(defaultTransactionIsolation)) {
            connectionPool.setDefaultTransactionIsolation(DEFAULT_TRANSACTION_ISOLATION_DEFAULT);
        } else {
            connectionPool.setDefaultTransactionIsolation(defaultTransactionIsolation);
        }

        String defaultCatalog = parseProperties(resource.getAttribute(DEFAULT_CATALOG), catalinaProperties);
        if (!(EMPTY_STRING.equals(defaultCatalog))) {
            connectionPool.setDefaultCatalog(defaultCatalog);
        }
        String testOnReturn = parseProperties(resource.getAttribute(TEST_ON_RETURN), catalinaProperties);
        if (!(EMPTY_STRING.equals(testOnReturn))) {
            connectionPool.setTestOnReturn(Boolean.valueOf(testOnReturn));
        }
        String testWhileIdle = parseProperties(resource.getAttribute(TEST_WHILE_IDLE), catalinaProperties);
        if (!(EMPTY_STRING.equals(testWhileIdle))) {
            connectionPool.setTestWhileIdle(Boolean.valueOf(testWhileIdle));
        }
        String validationQuery = parseProperties(resource.getAttribute(VALIDATION_QUERY), catalinaProperties);
        if (!(EMPTY_STRING.equals(validationQuery))) {
            connectionPool.setValidationQuery(validationQuery);
        }
        String removeAbandoned = parseProperties(resource.getAttribute(REMOVE_ABANDONED), catalinaProperties);
        if (!(EMPTY_STRING.equals(removeAbandoned))) {
            connectionPool.setRemoveAbandoned(Boolean.valueOf(removeAbandoned));
        }
        String logAbandoned = parseProperties(resource.getAttribute(LOG_ABANDONED), catalinaProperties);
        if (!(EMPTY_STRING.equals(logAbandoned))) {
            connectionPool.setLogAbandoned(Boolean.valueOf(logAbandoned));
        }
        String defaultAutoCommit = parseProperties(resource.getAttribute(DEFAULT_AUTO_COMMIT), catalinaProperties);
        if (!(EMPTY_STRING.equals(defaultAutoCommit))) {
            connectionPool.setDefaultAutoCommit(Boolean.valueOf(defaultAutoCommit));
        }
        String maxActive = parseProperties(resource.getAttribute(MAX_ACTIVE), catalinaProperties);
        if (!(EMPTY_STRING.equals(maxActive))) {
            try {
                connectionPool.setMaxActive(Long.valueOf(maxActive));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, MAX_ACTIVE, maxActive);
            }
        }
        String maxIdle = parseProperties(resource.getAttribute(MAX_IDLE), catalinaProperties);
        if (!(EMPTY_STRING.equals(maxIdle))) {
            try {
                connectionPool.setMaxIdle(Long.valueOf(maxIdle));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, MAX_IDLE, maxIdle);
            }
        }
        String initialSize = parseProperties(resource.getAttribute(INITIAL_SIZE), catalinaProperties);
        if (!(EMPTY_STRING.equals(initialSize))) {
            try {
                connectionPool.setInitialSize(Long.valueOf(initialSize));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, INITIAL_SIZE, initialSize);
            }
        }
        String minIdle = parseProperties(resource.getAttribute(MIN_IDLE), catalinaProperties);
        if (!(EMPTY_STRING.equals(minIdle))) {
            try {
                connectionPool.setMinIdle(Long.valueOf(minIdle));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, MIN_IDLE, minIdle);
            }
        }
        String maxWait = parseProperties(resource.getAttribute(MAX_WAIT), catalinaProperties);
        if (!(EMPTY_STRING.equals(maxWait))) {
            try {
                connectionPool.setMaxWait(Long.valueOf(maxWait));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, MAX_WAIT, maxWait);
            }
        }
        String testOnBorrow = parseProperties(resource.getAttribute(TEST_ON_BORROW), catalinaProperties);
        if (!(EMPTY_STRING.equals(testOnBorrow))) {
            connectionPool.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
        }
        String timeBetweenEvictionRunsMillis = parseProperties(resource.getAttribute(TIME_BETWEEN_EVICTION_RUNS_MILLIS), catalinaProperties);
        if (!(EMPTY_STRING.equals(timeBetweenEvictionRunsMillis))) {
            try {
                connectionPool.setTimeBetweenEvictionRunsMillis(Long.valueOf(timeBetweenEvictionRunsMillis));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, TIME_BETWEEN_EVICTION_RUNS_MILLIS, timeBetweenEvictionRunsMillis);
            }
        }
        String minEvictableIdleTimeMillis = parseProperties(resource.getAttribute(MIN_EVICTABLE_IDLE_TIME_MILLIS), catalinaProperties);
        if (!(EMPTY_STRING.equals(minEvictableIdleTimeMillis))) {
            try {
                connectionPool.setMinEvictableIdleTimeMillis(Long.valueOf(minEvictableIdleTimeMillis));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, MIN_EVICTABLE_IDLE_TIME_MILLIS, minEvictableIdleTimeMillis);
            }
        }
        String removeAbandonedTimeout = parseProperties(resource.getAttribute(REMOVE_ABANDONED_TIMEOUT), catalinaProperties);
        if (!(EMPTY_STRING.equals(removeAbandonedTimeout))) {
            try {
                connectionPool.setRemoveAbandonedTimeout(Long.valueOf(removeAbandonedTimeout));
            } catch (NumberFormatException e) {
                logNonNumericValue(DATA_SOURCE, REMOVE_ABANDONED_TIMEOUT, removeAbandonedTimeout);
            }
        }
        return connectionPool;
    }

}
