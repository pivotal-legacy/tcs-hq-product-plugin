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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.GarbageCollection;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Unit tests for {@link GarbageCollection}
 * 
 * @author Scott Andrews
 */
public class GarbageCollectionTests {

    private GarbageCollection garbageCollection;

    @Before
    public void setup() {
        garbageCollection = new GarbageCollection();
    }

    @Test
    public void testHierarchical() {
        assertTrue(garbageCollection instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(garbageCollection.parent());
    }

    @Test
    public void testParent_reflective() {
        JvmOptions jvmOptions = new JvmOptions();
        garbageCollection.setParent(jvmOptions);
        assertSame(jvmOptions, garbageCollection.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        garbageCollection.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(garbageCollection instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(garbageCollection.supports(garbageCollection.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(garbageCollection, "garbageCollection");
        garbageCollection.validate(garbageCollection, errors);
        assertFalse(errors.hasErrors());
    }

}
