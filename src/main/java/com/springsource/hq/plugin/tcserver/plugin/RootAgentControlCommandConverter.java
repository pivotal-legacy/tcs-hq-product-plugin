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

package com.springsource.hq.plugin.tcserver.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class RootAgentControlCommandConverter implements ControlCommandConverter {

    private static final String SU_COMMAND_SWITCH = "-c";

    private static final File CONTROL_PROGRAM = new File("/bin/su");

    private final String instanceUserName;

    public RootAgentControlCommandConverter(String instanceUserName) {
        this.instanceUserName = instanceUserName;
    }

    public ControlCommand convert(ControlCommand controlCommand) {
        List<String> convertedArguments = new ArrayList<String>();
        convertedArguments.add(instanceUserName);
        convertedArguments.add(SU_COMMAND_SWITCH);
        convertedArguments.add(buildCommandString(controlCommand));

        return new ControlCommand(CONTROL_PROGRAM, convertedArguments);
    }

    private String buildCommandString(ControlCommand controlCommand) {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(controlCommand.getControlProgram().getAbsolutePath());

        Iterator<String> argumentIterator = controlCommand.getArguments().iterator();

        while (argumentIterator.hasNext()) {
            commandBuilder.append(" ");
            commandBuilder.append(argumentIterator.next());
        }

        return commandBuilder.toString();
    }
}
