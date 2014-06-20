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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.General;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Unit tests for {@link General}
 * 
 * @author Scott Andrews
 */
public class GeneralTests {

    private General general;

    @Before
    public void setup() {
        general = new General();
    }

    @Test
    public void testHierarchical() {
        assertTrue(general instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(general.parent());
    }

    @Test
    public void testParent_reflective() {
        JvmOptions jvmOptions = new JvmOptions();
        general.setParent(jvmOptions);
        assertSame(jvmOptions, general.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        general.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(general instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(general.supports(general.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(general, "general");
        general.validate(general, errors);
        assertFalse(errors.hasErrors());
    }

}
