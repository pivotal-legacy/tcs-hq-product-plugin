// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;

/**
 * Unit tests for {@link AjpConnector}
 * 
 * @author Scott Andrews
 */
public class AjpConnectorTests {

    private AjpConnector ajpConnector;

    @Before
    public void setup() {
        ajpConnector = new AjpConnector();
    }

    @Test
    public void testConnector() {
        assertTrue(ajpConnector instanceof Connector);
    }

    @Test
    public void testValidator() {
        assertTrue(ajpConnector instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(ajpConnector.supports(ajpConnector.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ajpConnector, "ajpConnector");
        ajpConnector.validate(ajpConnector, errors);
        assertEquals(1, errors.getFieldErrorCount());
        assertEquals("service.connector.port.required", errors.getFieldError("port").getCode());
    }

}
