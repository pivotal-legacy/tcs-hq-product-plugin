// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;

/**
 * Unit tests for {@link DataSource}
 * 
 * @author Scott Andrews
 */
public class DataSourceTests {

    private DataSource dataSource;

    @Before
    public void setup() {
        dataSource = new MockDataSource();
    }

    @Test
    public void testHierarchical() {
        assertTrue(dataSource instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(dataSource.parent());
    }

    @Test
    public void testParent_reflective() {
        Settings settings = new Settings();
        dataSource.setParent(settings);
        assertSame(settings, dataSource.parent());
    }

    @Test
    public void testIdentity() {
        assertTrue(dataSource instanceof Identity);
    }

    @Test
    public void testGetId_null() {
        assertNull(dataSource.getId());
    }

    @Test
    public void testGetId_reflective() {
        String id = "testId";
        dataSource.setId(id);
        assertEquals(id, dataSource.getId());
    }

    @Test
    public void testGetHumanId() {
        General general = new General();
        general.setJndiName("jndi/employee");
        dataSource.setGeneral(general);
        assertEquals("jndi/employee", dataSource.getHumanId());
    }

}
