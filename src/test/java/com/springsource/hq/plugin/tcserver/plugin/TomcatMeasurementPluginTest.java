// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import junit.framework.TestCase;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;

/**
 * Test of the {@link TomcatMeasurementPlugin} Uses a local Platform MBeanServer to confirm that values are retrieved
 * from Garbage Collector and Runtime MXBeans for calculation of the Percent CPU Time in Garbage Collection metric
 * 
 * @author Jennifer Hickey
 * @author jkonicki
 * 
 */
public class TomcatMeasurementPluginTest extends TestCase {

    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private TomcatMeasurementPlugin measurementPlugin = new TomcatMeasurementPlugin();

    private String createConnectorServer(final MBeanServer mBeanServer) throws IOException {
        JMXServiceURL address = new JMXServiceURL("service:jmx:rmi://localhost");
        JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(address, null, mBeanServer);
        cntorServer.start();
        return cntorServer.getAddress().toString();
    }

    /**
     * Verifies that the measurement plugin will return a value for Percent CPU Time in Garbage Collection metric
     * 
     * @throws MetricNotFoundException
     * @throws MetricUnreachableException
     * @throws PluginException
     * @throws IOException
     */
    public void testGetPercentGc() throws MetricNotFoundException, MetricUnreachableException, PluginException, IOException {
        final String jmxUrl = createConnectorServer(mBeanServer);
        Metric metric = Metric.parse("tcServer:GC:percentUpTimeSpent:jmx.url=" + jmxUrl);
        MetricValue value = measurementPlugin.getValue(metric);
        assertTrue(value.getValue() >= 0d);
    }

    public void testGetDeadlockCount() throws MetricNotFoundException, MetricUnreachableException, PluginException, IOException {
        final String jmxUrl = createConnectorServer(mBeanServer);
        Metric metric = Metric.parse("tcServer:Deadlocks:deadlockedThreadCount:jmx.url=" + jmxUrl);
        MetricValue value = measurementPlugin.getValue(metric);
        assertTrue(value.getValue() >= 0d);
    }

}
