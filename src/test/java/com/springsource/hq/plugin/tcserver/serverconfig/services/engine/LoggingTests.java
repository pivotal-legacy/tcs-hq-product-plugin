// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Logging;

/**
 * Unit tests for {@link Logging}
 * 
 * @author Scott Andrews
 */
public class LoggingTests {

    private Logging<Engine> logging;

    @Before
    public void setup() {
        logging = new Logging<Engine>();
    }

    @Test
    public void testHierarchical() {
        assertTrue(logging instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(logging.parent());
    }

    @Test
    public void testParent_reflective() {
        Engine engine = new Engine();
        logging.setParent(engine);
        assertSame(engine, logging.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        logging.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(logging instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(logging.supports(logging.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(logging, "logging");
        logging.validate(logging, errors);
        assertFalse(errors.hasErrors());
    }

}
