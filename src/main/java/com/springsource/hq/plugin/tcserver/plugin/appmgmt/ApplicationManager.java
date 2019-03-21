/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;

/**
 * Handles all functions related to management of web applications in tc Runtime
 * 
 * @author jkonicki
 * 
 */
public interface ApplicationManager {

    Object deploy(ConfigResponse config) throws PluginException;

    Map<String, List<String>> getServiceHostMappings(ConfigResponse config) throws PluginException;

    Set<Application> list(ConfigResponse config) throws PluginException;

    Map<String, Object> reload(ConfigResponse config) throws PluginException;

    Map<String, Object> start(ConfigResponse config) throws PluginException;

    Map<String, Object> stop(ConfigResponse config) throws PluginException;

    Map<String, Object> undeploy(ConfigResponse config) throws PluginException;

    void removeTemporaryWarFile(ConfigResponse config);

    String getAppBase(ConfigResponse config) throws PluginException;
}
