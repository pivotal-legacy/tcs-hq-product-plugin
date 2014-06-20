// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;

/**
 * Unit tests for {@link Host}
 * 
 * @author Scott Andrews
 */
public class HostTests {

    private Host host;

    @Before
    public void setup() {
        host = new Host();
    }

    @Test
    public void testHierarchical() {
        assertTrue(host instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(host.parent());
    }

    @Test
    public void testParent_reflective() {
        Engine engine = new Engine();
        host.setParent(engine);
        assertSame(engine, host.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        host.applyParentToChildren();
    }

    @Test
    public void testIdentity() {
        assertTrue(host instanceof Identity);
    }

    @Test
    public void testGetId_null() {
        assertNull(host.getId());
    }

    @Test
    public void testGetId_reflective() {
        String id = "testId";
        host.setId(id);
        assertEquals(id, host.getId());
    }

    @Test
    public void testGetHumanId() {
        host.setName("host/name");
        assertEquals("hostname", host.getHumanId());
    }

    @Test
    public void testValidator() {
        assertTrue(host instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(host.supports(host.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(host, "host");
        host.validate(host, errors);
        assertEquals(2, errors.getFieldErrorCount());
        assertEquals("service.engine.host.name.required", errors.getFieldError("name").getCode());
        assertEquals("service.engine.host.appBase.required", errors.getFieldError("appBase").getCode());
    }

}
