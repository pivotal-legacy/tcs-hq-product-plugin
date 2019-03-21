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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.jmx.MxServerControlPlugin;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.discovery.TcRuntimeDetector;

/**
 * Extension of {@link MxServerControlPlugin} that will kill a process if not killed by stop, and will check that a
 * process has been started to report accurate success/fail of start operation
 * 
 * @author jhickey
 * 
 */
public class TomcatServerControlPlugin extends MxServerControlPlugin {

    // 1 minute timeout on server control actions
    private static final int DEFAULT_TIMEOUT = 60;

    public static final String STATE_ERROR_MULTIPLE_PROCESS = "error-multiple-process";

    public static final String STATE_ERROR_FINDING_PROCESS = "error-finding-process";

    private final Log logger = LogFactory.getLog(TomcatServerControlPlugin.class);

    private ControlCommandConverterFactory controlCommandConverterFactory;

    private volatile Sigar sigar;

    /**
     * Constructor. Sets the default timeout
     */
    public TomcatServerControlPlugin() throws PluginException {
        super();
        this.controlCommandConverterFactory = new StandardControlCommandConverterFactory();
        setTimeout(DEFAULT_TIMEOUT);
    }

    public void configure(ConfigResponse config) throws PluginException {
        this.sigar = new Sigar();
        super.configure(config);
    }

    public void doAction(String action, String[] args) throws PluginException {
        if ("start".equals(action)) {
            final int status = startWithArgs(args);
            setResult(status);
        } else if ("restart".equals(action)) {
            final int status = restartWithArgs(args);
            setResult(status);
        } else if ("stop".equals(action)) {
            final int status = stopWithArgs(args);
            setResult(status);
        } else {
            super.doAction(action, args);
        }
    }

    private int doMyCommand(String action, String[] args) throws PluginException {
        List<String> arguments = new ArrayList<String>();

        File instanceDirectory = getInstanceDirectory();

        arguments.add(instanceDirectory.getName());

        arguments.add(action);
        for (String arg : args) {
            arguments.add(arg);
        }

        arguments.add("-n");
        arguments.add(instanceDirectory.getParentFile().getAbsolutePath());

        ControlCommand controlCommand = this.controlCommandConverterFactory.getControlCommandConverter(
            getConfig(Utils.SERVER_RESOURCE_CONFIG_PROCESS_USERNAME)).convert(new ControlCommand(new File(getControlProgram()), arguments));

        String[] argumentArray = controlCommand.getArguments().toArray(new String[controlCommand.getArguments().size()]);
        return super.doCommand(controlCommand.getControlProgram().getAbsolutePath(), argumentArray);
    }

    private File getInstanceDirectory() {
        return new File(getConfig(TcRuntimeDetector.SERVER_RESOURCE_CONFIG_CATALINA_BASE));
    }

    private int getPid() {
        try {
            final String ptql = Metric.decode(Metric.translate(config.getValue("ptql"), config));
            long[] pids = ProcessFinder.find(sigar, ptql);
            if (pids.length == 1) {
                return (int) pids[0];
            } else if (pids.length > 1) {
                return -2;
            }
        } catch (SigarException e) {
            return -3;
        }
        return -1;
    }

    @Override
    protected String waitForState(String wantedState) {
        int timeout = getTimeoutMillis();
        long timeStart = System.currentTimeMillis();
        String state = detectState();
        if (!(state.equals(STATE_ERROR_MULTIPLE_PROCESS) || state.equals(STATE_ERROR_FINDING_PROCESS))) {
            while (!state.equals(wantedState) && (System.currentTimeMillis() - timeStart) < timeout) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Ignore
                }
                state = detectState();
            }
        }

        return state;
    }

    @Override
    protected String detectState() {
        String state = STATE_UNKNOWN;
        int pid = getPid();
        if (pid >= 0) {
            state = STATE_STARTED;
        } else if (pid == -1) {
            state = STATE_STOPPED;
        } else if (pid == -2) {
            state = STATE_ERROR_MULTIPLE_PROCESS;
        } else if (pid == -3) {
            state = STATE_ERROR_FINDING_PROCESS;
        }

        return state;
    }

    private int kill(int signal) {
        int pid = getPid();
        if (pid >= 0) {
            try {
                sigar.kill(pid, signal);

                if (waitForState(STATE_STOPPED).equals(STATE_STOPPED)) {
                    return RESULT_SUCCESS;
                }
            } catch (SigarException e) {
                logger.warn("Error terminating tc Runtime process.  Cause: " + e.getMessage());
            }
            setMessage("Stop failed.  Unable to kill process");
        }
        return RESULT_FAILURE;
    }

    private int restartWithArgs(String[] args) throws PluginException {
        final int stopStatus = stopWithArgs(args);
        try {
            // don't like doing this, but we need to wait sometime for
            // process cleanup to occur like network ports freeing up
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        final int startStatus = startWithArgs(args);
        if (startStatus == RESULT_SUCCESS && stopStatus == RESULT_SUCCESS) {
            setMessage("Restart executed successfully");
            return RESULT_SUCCESS;
        } else if (startStatus != RESULT_SUCCESS && stopStatus != RESULT_SUCCESS) {
            if (getMessage() != null) {
                setMessage("Restart failed.  " + getMessage());
            } else {
                setMessage("Restart failed");
            }
        } else if (startStatus == RESULT_SUCCESS && stopStatus != RESULT_SUCCESS) {
            if (getMessage() != null) {
                setMessage("Stop failed but start succeeded.  " + getMessage());
            } else {
                setMessage("Stop failed but start succeeded");
            }
        } else if (startStatus != RESULT_SUCCESS && stopStatus == RESULT_SUCCESS) {
            if (getMessage() != null) {
                setMessage("Stop succeeded but start failed.  " + getMessage());
            } else {
                setMessage("Stop succeeded but start failed");
            }
        }
        return RESULT_FAILURE;
    }

    public void shutdown() throws PluginException {
        super.shutdown();
        if (this.sigar != null) {
            this.sigar.close();
        }
    }

    public int startWithArgs(String[] args) throws PluginException {
        final int status = doMyCommand("start", args);
        if (status == RESULT_FAILURE) {
            return status;
        }
        // process may start, then die. It's possible we will find the PID
        // moments before that happens and report success. Therefore, we'll wait
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            logger.debug("Interrupted while waiting for tc Runtime to start.  The start operation may report a false success.");
        }
        String state = waitForState(STATE_STARTED);
        if (state.equals(STATE_ERROR_FINDING_PROCESS)) {
            setMessage("Unable to determine process ID for tc Runtime instance prior to start operation. " + "Cannot verify that start executed correctly");
            return RESULT_FAILURE;
        } else if (state.equals(STATE_ERROR_MULTIPLE_PROCESS)) {
            setMessage("Multiple Process IDs found for installpath: " + config.getValue("installpath")
                + ".  Cannot verify that start operation executed correctly.");
            return RESULT_FAILURE;
        } else if (!(state.equals(STATE_STARTED))) {
            setMessage("Unable to find Process ID of tc Runtime instance.  If instance is running, try increasing control timeout.");
            return RESULT_FAILURE;
        }
        setMessage("Start executed successfully");
        return RESULT_SUCCESS;
    }

    private int stopWithArgs(String[] args) throws PluginException {
        // Don't attempt to stop an already stopped server- just report success and move on
        if (detectState().equals(STATE_STOPPED)) {
            setMessage("Stop executed successfully");
            return RESULT_SUCCESS;
        }
        // int status = super.stop();
        final int status = doMyCommand("stop", args);
        if (status == RESULT_SUCCESS) {
            String state = waitForState(STATE_STOPPED);
            if (state.equals(STATE_STOPPED)) {
                setMessage("Stop executed successfully");
                return RESULT_SUCCESS;
            } else if (state.equals(STATE_ERROR_FINDING_PROCESS)) {
                setMessage("Unable to determine process ID for tc Runtime instance prior to stop operation. "
                    + "Cannot verify that stop executed correctly");
                return RESULT_FAILURE;
            } else if (state.equals(STATE_ERROR_MULTIPLE_PROCESS)) {
                setMessage("Multiple Process IDs found for installpath: " + config.getValue("installpath")
                    + ".  Cannot verify that stop operation executed correctly.");
                return RESULT_FAILURE;
            }
        }

        int sig = Sigar.getSigNum("TERM");
        if (kill(sig) == RESULT_FAILURE) {
            return kill(9);
        }

        setMessage("Stop executed successfully");
        return RESULT_SUCCESS;
    }
}
