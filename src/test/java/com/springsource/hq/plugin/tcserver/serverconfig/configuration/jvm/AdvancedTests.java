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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Advanced;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Unit tests for {@link Advanced}
 * 
 * @author Scott Andrews
 */
public class AdvancedTests {

    private Advanced advanced;

    @Before
    public void setup() {
        advanced = new Advanced();
    }

    @Test
    public void testHierarchical() {
        assertTrue(advanced instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(advanced.parent());
    }

    @Test
    public void testParent_reflective() {
        JvmOptions jvmOptions = new JvmOptions();
        advanced.setParent(jvmOptions);
        assertSame(jvmOptions, advanced.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        advanced.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(advanced instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(advanced.supports(advanced.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(advanced, "advanced");
        advanced.validate(advanced, errors);
        assertFalse(errors.hasErrors());
    }

}
