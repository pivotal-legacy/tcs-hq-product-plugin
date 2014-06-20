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

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Tomcat based connection pool settings. (http://people.apache.org/~fhanik/tomcat/jdbc-pool.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "tomcat-connection-pool")
public class TomcatConnectionPool extends ConnectionPool {

    private static final long DEFAULT_REMOVE_ABANDONED_TIMEOUT = 60l;

    private static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 5000l;

    private static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS = 5000l;

    private static final boolean DEFAULT_TEST_ON_BORROW = false;

    private static final long DEFAULT_MAX_WAIT = 30000l;

    private static final long DEFAULT_MIN_IDLE = 10l;

    private static final long DEFAULT_INITIAL_SIZE = 10l;

    private static final long DEFAULT_MAX_IDLE = 100l;

    private static final long DEFAULT_MAX_ACTIVE = 100l;

    private static final boolean DEFAULT_FAIR_QUEUE = false;

    private static final boolean DEFAULT_JMX_ENABLED = true;

    private static final boolean DEFAULT_LOG_ABANDONED = false;

    private static final boolean DEFAULT_REMOVE_ABANDONED = false;

    private static final boolean DEFAULT_TEST_ON_RETURN = false;

    private static final boolean DEFAULT_TEST_WHILE_IDLE = false;

    private static final boolean DEFAULT_USE_EQUALS = false;

    private static final long DEFAULT_VALIDATION_INTERVAL = 30000l;

    /**
     * The default auto-commit state of connections created by this pool. [true]
     */
    private Boolean defaultAutoCommit;

    /**
     * The default catalog of connections created by this pool.
     */
    private String defaultCatalog;

    /**
     * The default read-only state of connections created by this pool. If not set then the setReadOnly method will not
     * be called. (Some drivers don't support read only mode, ex: Informix) [driver default]
     */
    private Boolean defaultReadOnly;

    /**
     * The default TransactionIsolation state of connections created by this pool. One of the following: (see javadoc )
     * 
     * <ul>
     * <li>NONE</li>
     * <li>READ_COMMITTED</li>
     * <li>READ_UNCOMMITTED</li>
     * <li>REPEATABLE_READ</li>
     * <li>SERIALIZABLE</li>
     * </ul>
     * 
     * <p>
     * [driver default]
     */
    private String defaultTransactionIsolation;

    /**
     * Set to true if you wish that calls to getConnection should be treated fairly in a true FIFO fashion. This uses
     * the org.apache.tomcat.jdbc.pool.FairBlockingQueue implementation for the list of the idle connections. The
     * default value is false. This flag is required when you want to use asynchronous connection retrieval.
     */
    private Boolean fairQueue = DEFAULT_FAIR_QUEUE;

    /**
     * The initial number of connections that are created when the pool is started. Since: 1.2 [0]
     */
    private Long initialSize = DEFAULT_INITIAL_SIZE;

    /**
     * A custom query to be run when a connection is first created. The default value is null.
     */
    private String initSQL;

    /**
     * A semicolon separated list of classnames extending org.apache.tomcat.jdbc.pool.JdbcInterceptor class. These
     * interceptors will be inserted as an interceptor into the chain of operations on a java.sql.Connection object. The
     * default value is null. Predefined interceptors: org.apache.tomcat.jdbc.pool.interceptor.ConnectionState - keeps
     * track of auto commit, read only, catalog and transaction isolation level.
     * org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer - keeps track of opened statements, and closes them
     * when the connection is returned to the pool.
     */
    private String jdbcInterceptors;

    /**
     * Register the pool with JMX or not. The default value is true.
     */
    private Boolean jmxEnabled = DEFAULT_JMX_ENABLED;

    /**
     * Flag to log stack traces for application code which abandoned a Statement or Connection. Logging of abandoned
     * Statements and Connections adds overhead for every Connection open or new Statement because a stack trace has to
     * be generated. [false]
     */
    private Boolean logAbandoned = DEFAULT_LOG_ABANDONED;

    /**
     * The maximum number of active connections that can be allocated from this pool at the same time, or negative for
     * no limit. [8]
     */
    private Long maxActive = DEFAULT_MAX_ACTIVE;

    /**
     * The maximum number of active connections that can be allocated from this pool at the same time, or negative for
     * no limit. [8]
     */
    private Long maxIdle = DEFAULT_MAX_IDLE;

    /**
     * The maximum number of milliseconds that the pool will wait (when there are no available connections) for a
     * connection to be returned before throwing an exception, or -1 to wait indefinitely. [indefinitely]
     */
    private Long maxWait = DEFAULT_MAX_WAIT;

    /**
     * The minimum amount of time an object may sit idle in the pool before it is eligable for eviction by the idle
     * object evictor (if any). [1000 * 60 * 30]
     */
    private Long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * The minimum number of connections that can remain idle in the pool, without extra ones being created, or zero to
     * create none. [0]
     */
    private Long minIdle = DEFAULT_MIN_IDLE;

    /**
     * Flag to remove abandoned connections if they exceed the removeAbandonedTimout. If set to true a connection is
     * considered abandoned and eligible for removal if it has been idle longer than the removeAbandonedTimeout. Setting
     * this to true can recover db connections from poorly written applications which fail to close a connection.
     * [false]
     */
    private Boolean removeAbandoned = DEFAULT_REMOVE_ABANDONED;

    /**
     * Timeout in seconds before an abandoned connection can be removed. [300]
     */
    private Long removeAbandonedTimeout = DEFAULT_REMOVE_ABANDONED_TIMEOUT;

    /**
     * The indication of whether objects will be validated before being borrowed from the pool. If the object fails to
     * validate, it will be dropped from the pool, and we will attempt to borrow another. [true]
     * 
     * <p>
     * NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string.
     */
    private Boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;

    /**
     * The indication of whether objects will be validated before being returned to the pool. [false]
     * 
     * <p>
     * NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string.
     */
    private Boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

    /**
     * The indication of whether objects will be validated by the idle object evictor (if any). If an object fails to
     * validate, it will be dropped from the pool. [false]
     * 
     * <p>
     * NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string.
     */
    private Boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

    /**
     * The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle
     * object evictor thread will be run. [-1]
     */
    private Long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS;

    /**
     * Set to true if you wish the ProxyConnection class to use String.equals instead of == when comparing method names.
     * This property does not apply to added interceptors as those are configured individually. The default value is
     * false.
     */
    private Boolean useEquals = DEFAULT_USE_EQUALS;

    /**
     * avoid excess validation, only run validation at most at this frequency - time in milliseconds. If a connection is
     * due for validation, but has been validated previously within this interval, it will not be validated again. The
     * default value is 30000 (30 seconds).
     */
    private Long validationInterval = DEFAULT_VALIDATION_INTERVAL;

    /**
     * The SQL query that will be used to validate connections from this pool before returning them to the caller. If
     * specified, this query MUST be an SQL SELECT statement that returns at least one row.
     */
    private String validationQuery = "SELECT 2+2";

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TomcatConnectionPool)) {
            return false;
        }
        TomcatConnectionPool tomcatConnectionPool = (TomcatConnectionPool) obj;
        return ObjectUtils.nullSafeEquals(this.getFairQueue(), tomcatConnectionPool.getFairQueue())
            && ObjectUtils.nullSafeEquals(this.getInitSQL(), tomcatConnectionPool.getInitSQL())
            && ObjectUtils.nullSafeEquals(this.getJdbcInterceptors(), tomcatConnectionPool.getJdbcInterceptors())
            && ObjectUtils.nullSafeEquals(this.getJmxEnabled(), tomcatConnectionPool.getJmxEnabled())
            && ObjectUtils.nullSafeEquals(this.getUseEquals(), tomcatConnectionPool.getUseEquals())
            && ObjectUtils.nullSafeEquals(this.getValidationInterval(), tomcatConnectionPool.getValidationInterval())
            && ObjectUtils.nullSafeEquals(this.getDefaultAutoCommit(), tomcatConnectionPool.getDefaultAutoCommit())
            && ObjectUtils.nullSafeEquals(this.getDefaultCatalog(), tomcatConnectionPool.getDefaultCatalog())
            && ObjectUtils.nullSafeEquals(this.getDefaultReadOnly(), tomcatConnectionPool.getDefaultReadOnly())
            && ObjectUtils.nullSafeEquals(this.getDefaultTransactionIsolation(), tomcatConnectionPool.getDefaultTransactionIsolation())
            && ObjectUtils.nullSafeEquals(this.getInitialSize(), tomcatConnectionPool.getInitialSize())
            && ObjectUtils.nullSafeEquals(this.getLogAbandoned(), tomcatConnectionPool.getLogAbandoned())
            && ObjectUtils.nullSafeEquals(this.getMaxActive(), tomcatConnectionPool.getMaxActive())
            && ObjectUtils.nullSafeEquals(this.getMaxIdle(), tomcatConnectionPool.getMaxIdle())
            && ObjectUtils.nullSafeEquals(this.getMaxWait(), tomcatConnectionPool.getMaxWait())
            && ObjectUtils.nullSafeEquals(this.getMinEvictableIdleTimeMillis(), tomcatConnectionPool.getMinEvictableIdleTimeMillis())
            && ObjectUtils.nullSafeEquals(this.getMinIdle(), tomcatConnectionPool.getMinIdle())
            && ObjectUtils.nullSafeEquals(this.getRemoveAbandoned(), tomcatConnectionPool.getRemoveAbandoned())
            && ObjectUtils.nullSafeEquals(this.getRemoveAbandonedTimeout(), tomcatConnectionPool.getRemoveAbandonedTimeout())
            && ObjectUtils.nullSafeEquals(this.getTestOnBorrow(), tomcatConnectionPool.getTestOnBorrow())
            && ObjectUtils.nullSafeEquals(this.getTestOnReturn(), tomcatConnectionPool.getTestOnReturn())
            && ObjectUtils.nullSafeEquals(this.getTestWhileIdle(), tomcatConnectionPool.getTestWhileIdle())
            && ObjectUtils.nullSafeEquals(this.getTimeBetweenEvictionRunsMillis(), tomcatConnectionPool.getTimeBetweenEvictionRunsMillis())
            && ObjectUtils.nullSafeEquals(this.getValidationQuery(), tomcatConnectionPool.getValidationQuery());
    }

    @XmlAttribute(name = "default-auto-commit")
    public Boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    @XmlAttribute(name = "default-catalog")
    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    @XmlAttribute(name = "default-read-only")
    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    @XmlAttribute(name = "default-transaction-isolation")
    public String getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    @XmlAttribute(name = "fair-queue")
    public Boolean getFairQueue() {
        return fairQueue;
    }

    @XmlAttribute(name = "initial-size")
    public Long getInitialSize() {
        return initialSize;
    }

    @XmlAttribute(name = "init-sql")
    public String getInitSQL() {
        return initSQL;
    }

    @XmlAttribute(name = "jdbc-interceptors")
    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    @XmlAttribute(name = "jmx-enabled")
    public Boolean getJmxEnabled() {
        return jmxEnabled;
    }

    @XmlAttribute(name = "log-abandoned")
    public Boolean getLogAbandoned() {
        return logAbandoned;
    }

    @XmlAttribute(name = "max-active")
    public Long getMaxActive() {
        return maxActive;
    }

    @XmlAttribute(name = "max-idle")
    public Long getMaxIdle() {
        return maxIdle;
    }

    @XmlAttribute(name = "max-wait")
    public Long getMaxWait() {
        return maxWait;
    }

    @XmlAttribute(name = "min-evictable-idle-time-millis")
    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    @XmlAttribute(name = "min-idle")
    public Long getMinIdle() {
        return minIdle;
    }

    @XmlAttribute(name = "remove-abandoned")
    public Boolean getRemoveAbandoned() {
        return removeAbandoned;
    }

    @XmlAttribute(name = "remove-abandoned-timeout")
    public Long getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    @XmlAttribute(name = "test-on-borrow")
    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @XmlAttribute(name = "test-on-return")
    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    @XmlAttribute(name = "test-while-idle")
    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    @XmlAttribute(name = "time-between-eviction-runs-millis")
    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    @XmlAttribute(name = "use-equals")
    public Boolean getUseEquals() {
        return useEquals;
    }

    @XmlAttribute(name = "validation-interval")
    public Long getValidationInterval() {
        return validationInterval;
    }

    @XmlAttribute(name = "validation-query")
    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.fairQueue) * 29 + ObjectUtils.nullSafeHashCode(this.initSQL) * 29
            + ObjectUtils.nullSafeHashCode(this.jdbcInterceptors) * 29 + ObjectUtils.nullSafeHashCode(this.jmxEnabled) * 29
            + ObjectUtils.nullSafeHashCode(this.useEquals) * 29 + ObjectUtils.nullSafeHashCode(this.validationInterval) * 29

            + ObjectUtils.nullSafeHashCode(this.defaultAutoCommit) * 29 + ObjectUtils.nullSafeHashCode(this.defaultCatalog) * 29
            + ObjectUtils.nullSafeHashCode(this.defaultReadOnly) * 29 + ObjectUtils.nullSafeHashCode(this.defaultTransactionIsolation) * 29
            + ObjectUtils.nullSafeHashCode(this.initialSize) * 29 + ObjectUtils.nullSafeHashCode(this.logAbandoned) * 29
            + ObjectUtils.nullSafeHashCode(this.maxActive) * 29 + ObjectUtils.nullSafeHashCode(this.maxIdle) * 29

            + ObjectUtils.nullSafeHashCode(this.maxWait) * 29 + ObjectUtils.nullSafeHashCode(this.minEvictableIdleTimeMillis) * 29
            + ObjectUtils.nullSafeHashCode(this.minIdle) * 29

            + ObjectUtils.nullSafeHashCode(this.removeAbandoned) * 29 + ObjectUtils.nullSafeHashCode(this.removeAbandonedTimeout) * 29
            + ObjectUtils.nullSafeHashCode(this.testOnBorrow) * 29 + ObjectUtils.nullSafeHashCode(this.testOnReturn) * 29
            + ObjectUtils.nullSafeHashCode(this.testWhileIdle) * 29 + ObjectUtils.nullSafeHashCode(this.timeBetweenEvictionRunsMillis) * 29
            + ObjectUtils.nullSafeHashCode(this.validationQuery) * 29;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultTransactionIsolation(String defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setFairQueue(Boolean fairQueue) {
        this.fairQueue = fairQueue;
    }

    public void setInitialSize(Long initialSize) {
        this.initialSize = initialSize;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
    }

    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public void setJmxEnabled(Boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public void setLogAbandoned(Boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public void setMaxActive(Long maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(Long maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxWait(Long maxWait) {
        this.maxWait = maxWait;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setMinIdle(Long minIdle) {
        this.minIdle = minIdle;
    }

    public void setRemoveAbandoned(Boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public void setRemoveAbandonedTimeout(Long removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setUseEquals(Boolean useEquals) {
        this.useEquals = useEquals;
    }

    public void setValidationInterval(Long validationInterval) {
        this.validationInterval = validationInterval;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        TomcatConnectionPool connectionPool = (TomcatConnectionPool) target;

        if (!errors.hasFieldErrors("validationQuery")) {
            if (connectionPool.getTestOnBorrow() || connectionPool.getTestOnReturn() || connectionPool.getTestWhileIdle()) {
                if (!StringUtils.hasText(connectionPool.getValidationQuery())) {
                    errors.rejectValue("validationQuery", "resource.dataSource.connectionPool.validationQuery.required");
                }
            }
        }
    }

}
