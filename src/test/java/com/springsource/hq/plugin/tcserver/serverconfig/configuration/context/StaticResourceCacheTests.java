// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.context;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.StaticResourceCache;

/**
 * Unit tests for {@link StaticResourceCache}
 * 
 * @author Scott Andrews
 */
public class StaticResourceCacheTests {

    private StaticResourceCache staticResourceCache;

    @Before
    public void setup() {
        staticResourceCache = new StaticResourceCache();
    }

    @Test
    public void testHierarchical() {
        assertTrue(staticResourceCache instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(staticResourceCache.parent());
    }

    @Test
    public void testParent_reflective() {
        ContextContainer contextContrainer = new ContextContainer();
        staticResourceCache.setParent(contextContrainer);
        assertSame(contextContrainer, staticResourceCache.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        staticResourceCache.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(staticResourceCache instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(staticResourceCache.supports(staticResourceCache.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(staticResourceCache, "staticResourceCache");
        staticResourceCache.validate(staticResourceCache, errors);
        assertFalse(errors.hasErrors());
    }

}
