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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

/**
 * Unit tests for {@link JspDefaults}
 * 
 * @author Scott Andrews
 */
public class JspDefaultsTests {

    private JspDefaults jspDefaults;

    @Before
    public void setup() {
        jspDefaults = new JspDefaults();
    }

    @Test
    public void testHierarchical() {
        assertTrue(jspDefaults instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(jspDefaults.parent());
    }

    @Test
    public void testParent_reflective() {
        ServerDefaults serverDefaults = new ServerDefaults();
        jspDefaults.setParent(serverDefaults);
        assertSame(serverDefaults, jspDefaults.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        jspDefaults.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(jspDefaults instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(jspDefaults.supports(jspDefaults.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(jspDefaults, "jspDefaults");
        jspDefaults.validate(jspDefaults, errors);
        assertFalse(errors.hasErrors());
    }

}
