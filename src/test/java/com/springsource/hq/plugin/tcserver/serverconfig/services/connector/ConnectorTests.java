// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Unit tests for {@link Connector}
 * 
 * @author Scott Andrews
 */
public class ConnectorTests {

    private Connector connector;

    @Before
    public void setup() {
        connector = new MockConnector();
    }

    @Test
    public void testHierarchical() {
        assertTrue(connector instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(connector.parent());
    }

    @Test
    public void testParent_reflective() {
        Service service = new Service();
        connector.setParent(service);
        assertSame(service, connector.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        connector.applyParentToChildren();
    }

    @Test
    public void testIdentity() {
        assertTrue(connector instanceof Identity);
    }

    @Test
    public void testGetId_null() {
        assertNull(connector.getId());
    }

    @Test
    public void testGetId_reflective() {
        String id = "testId";
        connector.setId(id);
        assertEquals(id, connector.getId());
    }

    @Test
    public void testGetHumanId() {
        connector.setAddress("the/address");
        connector.setPort(8080L);
        assertEquals("theaddress:8080", connector.getHumanId());
    }

    @Test
    public void testGetHumanId_noAddress() {
        connector.setPort(8080L);
        assertEquals(":8080", connector.getHumanId());
    }

    @Test
    public void validateWithTwoConnectorsWithNullPorts() {
        connector.setScheme("http");

        Service service = new MockService();

        MockSettings settings = new MockSettings();
        settings.getServices().add(service);
        service.setParent(settings);

        service.getConnectors().add(connector);
        connector.setParent(service);

        Connector connector2 = new MockConnector();
        connector2.setScheme("https");
        service.getConnectors().add(connector2);
        connector.setParent(service);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(connector, "connector");
        connector.validate(connector, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals(1, errors.getFieldErrorCount("port"));
    }

    private class MockConnector extends Connector {

    }

    private class MockService extends Service {

    }

    private class MockSettings extends Settings {

    }

}
