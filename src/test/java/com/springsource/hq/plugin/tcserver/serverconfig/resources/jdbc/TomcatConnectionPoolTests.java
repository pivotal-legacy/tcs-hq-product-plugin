// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.ConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;

/**
 * Unit tests for {@link TomcatConnectionPool}
 * 
 * @author Scott Andrews
 */
public class TomcatConnectionPoolTests {

    private TomcatConnectionPool tomcatConnectionPool;

    @Before
    public void setup() {
        tomcatConnectionPool = new TomcatConnectionPool();
    }

    @Test
    public void testConnectionPool() {
        assertTrue(tomcatConnectionPool instanceof ConnectionPool);
    }

    @Test
    public void testValidator() {
        assertTrue(tomcatConnectionPool instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(tomcatConnectionPool.supports(tomcatConnectionPool.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(tomcatConnectionPool, "staticDefaults");
        tomcatConnectionPool.validate(tomcatConnectionPool, errors);
        assertFalse(errors.hasErrors());
    }

}
