// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;

/**
 * Unit tests for {@link Connection}
 * 
 * @author Scott Andrews
 */
public class ConnectionTests {

    private Connection connection;

    @Before
    public void setup() {
        connection = new Connection();
    }

    @Test
    public void testHierarchical() {
        assertTrue(connection instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(connection.parent());
    }

    @Test
    public void testParent_reflective() {
        DataSource dataSource = new MockDataSource();
        connection.setParent(dataSource);
        assertSame(dataSource, connection.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        connection.applyParentToChildren();
    }

}
