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
 * DBCP based connection pool settings. (https://commons.apache.org/dbcp/configuration.html)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "dbcp-connection-pool")
public class DbcpConnectionPool extends ConnectionPool {

    private static final boolean DEFAULT_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED = false;

    private static final boolean DEFAULT_AUTO_COMMIT = true;

    private static final long DEFAULT_INITIAL_SIZE = 0l;

    private static final boolean DEFAULT_LOG_ABANDONED = false;

    private static final long DEFAULT_MAX_ACTIVE = 8l;

    private static final long DEFAULT_MAX_IDLE = 8l;

    private static final long DEFAULT_MAX_OPEN_PREPARED_STMTS = 0l;

    private static final long DEFAULT_MAX_WAIT = -1l;

    private static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME = 1000 * 60 * 30l;

    private static final long DEFAULT_MIN_IDLE = 0l;

    private static final long DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3l;

    private static final boolean DEFAULT_POOL_PREPARED_STMTS = false;

    private static final boolean DEFAULT_REMOVE_ABANDONED = false;

    private static final long DEFAULT_REMOVE_ABANDONED_TIMEOUT = 300l;

    private static final boolean DEFAULT_TEST_ON_BORROW = true;

    private static final boolean DEFAULT_TEST_ON_RETURN = false;

    private static final boolean DEFAULT_TEST_WHILE_IDLE = false;

    private static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS = -1l;

    /**
     * Controls if the PoolGuard allows access to the underlying connection. [false]
     */
    private Boolean accessToUnderlyingConnectionAllowed = DEFAULT_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED;

    /**
     * The default auto-commit state of connections created by this pool. [true]
     */
    private Boolean defaultAutoCommit = DEFAULT_AUTO_COMMIT;

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
     * The initial number of connections that are created when the pool is started. Since: 1.2 [0]
     */
    private Long initialSize = DEFAULT_INITIAL_SIZE;

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
     * The maximum number of open statements that can be allocated from the statement pool at the same time, or zero for
     * no limit. [unlimited]
     */
    private Long maxOpenPreparedStatements = DEFAULT_MAX_OPEN_PREPARED_STMTS;

    /**
     * The maximum number of milliseconds that the pool will wait (when there are no available connections) for a
     * connection to be returned before throwing an exception, or -1 to wait indefinitely. [indefinitely]
     */
    private Long maxWait = DEFAULT_MAX_WAIT;

    /**
     * The minimum amount of time an object may sit idle in the pool before it is eligable for eviction by the idle
     * object evictor (if any). [1000 * 60 * 30]
     */
    private Long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME;

    /**
     * The minimum number of connections that can remain idle in the pool, without extra ones being created, or zero to
     * create none. [0]
     */
    private Long minIdle = DEFAULT_MIN_IDLE;

    /**
     * The number of objects to examine during each run of the idle object evictor thread (if any). [3]
     */
    private Long numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    /**
     * Enable prepared statement pooling for this pool. [false]
     */
    private Boolean poolPreparedStatements = DEFAULT_POOL_PREPARED_STMTS;

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
     * The SQL query that will be used to validate connections from this pool before returning them to the caller. If
     * specified, this query MUST be an SQL SELECT statement that returns at least one row.
     */
    private String validationQuery = "SELECT 2+2";

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DbcpConnectionPool)) {
            return false;
        }
        DbcpConnectionPool connectionPool = (DbcpConnectionPool) obj;
        return ObjectUtils.nullSafeEquals(this.getAccessToUnderlyingConnectionAllowed(), connectionPool.getAccessToUnderlyingConnectionAllowed())
            && ObjectUtils.nullSafeEquals(this.getDefaultAutoCommit(), connectionPool.getDefaultAutoCommit())
            && ObjectUtils.nullSafeEquals(this.getDefaultCatalog(), connectionPool.getDefaultCatalog())
            && ObjectUtils.nullSafeEquals(this.getDefaultReadOnly(), connectionPool.getDefaultReadOnly())
            && ObjectUtils.nullSafeEquals(this.getDefaultTransactionIsolation(), connectionPool.getDefaultTransactionIsolation())
            && ObjectUtils.nullSafeEquals(this.getInitialSize(), connectionPool.getInitialSize())
            && ObjectUtils.nullSafeEquals(this.getLogAbandoned(), connectionPool.getLogAbandoned())
            && ObjectUtils.nullSafeEquals(this.getMaxActive(), connectionPool.getMaxActive())
            && ObjectUtils.nullSafeEquals(this.getMaxIdle(), connectionPool.getMaxIdle())
            && ObjectUtils.nullSafeEquals(this.getMaxOpenPreparedStatements(), connectionPool.getMaxOpenPreparedStatements())
            && ObjectUtils.nullSafeEquals(this.getMaxWait(), connectionPool.getMaxWait())
            && ObjectUtils.nullSafeEquals(this.getMinEvictableIdleTimeMillis(), connectionPool.getMinEvictableIdleTimeMillis())
            && ObjectUtils.nullSafeEquals(this.getMinIdle(), connectionPool.getMinIdle())
            && ObjectUtils.nullSafeEquals(this.getNumTestsPerEvictionRun(), connectionPool.getNumTestsPerEvictionRun())
            && ObjectUtils.nullSafeEquals(this.getPoolPreparedStatements(), connectionPool.getPoolPreparedStatements())
            && ObjectUtils.nullSafeEquals(this.getRemoveAbandoned(), connectionPool.getRemoveAbandoned())
            && ObjectUtils.nullSafeEquals(this.getRemoveAbandonedTimeout(), connectionPool.getRemoveAbandonedTimeout())
            && ObjectUtils.nullSafeEquals(this.getTestOnBorrow(), connectionPool.getTestOnBorrow())
            && ObjectUtils.nullSafeEquals(this.getTestOnReturn(), connectionPool.getTestOnReturn())
            && ObjectUtils.nullSafeEquals(this.getTestWhileIdle(), connectionPool.getTestWhileIdle())
            && ObjectUtils.nullSafeEquals(this.getTimeBetweenEvictionRunsMillis(), connectionPool.getTimeBetweenEvictionRunsMillis())
            && ObjectUtils.nullSafeEquals(this.getValidationQuery(), connectionPool.getValidationQuery());
    }

    @XmlAttribute(name = "access-to-underlying-connection-allowed")
    public Boolean getAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
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

    @XmlAttribute(name = "initial-size")
    public Long getInitialSize() {
        return initialSize;
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

    @XmlAttribute(name = "max-open-prepared-statements")
    public Long getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
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

    @XmlAttribute(name = "num-tests-per-eviction-run")
    public Long getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    @XmlAttribute(name = "pool-prepared-statements")
    public Boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
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

    @XmlAttribute(name = "validation-query")
    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.accessToUnderlyingConnectionAllowed) * 29 + ObjectUtils.nullSafeHashCode(this.defaultAutoCommit)
            * 29 + ObjectUtils.nullSafeHashCode(this.defaultCatalog) * 29 + ObjectUtils.nullSafeHashCode(this.defaultReadOnly) * 29
            + ObjectUtils.nullSafeHashCode(this.defaultTransactionIsolation) * 29 + ObjectUtils.nullSafeHashCode(this.initialSize) * 29
            + ObjectUtils.nullSafeHashCode(this.logAbandoned) * 29 + ObjectUtils.nullSafeHashCode(this.maxActive) * 29
            + ObjectUtils.nullSafeHashCode(this.maxIdle) * 29 + ObjectUtils.nullSafeHashCode(this.maxOpenPreparedStatements) * 29
            + ObjectUtils.nullSafeHashCode(this.maxWait) * 29 + ObjectUtils.nullSafeHashCode(this.minEvictableIdleTimeMillis) * 29
            + ObjectUtils.nullSafeHashCode(this.minIdle) * 29 + ObjectUtils.nullSafeHashCode(this.numTestsPerEvictionRun) * 29
            + ObjectUtils.nullSafeHashCode(this.poolPreparedStatements) * 29 + ObjectUtils.nullSafeHashCode(this.removeAbandoned) * 29
            + ObjectUtils.nullSafeHashCode(this.removeAbandonedTimeout) * 29 + ObjectUtils.nullSafeHashCode(this.testOnBorrow) * 29
            + ObjectUtils.nullSafeHashCode(this.testOnReturn) * 29 + ObjectUtils.nullSafeHashCode(this.testWhileIdle) * 29
            + ObjectUtils.nullSafeHashCode(this.timeBetweenEvictionRunsMillis) * 29 + ObjectUtils.nullSafeHashCode(this.validationQuery) * 29;
    }

    public void setAccessToUnderlyingConnectionAllowed(Boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
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

    public void setInitialSize(Long initialSize) {
        this.initialSize = initialSize;
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

    public void setMaxOpenPreparedStatements(Long maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
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

    public void setNumTestsPerEvictionRun(Long numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
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

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        DbcpConnectionPool connectionPool = (DbcpConnectionPool) target;

        if (!errors.hasFieldErrors("validationQuery")) {
            if (connectionPool.getTestOnBorrow() || connectionPool.getTestOnReturn() || connectionPool.getTestWhileIdle()) {
                if (!StringUtils.hasText(connectionPool.getValidationQuery())) {
                    errors.rejectValue("validationQuery", "resource.dataSource.connectionPool.validationQuery.required");
                }
            }
        }
    }

}
