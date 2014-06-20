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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.WebApplicationLogger;

/**
 * Unit tests for {@link WebApplicationLogger}
 * 
 * @author Scott Andrews
 */
public class WebApplicationLoggerTests {

    private WebApplicationLogger webApplicationLogger;

    @Before
    public void setup() {
        webApplicationLogger = new WebApplicationLogger();
    }

    @Test
    public void testHierarchical() {
        assertTrue(webApplicationLogger instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(webApplicationLogger.parent());
    }

    @Test
    public void testParent_reflective() {
        ContextContainer contextContrainer = new ContextContainer();
        webApplicationLogger.setParent(contextContrainer);
        assertSame(contextContrainer, webApplicationLogger.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        webApplicationLogger.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(webApplicationLogger instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(webApplicationLogger.supports(webApplicationLogger.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(webApplicationLogger, "webApplicationLogger");
        webApplicationLogger.validate(webApplicationLogger, errors);
        assertFalse(errors.hasErrors());
    }

}
