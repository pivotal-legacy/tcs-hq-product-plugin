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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Advanced;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Debug;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.GarbageCollection;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.General;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Memory;

public class DefaultJvmOptionsConverter implements JvmOptionsConverter {

    private final Log logger = LogFactory.getLog(DefaultJvmOptionsConverter.class);

    private static final String THREAD_STACK_SIZE = "-Xss";

    private static final String PERM_SIZE = "-XX:PermSize=";

    private static final String NEW_SIZE = "-XX:NewSize=";

    private static final String MAX_HEAP = "-Xmx";

    private static final String MIN_HEAP = "-Xms";

    private static final String MAX_PERM_SIZE = "-XX:MaxPermSize=";

    private static final String MAX_NEW_SIZE = "-XX:MaxNewSize=";

    private static final String MAX_GC_PAUSE_MILLIS = "-XX:MaxGCPauseMillis=";

    private static final String MAX_GC_MINOR_PAUSE_MILLIS = "-XX:MaxGCMinorPauseMillis=";

    private static final String PRINT_HEAP_AT_GC = "-XX:+PrintHeapAtGC";

    private static final String PRINT_GC_TIME_STAMPS = "-XX:+PrintGCTimeStamps";

    private static final String PRINT_GC_DETAILS = "-XX:+PrintGCDetails";

    private static final String PRINT_GC_APPLICATION_STOPPED_TIME = "-XX:+PrintGCApplicationStoppedTime";

    private static final String PRINT_GC = "-XX:+PrintGC";

    private static final String LOG_GC = "-Xloggc:";

    private static final String HEAP_DUMP_ON_OUT_OF_MEMORY_ERROR = "-XX:+HeapDumpOnOutOfMemoryError";

    private static final String SERVER = "-server";

    public JvmOptions convert(final List<String> options) {
        final JvmOptions jvmOptions = new JvmOptions();
        final General general = new General();
        final Debug debug = new Debug();
        final Advanced advanced = new Advanced();
        final GarbageCollection garbageCollection = new GarbageCollection();
        final Memory memory = new Memory();
        jvmOptions.setAdvanced(advanced);
        jvmOptions.setDebug(debug);
        jvmOptions.setGarbageCollection(garbageCollection);
        jvmOptions.setGeneral(general);
        jvmOptions.setMemory(memory);
        final StringBuilder advancedCommandLine = new StringBuilder();
        final Iterator<String> iterator = options.iterator();
        while (iterator.hasNext()) {
            final String jvmOption = iterator.next();
            if (SERVER.equals(jvmOption)) {
                general.setServer(true);
            } else if (HEAP_DUMP_ON_OUT_OF_MEMORY_ERROR.equals(jvmOption)) {
                debug.setHeapDumpOnOutOfMemoryError(true);
            } else if (jvmOption.startsWith(LOG_GC)) {
                debug.setLoggc(jvmOption.substring(LOG_GC.length()));
            } else if (PRINT_GC.equals(jvmOption)) {
                debug.setPrintGC(true);
            } else if (PRINT_GC_APPLICATION_STOPPED_TIME.equals(jvmOption)) {
                debug.setPrintGCApplicationStoppedTime(true);
            } else if (PRINT_GC_DETAILS.equals(jvmOption)) {
                debug.setPrintGCDetails(true);
            } else if (PRINT_GC_TIME_STAMPS.equals(jvmOption)) {
                debug.setPrintGCTimeStamps(true);
            } else if (PRINT_HEAP_AT_GC.equals(jvmOption)) {
                debug.setPrintHeapAtGC(true);
            } else if (jvmOption.startsWith(MAX_GC_MINOR_PAUSE_MILLIS)) {
                try {
                    garbageCollection.setMaxGCMinorPauseMillis(Long.valueOf(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MAX_GC_MINOR_PAUSE_MILLIS
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(MAX_GC_PAUSE_MILLIS)) {
                try {
                    garbageCollection.setMaxGCPauseMillis(Long.valueOf(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MAX_GC_PAUSE_MILLIS
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(MAX_NEW_SIZE)) {
                try {
                    memory.setMaxNewSize(getMemValueMB(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MAX_NEW_SIZE
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(MAX_PERM_SIZE)) {
                try {
                    memory.setMaxPermSize(getMemValueMB(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MAX_PERM_SIZE
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(MIN_HEAP)) {
                try {
                    memory.setMs(getMemValueMB(jvmOption.substring(MIN_HEAP.length())));
                } catch (NumberFormatException e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MIN_HEAP
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(MAX_HEAP)) {
                try {
                    memory.setMx(getMemValueMB(jvmOption.substring(MAX_HEAP.length())));
                } catch (NumberFormatException e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + MAX_HEAP
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(NEW_SIZE)) {
                try {
                    memory.setNewSize(getMemValueMB(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + NEW_SIZE
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(PERM_SIZE)) {
                try {
                    memory.setPermSize(getMemValueMB(jvmOption.split("=")[1]));
                } catch (Exception e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + PERM_SIZE
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else if (jvmOption.startsWith(THREAD_STACK_SIZE)) {
                try {
                    memory.setSs(getMemValueKB(jvmOption.substring(THREAD_STACK_SIZE.length())));
                } catch (NumberFormatException e) {
                    logger.warn("Error reading tc Runtime configuration.  JVM option " + THREAD_STACK_SIZE
                        + " set to a non-numeric value.  Default value will be displayed instead.");
                }
            } else {
                advancedCommandLine.append(jvmOption).append(" ");
            }
        }
        if (!("".equals(advancedCommandLine.toString().trim()))) {
            advanced.setCliArgs(advancedCommandLine.toString().trim());
        }
        return jvmOptions;
    }

    public List<String> convert(JvmOptions jvmOptions) {
        final List<String> jvmOpts = new LinkedList<String>();
        if (Boolean.TRUE.equals(jvmOptions.getGeneral().getServer())) {
            jvmOpts.add(SERVER);
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getHeapDumpOnOutOfMemoryError())) {
            jvmOpts.add(HEAP_DUMP_ON_OUT_OF_MEMORY_ERROR);
        }
        if (jvmOptions.getDebug().getLoggc() != null && !("".equals(jvmOptions.getDebug().getLoggc()))) {
            jvmOpts.add(LOG_GC + jvmOptions.getDebug().getLoggc());
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getPrintGC())) {
            jvmOpts.add(PRINT_GC);
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getPrintGCApplicationStoppedTime())) {
            jvmOpts.add(PRINT_GC_APPLICATION_STOPPED_TIME);
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getPrintGCDetails())) {
            jvmOpts.add(PRINT_GC_DETAILS);
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getPrintGCTimeStamps())) {
            jvmOpts.add(PRINT_GC_TIME_STAMPS);
        }
        if (Boolean.TRUE.equals(jvmOptions.getDebug().getPrintHeapAtGC())) {
            jvmOpts.add(PRINT_HEAP_AT_GC);
        }
        if (jvmOptions.getGarbageCollection().getMaxGCMinorPauseMillis() != null) {
            jvmOpts.add(MAX_GC_MINOR_PAUSE_MILLIS + jvmOptions.getGarbageCollection().getMaxGCMinorPauseMillis());
        }
        if (jvmOptions.getGarbageCollection().getMaxGCPauseMillis() != null) {
            jvmOpts.add(MAX_GC_PAUSE_MILLIS + jvmOptions.getGarbageCollection().getMaxGCPauseMillis());
        }
        if (jvmOptions.getMemory().getMaxNewSize() != null) {
            jvmOpts.add(MAX_NEW_SIZE + jvmOptions.getMemory().getMaxNewSize() + "m");
        }
        if (jvmOptions.getMemory().getMaxPermSize() != null) {
            jvmOpts.add(MAX_PERM_SIZE + jvmOptions.getMemory().getMaxPermSize() + "m");
        }
        if (jvmOptions.getMemory().getMs() != null) {
            jvmOpts.add(MIN_HEAP + jvmOptions.getMemory().getMs() + "m");
        }
        if (jvmOptions.getMemory().getMx() != null) {
            jvmOpts.add(MAX_HEAP + jvmOptions.getMemory().getMx() + "m");
        }
        if (jvmOptions.getMemory().getNewSize() != null) {
            jvmOpts.add(NEW_SIZE + jvmOptions.getMemory().getNewSize() + "m");
        }
        if (jvmOptions.getMemory().getPermSize() != null) {
            jvmOpts.add(PERM_SIZE + jvmOptions.getMemory().getPermSize() + "m");
        }
        if (jvmOptions.getMemory().getSs() != null) {
            jvmOpts.add(THREAD_STACK_SIZE + jvmOptions.getMemory().getSs() + "k");
        }
        if (jvmOptions.getAdvanced().getCliArgs() != null && !("".equals(jvmOptions.getAdvanced().getCliArgs()))) {
            CliArgsParser parser = new DefaultCliArgsParser(jvmOptions.getAdvanced().getCliArgs());
            jvmOpts.addAll(parser.getArgumentList());
        }
        return jvmOpts;
    }

    private Long getMemValueMB(final String memValue) throws NumberFormatException {
        if (memValue.endsWith("k") || memValue.endsWith("K")) {
            final String kb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(kb) / 1000);
        } else if (memValue.endsWith("m") || memValue.endsWith("M")) {
            final String mb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(mb));
        } else if (memValue.endsWith("g") || memValue.endsWith("G")) {
            final String gb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(gb) * 1000);
        }
        return (Long.valueOf(memValue) / 1000000);
    }

    private Long getMemValueKB(final String memValue) throws NumberFormatException {
        if (memValue.endsWith("k") || memValue.endsWith("K")) {
            final String kb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(kb));
        } else if (memValue.endsWith("m") || memValue.endsWith("M")) {
            final String mb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(mb) * 1000);
        } else if (memValue.endsWith("g") || memValue.endsWith("G")) {
            final String gb = memValue.substring(0, memValue.length() - 1);
            return (Long.valueOf(gb) * 1000000);
        }
        return (Long.valueOf(memValue) / 1000);
    }
}
