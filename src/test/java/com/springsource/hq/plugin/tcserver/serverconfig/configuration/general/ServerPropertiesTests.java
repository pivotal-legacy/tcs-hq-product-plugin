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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.ServerProperties;

/**
 * Unit tests for {@link ServerProperties}
 * 
 * @author Scott Andrews
 */
public class ServerPropertiesTests {

    private ServerProperties serverProperties;

    @Before
    public void setup() {
        serverProperties = new ServerProperties();
    }

    @Test
    public void testHierarchical() {
        assertTrue(serverProperties instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(serverProperties.parent());
    }

    @Test
    public void testParent_reflective() {
        GeneralConfig generalConfig = new GeneralConfig();
        serverProperties.setParent(generalConfig);
        assertSame(generalConfig, serverProperties.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        serverProperties.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(serverProperties instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(serverProperties.supports(serverProperties.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(serverProperties, "serverProperties");
        serverProperties.validate(serverProperties, errors);
        assertFalse(errors.hasErrors());
    }

}
