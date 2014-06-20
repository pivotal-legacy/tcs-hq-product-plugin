// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Unit tests for {@link Configuration}
 * 
 * @author Scott Andrews
 */
public class ConfigurationTests {

    private Configuration configuration;

    @Before
    public void setup() {
        configuration = new Configuration();
    }

    @Test
    public void testHierarchical() {
        assertTrue(configuration instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(configuration.parent());
    }

    @Test
    public void testParent_reflective() {
        Settings settings = new Settings();
        configuration.setParent(settings);
        assertSame(settings, configuration.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        configuration.applyParentToChildren();
        assertSame(configuration, configuration.getGeneralConfig().parent());
        assertSame(configuration, configuration.getEnvironment().parent());
        assertSame(configuration, configuration.getContextContainer().parent());
        assertSame(configuration, configuration.getServerDefaults().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(configuration instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(configuration.supports(configuration.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(configuration, "configuration");
        configuration.validate(configuration, errors);
        assertFalse(errors.hasErrors());
    }

}
