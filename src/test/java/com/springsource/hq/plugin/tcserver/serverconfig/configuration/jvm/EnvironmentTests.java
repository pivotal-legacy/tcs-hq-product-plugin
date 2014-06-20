// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

/**
 * Unit tests for {@link Environment}
 * 
 * @author Scott Andrews
 */
public class EnvironmentTests {

    private Environment environment;

    @Before
    public void setup() {
        environment = new Environment();
    }

    @Test
    public void testHierarchical() {
        assertTrue(environment instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(environment.parent());
    }

    @Test
    public void testParent_reflective() {
        Configuration configuration = new Configuration();
        environment.setParent(configuration);
        assertSame(configuration, environment.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        environment.applyParentToChildren();
        assertSame(environment, environment.getJvmOptions().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(environment instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(environment.supports(environment.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(environment, "environment");
        environment.validate(environment, errors);
        assertFalse(errors.hasErrors());
    }

}
