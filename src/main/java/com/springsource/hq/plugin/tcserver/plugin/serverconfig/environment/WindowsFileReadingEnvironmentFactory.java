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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Create an Environment object based on the Windows wrapper.conf file.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
public class WindowsFileReadingEnvironmentFactory implements EnvironmentFactory {

    private JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    private WindowsSpecificNonJvmOptsUtil windowsOptsUtil = new WindowsSpecificNonJvmOptsUtil();

    public Environment create(ConfigResponse config) throws PluginException {
        try {
            Environment environment = new Environment();
            Resource wrapperConf = new FileSystemResource(Metric.decode(config.getValue("installpath")) + "/conf/wrapper.conf");
            if (wrapperConf.exists()) {
                environment.setJvmOptions(createJvmOptions(wrapperConf));
                environment.setJavaHome(createJavaHome(wrapperConf));
                return environment;
            }
            return environment;
        } catch (Exception e) {
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e.getMessage());
        }
    }

    private JvmOptions createJvmOptions(final Resource wrapperConf) throws IOException {
        BufferedReader envFileReader = new BufferedReader(new InputStreamReader(wrapperConf.getInputStream()));
        try {
            List<String> jvmOpts = new LinkedList<String>();
            String line = envFileReader.readLine();
            for (; line != null; line = envFileReader.readLine()) {
                if (line.trim().startsWith("wrapper.java.additional")) {
                    jvmOpts.add(windowsOptsUtil.stripQuotes(line.trim().substring(line.indexOf("=") + 1)));
                }
            }
            jvmOpts = windowsOptsUtil.removeProtectedOpts(jvmOpts);
            return jvmOptionsConverter.convert(jvmOpts);
        } finally {
            envFileReader.close();
        }
    }

    private String createJavaHome(final Resource wrapperConf) throws IOException {
        BufferedReader envFileReader = new BufferedReader(new InputStreamReader(wrapperConf.getInputStream()));
        try {
            String line = envFileReader.readLine();
            for (; line != null; line = envFileReader.readLine()) {
                if (line.trim().startsWith("set.JAVA_HOME") && line.indexOf("=") != -1) {
                    return windowsOptsUtil.stripQuotes(line.substring(line.indexOf("=") + 1));
                }
            }
            return null;
        } finally {
            envFileReader.close();
        }
    }

}
