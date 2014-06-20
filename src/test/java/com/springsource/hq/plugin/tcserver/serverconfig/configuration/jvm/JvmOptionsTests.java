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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Unit tests for {@link JvmOptions}
 * 
 * @author Scott Andrews
 */
public class JvmOptionsTests {

    private JvmOptions jvmOptions;

    @Before
    public void setup() {
        jvmOptions = new JvmOptions();
    }

    @Test
    public void testHierarchical() {
        assertTrue(jvmOptions instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(jvmOptions.parent());
    }

    @Test
    public void testParent_reflective() {
        Environment environment = new Environment();
        jvmOptions.setParent(environment);
        assertSame(environment, jvmOptions.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        jvmOptions.applyParentToChildren();
        assertSame(jvmOptions, jvmOptions.getAdvanced().parent());
        assertSame(jvmOptions, jvmOptions.getDebug().parent());
        assertSame(jvmOptions, jvmOptions.getGarbageCollection().parent());
        assertSame(jvmOptions, jvmOptions.getGeneral().parent());
        assertSame(jvmOptions, jvmOptions.getMemory().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(jvmOptions instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(jvmOptions.supports(jvmOptions.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(jvmOptions, "jvmOptions");
        jvmOptions.validate(jvmOptions, errors);
        assertFalse(errors.hasErrors());
    }

}
