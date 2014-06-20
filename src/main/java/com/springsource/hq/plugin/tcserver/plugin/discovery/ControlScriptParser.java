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

package com.springsource.hq.plugin.tcserver.plugin.discovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyperic.hq.product.PluginException;

final class ControlScriptParser {

    private static final Pattern PATTERN_INSTALL_BASE = Pattern.compile("(?:set )*INSTALL_BASE=(.+)");

    private static final Object INSTALL_BASE_PLACEHOLDER = "placeholder";

    File getInstallBase(String catalinaBase, File controlScript) throws PluginException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(controlScript));

            String line = null;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = PATTERN_INSTALL_BASE.matcher(line);
                if (matcher.matches()) {
                    String installBase = matcher.group(1);

                    File installationRoot = new File(installBase.replaceAll("\"", ""));
                    if (installationRoot.isDirectory()) {
                        return installationRoot;
                    } else if (INSTALL_BASE_PLACEHOLDER.equals(installBase)) {
                        return controlScript.getParentFile().getParentFile().getParentFile();
                    }
                }
            }
            throw new PluginException("Did not find INSTALL_BASE in control script '" + controlScript + "'");
        } catch (IOException ioe) {
            throw new PluginException("Failed to parse control script '" + controlScript
                + "' when attempting to determine installation root of combined instance '" + catalinaBase + "'", ioe);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    // Do nothing
                }
            }
        }
    }
}
