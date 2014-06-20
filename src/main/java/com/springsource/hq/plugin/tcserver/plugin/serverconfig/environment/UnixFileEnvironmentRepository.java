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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public class UnixFileEnvironmentRepository implements EnvironmentRepository {

    private JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    private final Log logger = LogFactory.getLog(UnixFileEnvironmentRepository.class);

    public void save(final String backupFileName, ConfigResponse config, Environment environment) throws PluginException {
        BufferedReader envFileReader = null;
        FileOutputStream newSetEnv = null;

        try {
            try {
                envFileReader = new BufferedReader(new FileReader(backupFileName));
            } catch (FileNotFoundException e) {
                throw new PluginException("Unable to save setenv.  Error parsing existing file.  Cause: " + e.getMessage());
            }

            try {
                newSetEnv = new FileOutputStream(Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh");
            } catch (FileNotFoundException e) {
                throw new PluginException("Unable to save setenv.  Error writing to existing file.  Cause: " + e.getMessage());
            }

            // write backup file to setenv, replacing existing JVM_OPTS line
            String line;
            try {
                line = envFileReader.readLine();
                boolean processedJavaHome = false;
                for (; line != null; line = envFileReader.readLine()) {
                    if (line.trim().startsWith("JVM_OPTS")) {
                        while (line != null && !line.trim().endsWith("\"")) {
                            line = envFileReader.readLine();
                        }
                        writeJvmOpts(environment, newSetEnv);
                        continue;
                    } else if (line.trim().startsWith("JAVA_HOME") || line.trim().startsWith("#JAVA_HOME")) {
                        processedJavaHome = true;
                        writeJavaHome(environment, newSetEnv);
                    } else {
                        newSetEnv.write(line.getBytes());
                        newSetEnv.write("\n".getBytes());
                    }

                }
                if (!processedJavaHome) {
                    // append JAVA_HOME even if it is not already in the file
                    writeJavaHome(environment, newSetEnv);
                }

                newSetEnv.flush();
                newSetEnv.getFD().sync();

            } catch (IOException e) {
                throw new PluginException("Error writing JVM options to setenv.  Cause: " + e.getMessage());
            }
        } finally {
            try {
                if (envFileReader != null) {
                    envFileReader.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing reader to backup setenv file.  Cause: " + e.getMessage());
            }
            try {
                if (newSetEnv != null) {
                    newSetEnv.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing output stream to setenv file.  Cause: " + e.getMessage());
            }
        }
    }

    private void writeJvmOpts(Environment environment, FileOutputStream newSetEnv) throws IOException {
        List<String> jvmOpts = jvmOptionsConverter.convert(environment.getJvmOptions());
        StringBuilder jvmOptString = new StringBuilder("JVM_OPTS=\"");
        Iterator<String> optsIterator = jvmOpts.iterator();
        while (optsIterator.hasNext()) {
            jvmOptString.append(optsIterator.next());
            if (optsIterator.hasNext()) {
                jvmOptString.append(" ");
            }
        }
        jvmOptString.append("\"");
        newSetEnv.write(jvmOptString.toString().getBytes());
        newSetEnv.write("\n".getBytes());
    }

    public void save(ConfigResponse config, Environment environment) throws PluginException {
        File setenv = new File(Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh");
        try {
            setenv.createNewFile();
        } catch (IOException e) {
            throw new PluginException("Error creating new setenv file. Cause: " + e.getMessage());
        }
        OutputStream setenvStream;
        try {
            setenvStream = new FileOutputStream(setenv);
        } catch (FileNotFoundException e) {
            throw new PluginException("Unable to save setenv.  Error writing to existing file.  Cause: " + e.getMessage());
        }
        final StringBuilder setenvString = new StringBuilder();
        setenvString.append("JVM_OPTS=\"");
        final List<String> jvmOpts = jvmOptionsConverter.convert(environment.getJvmOptions());
        for (String jvmOpt : jvmOpts) {
            setenvString.append(jvmOpt + " ");
        }
        setenvString.append("\"\n");
        setenvString.append("CATALINA_OPTS=\"$JVM_OPTS\"");
        setenvString.append("\n");
        if (environment.getJavaHome() != null && !"".equals(environment.getJavaHome().trim())) {
            setenvString.append("JAVA_HOME=\"" + environment.getJavaHome() + "\"");
        } else {
            setenvString.append("#JAVA_HOME=\"\"");
        }
        try {
            setenvStream.write(setenvString.toString().getBytes());
        } catch (IOException e) {
            throw new PluginException("Error writing to new setenv file.  Cause: " + e.getMessage());
        } finally {
            try {
                setenvStream.close();
            } catch (IOException e) {
                logger.warn("Error closing output stream to setenv file.  Cause: " + e.getMessage());
            }
        }
    }

    private void writeJavaHome(Environment environment, OutputStream newSetEnv) throws IOException {
        if (environment.getJavaHome() != null && !"".equals(environment.getJavaHome().trim())) {
            newSetEnv.write(("JAVA_HOME=\"" + environment.getJavaHome() + "\"").getBytes());
        } else {
            newSetEnv.write("#JAVA_HOME=\"\"".getBytes());
        }
        newSetEnv.write("\n".getBytes());
    }

}
