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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Memory;

/**
 * Unit tests for {@link Memory}
 * 
 * @author Scott Andrews
 */
public class MemoryTests {

    private Memory memory;

    @Before
    public void setup() {
        memory = new Memory();
    }

    @Test
    public void testHierarchical() {
        assertTrue(memory instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(memory.parent());
    }

    @Test
    public void testParent_reflective() {
        JvmOptions jvmOptions = new JvmOptions();
        memory.setParent(jvmOptions);
        assertSame(jvmOptions, memory.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        memory.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(memory instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(memory.supports(memory.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(memory, "memory");
        memory.validate(memory, errors);
        assertFalse(errors.hasErrors());
    }

}
