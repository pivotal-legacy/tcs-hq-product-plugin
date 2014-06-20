// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;

/**
 * Unit tests for {@link TomcatDataSource}
 * 
 * @author Scott Andrews
 */
public class TomcatDataSourceTests {

    private TomcatDataSource tomcatDataSource;

    @Before
    public void setup() {
        tomcatDataSource = new TomcatDataSource();
        tomcatDataSource.getConnection().setObscuredPassword("");
    }

    @Test
    public void testDataSource() {
        assertTrue(tomcatDataSource instanceof DataSource);
    }

    @Test
    public void testApplyParentToChildren() {
        tomcatDataSource.applyParentToChildren();
        assertSame(tomcatDataSource, tomcatDataSource.getConnection().parent());
        assertSame(tomcatDataSource, tomcatDataSource.getGeneral().parent());
        assertSame(tomcatDataSource, tomcatDataSource.getConnectionPool().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(tomcatDataSource instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(tomcatDataSource.supports(tomcatDataSource.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(tomcatDataSource, "tomcatDataSource");
        tomcatDataSource.validate(tomcatDataSource, errors);
        assertEquals(5, errors.getFieldErrorCount());
        assertEquals("resource.dataSource.general.jndiName.required", errors.getFieldError("general.jndiName").getCode());
        assertEquals("resource.dataSource.connection.username.required", errors.getFieldError("connection.username").getCode());
        assertEquals("resource.dataSource.connection.password.required", errors.getFieldError("connection.obscuredPassword").getCode());
        assertEquals("resource.dataSource.connection.url.required", errors.getFieldError("connection.url").getCode());
        assertEquals("resource.dataSource.connection.driverClassName.required", errors.getFieldError("connection.driverClassName").getCode());
    }

}
