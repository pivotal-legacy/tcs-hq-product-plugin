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

final class NonRootAgentControlCommandConverter implements ControlCommandConverter {

    private static final String SUDO_NON_INTERACTIVE_SWITCH = "-n";

    private static final String SUDO_USER_SWITCH = "-u";

    private static final File CONTROL_PROGRAM = new File("/usr/bin/sudo");

    private final String instanceUserName;

    NonRootAgentControlCommandConverter(String instanceUserName) {
        this.instanceUserName = instanceUserName;
    }

    public ControlCommand convert(ControlCommand controlCommand) {
        List<String> convertedArguments = new ArrayList<String>();
        convertedArguments.add(SUDO_NON_INTERACTIVE_SWITCH);
        convertedArguments.add(SUDO_USER_SWITCH);
        convertedArguments.add(instanceUserName);
        convertedArguments.add(controlCommand.getControlProgram().getAbsolutePath());
        convertedArguments.addAll(controlCommand.getArguments());

        return new ControlCommand(CONTROL_PROGRAM, convertedArguments);
    }
}
