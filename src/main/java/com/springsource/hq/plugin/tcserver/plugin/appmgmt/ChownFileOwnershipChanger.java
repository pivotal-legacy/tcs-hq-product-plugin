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

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.exec.Execute;
import org.hyperic.util.exec.PumpStreamHandler;

final class ChownFileOwnershipChanger implements FileOwnershipChanger {

    private static final String EXECUTABLE_CHOWN = "chown";

    private static final String FORMAT_OWNER_AND_GROUP = "%s:%s";

    private final Log log = LogFactory.getLog(ChownFileOwnershipChanger.class);

    public void changeFileOwnership(File file, String owningUser, String owningGroup) throws PluginException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Execute ex = new Execute(new PumpStreamHandler(output));

        if (owningUser != null) {
            String owners;

            if (owningGroup == null) {
                owners = owningUser;
            } else {
                owners = String.format(FORMAT_OWNER_AND_GROUP, owningUser, owningGroup);
            }

            ex.setCommandline(new String[] { EXECUTABLE_CHOWN, owners, file.getAbsolutePath() });

            this.log.info("Changing file ownership with command '" + ex.getCommandLineString() + "'");

            int returnCode;
            try {
                returnCode = ex.execute();
            } catch (Exception e) {
                throw new PluginException("Failed to change ownership of war file", e);
            }
            if (returnCode != 0) {
                throw new PluginException("Failed to change ownership of war file: " + output.toString());
            }
        }
    }
}
