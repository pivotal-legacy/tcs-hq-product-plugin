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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public interface EnvironmentRepository {

    /**
     * The will write out the environment file from scratch. This should be used when the file does not exist already.
     * 
     * @param config The configuration response collection of properties
     * @param environment The environment object.
     * @throws PluginException
     */
    void save(ConfigResponse config, Environment environment) throws PluginException;

    /**
     * This is called if the backup file exists. This will preserve the other options the user has added to the file.
     * 
     * @param backupFileName The backup file to read in the file data
     * @param config The configuration response collection of properties
     * @param environment The environment object.
     * @throws PluginException
     */
    void save(final String backupFileName, ConfigResponse config, Environment environment) throws PluginException;
}
