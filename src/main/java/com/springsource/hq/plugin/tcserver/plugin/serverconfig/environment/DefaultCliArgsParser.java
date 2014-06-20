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

import java.util.ArrayList;
import java.util.List;

public class DefaultCliArgsParser implements CliArgsParser {

    String parameters;

    public DefaultCliArgsParser(String parameters) {
        this.parameters = parameters;
    }

    public List<String> getArgumentList() {
        return separatePropertyValues(parameters);
    }

    private List<String> separatePropertyValues(String fullValue) {
        List<String> values = new ArrayList<String>();
        int quoteCount = 0;
        String separatedValue = "";
        String trimmedFullValue = fullValue.trim();
        for (int i = 0; i < trimmedFullValue.length(); i++) {
            char character = trimmedFullValue.charAt(i);
            if (character == '\'' || character == '\"') {
                quoteCount++;
                separatedValue += character;
            } else {
                separatedValue += character;
            }
            // If there is a space and it is not within quotes it is a new value,
            // or if it is the last character we also want to add the value
            if ((character == ' ' && quoteCount % 2 == 0) || trimmedFullValue.length() == i + 1) {
                values.add(separatedValue.trim());
                quoteCount = 0;
                separatedValue = "";
            }
        }
        return values;
    }
}
