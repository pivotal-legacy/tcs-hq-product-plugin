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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Update an existing wrapper.conf file with the new values for JVM_OPTS and JAVA_HOME.
 * 
 * @author Scott Andrews
 * @author Greg Turnquist
 * @since 2.0
 */
public class WindowsFileEnvironmentRepository implements EnvironmentRepository {

    private JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    private WindowsSpecificNonJvmOptsUtil windowsOptsUtil = new WindowsSpecificNonJvmOptsUtil();

    private final Log logger = LogFactory.getLog(getClass());

    public void save(String backupFileName, ConfigResponse config, Environment environment) throws PluginException {
        BufferedReader wrapperconfFileReader = null;
        FileOutputStream newWrapperconf = null;

        try {
            try {
                wrapperconfFileReader = new BufferedReader(new FileReader(backupFileName));
            } catch (FileNotFoundException e) {
                throw new PluginException("Unable to save wrapper.conf.  Error parsing existing file.  Cause: " + e.getMessage());
            }

            try {
                newWrapperconf = new FileOutputStream(Metric.decode(config.getValue("installpath")) + "/conf/wrapper.conf");
            } catch (FileNotFoundException e) {
                throw new PluginException("Unable to save wrapper.conf.  Error writing to existing file.  Cause: " + e.getMessage());
            }

            // write backup file to wrapper.conf, replacing existing JVM_OPTS and JAVA_HOME
            String line;
            try {
                line = wrapperconfFileReader.readLine();
                boolean processedJvmOpts = false;
                boolean processingJvmOpts = false;
                boolean processedJavaHome = false;
                List<String> exsistingJvmOpts = new ArrayList<String>();
                for (; line != null; line = wrapperconfFileReader.readLine()) {
                    /**
                     * The two following "if" statements represents an embedded state machine here where it flip flops
                     * between parsing JVM options and having completed that phase.
                     * 
                     * -- Testing for wrapper.java.additional enters a special state tagged "processingJvmOpts". -- When
                     * the last wrapper.java.additional is passed, it exits the state "processingJvmOpts".
                     * 
                     * The if-then checks for this MUST come before any other checks. Otherwise, the state machine gets
                     * mixed up with other line checks, and can cause lines to be dropped.
                     */
                    if (line.trim().startsWith("wrapper.java.additional")) {
                        if (!processingJvmOpts) {
                            processingJvmOpts = true;
                        }
                        // add jvm opt to exsistingJvmOpts
                        int pos = line.indexOf("=");
                        if (pos != -1) {
                            exsistingJvmOpts.add(windowsOptsUtil.stripQuotes(line.substring(pos + 1)));
                        }
                    } else if (processingJvmOpts) {
                        writeJvmOpts(environment.getJvmOptions(), newWrapperconf, exsistingJvmOpts);
                        processingJvmOpts = false;
                        processedJvmOpts = true;
                    }
                    /**
                     * The rest of these else-if's represent simple line checks for parsing and involves no special
                     * state machine.
                     */
                    else if (line.trim().equals("")) {
                        newWrapperconf.write(line.getBytes());
                        newWrapperconf.write("\n".getBytes());
                    } else if (line.trim().startsWith("set.JAVA_HOME") || line.trim().startsWith("#set.JAVA_HOME")) {
                        if (!processedJavaHome) {
                            writeJavaHome(environment.getJavaHome(), newWrapperconf);
                            processedJavaHome = true;
                        }
                    } else {
                        newWrapperconf.write(line.getBytes());
                        newWrapperconf.write("\n".getBytes());
                    }
                }
                if (!processedJvmOpts) {
                    writeJvmOpts(environment.getJvmOptions(), newWrapperconf, null);
                }
                if (!processedJavaHome) {
                    writeJavaHome(environment.getJavaHome(), newWrapperconf);
                }

                newWrapperconf.flush();
                newWrapperconf.getFD().sync();

            } catch (IOException e) {
                throw new PluginException("Error writing JVM options to wrapper.conf.  Cause: " + e.getMessage());
            }
        } finally {
            try {
                if (wrapperconfFileReader != null) {
                    wrapperconfFileReader.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing input stream to backup wrapper.conf file.  Cause: " + e.getMessage());
            }
            try {
                if (newWrapperconf != null) {
                    newWrapperconf.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing output stream to wrapper.conf file.  Cause: " + e.getMessage());
            }
        }
    }

    public final void save(ConfigResponse config, Environment environment) throws PluginException {
        throw new UnsupportedOperationException(
            "Use of the backup file is required when writing wrapper.conf.  Please invoke WindowsFileEnvironmentRepository.save(String, ConfigResponse, Environment) instead.");
    }

    private void writeJavaHome(final String javaHome, final OutputStream newWrapperconf) throws IOException {
        if (javaHome != null && !"".equals(javaHome.trim())) {
            newWrapperconf.write(("set.JAVA_HOME=" + javaHome.trim()).getBytes());
        } else {
            newWrapperconf.write(("#set.JAVA_HOME=").getBytes());
        }
        newWrapperconf.write("\n".getBytes());
    }

    private void writeJvmOpts(final JvmOptions jvmOptions, final OutputStream newWrapperconf, final List<String> exsistingJvmOpts) throws IOException {
        List<String> jvmOpts = jvmOptionsConverter.convert(jvmOptions);
        jvmOpts = windowsOptsUtil.mergeOpts(exsistingJvmOpts, jvmOpts);
        int counter = 0;
        for (String jvmOpt : jvmOpts) {
            StringBuilder line = new StringBuilder();
            line.append("wrapper.java.additional.");
            line.append(++counter);
            line.append("=");
            line.append(windowsOptsUtil.addQuotesIfNeeded(jvmOpt));
            line.append("\n");
            newWrapperconf.write(line.toString().getBytes());
        }
        newWrapperconf.write("\n".getBytes());
    }

}
