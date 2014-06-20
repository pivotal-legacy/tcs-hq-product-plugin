// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * Unit tests for {@link DbcpConnectionPool}
 * 
 * @author Scott Andrews
 */
public class DbcpConnectionPoolTests {

    private DbcpConnectionPool dbcpConnectionPool;

    @Before
    public void setup() {
        dbcpConnectionPool = new DbcpConnectionPool();
    }

    @Test
    public void testConnectionPool() {
        assertTrue(dbcpConnectionPool instanceof ConnectionPool);
    }

    @Test
    public void testValidator() {
        assertTrue(dbcpConnectionPool instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(dbcpConnectionPool.supports(dbcpConnectionPool.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dbcpConnectionPool, "dbcpConnectionPool");
        dbcpConnectionPool.validate(dbcpConnectionPool, errors);
        assertEquals(0, errors.getFieldErrorCount());
        // assertEquals("resource.dataSource.connectionPool.validationQuery.required",
        // errors.getFieldError("validationQuery").getCode());
    }

}
