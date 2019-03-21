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

package com.springsource.hq.plugin.tcserver.plugin;

import org.hyperic.hq.product.PluginException;

final class StandardControlCommandConverterFactory implements ControlCommandConverterFactory {

    private final String agentUser;

    public StandardControlCommandConverterFactory() throws PluginException {
        this.agentUser = Utils.getAgentUser();
    }

    public ControlCommandConverter getControlCommandConverter(String instanceUserName) throws PluginException {
        // if running on windows, or the instance user is not found, or if the agent user was not accessible, run
        // as the same user.
        if (Utils.isWindows() || instanceUserName == null || this.agentUser == null) {
            return new IdentityControlCommandConverter();
        } else if (Utils.isRoot(this.agentUser)) {
            if (Utils.isRoot(instanceUserName)) {
                return new IdentityControlCommandConverter();
            } else {
                return new RootAgentControlCommandConverter(instanceUserName);
            }
        } else {
            if (this.agentUser.equals(instanceUserName)) {
                return new IdentityControlCommandConverter();
            } else {
                return new NonRootAgentControlCommandConverter(instanceUserName);
            }
        }
    }
}
