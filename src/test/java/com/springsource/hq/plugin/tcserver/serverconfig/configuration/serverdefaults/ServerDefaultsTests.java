// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults;

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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

/**
 * Unit tests for {@link ServerDefaults}
 * 
 * @author Scott Andrews
 */
public class ServerDefaultsTests {

    private ServerDefaults serverDefaults;

    @Before
    public void setup() {
        serverDefaults = new ServerDefaults();
    }

    @Test
    public void testHierarchical() {
        assertTrue(serverDefaults instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(serverDefaults.parent());
    }

    @Test
    public void testParent_reflective() {
        Configuration configuration = new Configuration();
        serverDefaults.setParent(configuration);
        assertSame(configuration, serverDefaults.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        serverDefaults.applyParentToChildren();
        assertSame(serverDefaults, serverDefaults.getJspDefaults().parent());
        assertSame(serverDefaults, serverDefaults.getStaticDefaults().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(serverDefaults instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(serverDefaults.supports(serverDefaults.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(serverDefaults, "serverDefaults");
        serverDefaults.validate(serverDefaults, errors);
        assertFalse(errors.hasErrors());
    }

}
