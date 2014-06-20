/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.util.control

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future 
import org.hyperic.hq.appdef.server.session.AppdefResource;
import org.hyperic.hq.authz.server.session.AuthzSubject 
import org.hyperic.hq.authz.server.session.Resource 
import org.hyperic.hq.authz.shared.PermissionException 
import org.hyperic.hq.context.Bootstrap;
import org.hyperic.hq.control.ControlActionResult 
import org.hyperic.hq.control.shared.ControlConstants;
import org.hyperic.hq.control.shared.ControlManager 
import org.hyperic.hq.livedata.shared.LiveDataResult
import org.hyperic.util.config.ConfigResponse

class ControlActionHelper {
    
    private static final int JMX_TIMEOUT = 30000;
    
    private static final int DEFAULT_TIMEOUT = 60000
    
    private static final String CONTROL_ACTION_STOP = "stop";
    
    private static final String CONTROL_ACTION_START = "start";
    
    private static final String CONTROL_ACTION_RESTART = "restart";
    
    private static final long JMX_CHECK_INTERVAL = 1000;
    
    private final ControlManager controlManager
    
    ControlActionHelper() {
        this(Bootstrap.getBean(ControlManager.class))
    }
       
    ControlActionHelper(ControlManager controlManager) {
        this.controlManager = controlManager
    }
    
    void stopServer(AuthzSubject subject, Resource resource) throws PermissionException, ControlActionFailedException {
        performControlAction(CONTROL_ACTION_STOP, subject, resource)
    }
    
    void startServer(AuthzSubject subject, Resource resource) throws PermissionException, ControlActionFailedException {
        performControlAction(CONTROL_ACTION_START, subject, resource)
        ensureJmxConnection(subject, resource)
    }
    
    void restartServer(AuthzSubject subject, Resource resource, boolean waitForJmxConnection) throws PermissionException, ControlActionFailedException {
        performControlAction(CONTROL_ACTION_RESTART, subject, resource)

        if (waitForJmxConnection) {
            ensureJmxConnection(subject, resource)
        }
    }

    private void performControlAction(String controlAction, AuthzSubject subject, Resource resource) {        
        Future<ControlActionResult> actionFuture = this.controlManager.doAction(subject, resource.entityId, controlAction, null, DEFAULT_TIMEOUT)

        try {
            ControlActionResult result = actionFuture.get()
            String status = result.getStatus()
            if (ControlConstants.STATUS_FAILED.equals(status)) {
                throw new ControlActionFailedException(result.getMessage())
            }
        } catch (ExecutionException ee) {
            Throwable cause = ee.cause
            throw new ControlActionFailedException(cause.message, cause)
        } catch (InterruptedException ie) {
            throw new ControlActionFailedException(ie.message, ee)
        }
    }

    private void ensureJmxConnection(AuthzSubject subject, Resource resource) {
        long startTime = System.currentTimeMillis()
        
        boolean connected = false
        while (!connected) {
            if (System.currentTimeMillis() - startTime > JMX_TIMEOUT) {
                throw new ControlActionFailedException("Unable to make JMX connection to server");
            }
            
            LiveDataResult liveDataResult = resource.getLiveData(subject, "getServiceHostMappings", new ConfigResponse())
            if (!liveDataResult.hasError()) {
                connected = true
            } else {
                try {
                    Thread.sleep(JMX_CHECK_INTERVAL)
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt()
                    throw new ControlActionFailedException("Interrupted while waiting for JMX connection to be available")
                }
            }
        }
    }
}
