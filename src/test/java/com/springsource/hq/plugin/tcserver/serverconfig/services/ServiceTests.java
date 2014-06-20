// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;

/**
 * Unit tests for {@link Service}
 * 
 * @author Scott Andrews
 */
public class ServiceTests {

    private Service service;

    @Before
    public void setup() {
        service = new Service();
    }

    @Test
    public void testHierarchical() {
        assertTrue(service instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(service.parent());
    }

    @Test
    public void testParent_reflective() {
        Settings settings = new Settings();
        service.setParent(settings);
        assertSame(settings, service.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        Connector connector = new HttpConnector();
        service.getConnectors().add(connector);

        service.applyParentToChildren();

        assertSame(service, service.getEngine().parent());
        for (Connector c : service.getConnectors()) {
            assertSame(service, c.parent());
        }
    }

    @Test
    public void testIdentity() {
        assertTrue(service instanceof Identity);
    }

    @Test
    public void testGetId_null() {
        assertNull(service.getId());
    }

    @Test
    public void testGetId_reflective() {
        String id = "testId";
        service.setId(id);
        assertEquals(id, service.getId());
    }

    @Test
    public void testBackSlashes() {
        service.setName("Service\\name");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(service, "service");
        service.validate(service, errors);
        assertEquals("Service\\name", service.getHumanId());
    }

    @Test
    public void testFowardSlashes() {
        service.setName("Service/name");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(service, "service");
        service.validate(service, errors);
        assertEquals("Service/name", service.getHumanId());
    }

    @Test
    public void testValidator() {
        assertTrue(service instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(service.supports(service.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(service, "service");
        service.validate(service, errors);
        assertEquals(3, errors.getFieldErrorCount());
        assertEquals("service.name.required", errors.getFieldError("name").getCode());
        assertEquals("service.engine.name.required", errors.getFieldError("engine.name").getCode());
        assertEquals("service.engine.defaultHost.required", errors.getFieldError("engine.defaultHost").getCode());
    }

    @Test
    public void validateWithInvalidConnector() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(new HttpConnector());
        service.setConnectors(connectors);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(service, "service");
        service.validate(service, errors);

        assertEquals("service.connector.port.required", errors.getFieldError("connectors[0].port").getCode());
    }

}
