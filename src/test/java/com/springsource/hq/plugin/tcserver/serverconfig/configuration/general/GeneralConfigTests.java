// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.general;

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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;

/**
 * Unit tests for {@link GeneralConfig}
 * 
 * @author Scott Andrews
 */
public class GeneralConfigTests {

    private GeneralConfig generalConfig;

    @Before
    public void setup() {
        generalConfig = new GeneralConfig();
    }

    @Test
    public void testHierarchical() {
        assertTrue(generalConfig instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(generalConfig.parent());
    }

    @Test
    public void testParent_reflective() {
        Configuration configuration = new Configuration();
        generalConfig.setParent(configuration);
        assertSame(configuration, generalConfig.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        generalConfig.applyParentToChildren();
        assertSame(generalConfig, generalConfig.getServerProperties().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(generalConfig instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(generalConfig.supports(generalConfig.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(generalConfig, "generalConfig");
        generalConfig.validate(generalConfig, errors);
        assertFalse(errors.hasErrors());
    }

}
