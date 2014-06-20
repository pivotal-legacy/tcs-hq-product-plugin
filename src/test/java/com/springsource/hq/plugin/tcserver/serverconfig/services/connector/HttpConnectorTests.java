// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;

/**
 * Unit tests for {@link HttpConnector}
 * 
 * @author Scott Andrews
 */
public class HttpConnectorTests {

    private HttpConnector httpConnector;

    @Before
    public void setup() {
        httpConnector = new HttpConnector();
    }

    @Test
    public void testConnector() {
        assertTrue(httpConnector instanceof Connector);
    }

    @Test
    public void testValidator() {
        assertTrue(httpConnector instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(httpConnector.supports(httpConnector.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(httpConnector, "httpConnector");
        httpConnector.validate(httpConnector, errors);
        assertEquals(1, errors.getFieldErrorCount());
        assertEquals("service.connector.port.required", errors.getFieldError("port").getCode());
    }

}
