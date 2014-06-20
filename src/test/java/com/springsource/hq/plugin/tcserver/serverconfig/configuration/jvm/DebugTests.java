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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Debug;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Unit tests for {@link Debug}
 * 
 * @author Scott Andrews
 */
public class DebugTests {

    private Debug debug;

    @Before
    public void setup() {
        debug = new Debug();
    }

    @Test
    public void testHierarchical() {
        assertTrue(debug instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(debug.parent());
    }

    @Test
    public void testParent_reflective() {
        JvmOptions jvmOptions = new JvmOptions();
        debug.setParent(jvmOptions);
        assertSame(jvmOptions, debug.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        debug.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(debug instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(debug.supports(debug.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(debug, "debug");
        debug.validate(debug, errors);
        assertFalse(errors.hasErrors());
    }

}
