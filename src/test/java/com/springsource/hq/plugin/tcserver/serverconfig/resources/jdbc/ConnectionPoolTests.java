// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Unit tests for {@link ConnectionPool}
 * 
 * @author Scott Andrews
 */
public class ConnectionPoolTests {

    private ConnectionPool connectionPool;

    @Before
    public void setup() {
        connectionPool = new MockConnectionPool();
    }

    @Test
    public void testHierarchical() {
        assertTrue(connectionPool instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(connectionPool.parent());
    }

    @Test
    public void testParent_reflective() {
        DataSource dataSource = new MockDataSource();
        connectionPool.setParent(dataSource);
        assertSame(dataSource, connectionPool.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        connectionPool.applyParentToChildren();
    }

    private class MockConnectionPool extends ConnectionPool {

        public boolean supports(Class<?> clazz) {
            return false;
        }

        public void validate(Object target, Errors errors) {
            // no-op
        }

    }

}
