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

package com.springsource.hq.plugin.tcserver.util.tomcat;

public final class TomcatNameUtils {
    
    private static final String ROOT_APP_DISPLAYED_NAME = "ROOT";
    
    private static final String ROOT_APP_DISPLAYED_NAME_WITH_SLASH = "/" + ROOT_APP_DISPLAYED_NAME;
    
    private static final String ROOT_APP_PATH_EMPTY_STRING = "";
    
    private static final String ROOT_APP_PATH_FORWARD_SLASH = "/";
    
    private TomcatNameUtils() {
    }
    
    public static String convertPathToName(String path) {
        if (ROOT_APP_PATH_EMPTY_STRING.equals(path) || ROOT_APP_PATH_FORWARD_SLASH.equals(path) 
            || ROOT_APP_DISPLAYED_NAME_WITH_SLASH.equals(path.toUpperCase())) {
            return ROOT_APP_DISPLAYED_NAME;
        } else {
            return path;
        }
    }
    
    public static String convertNameToPath(String name) {
        if (ROOT_APP_DISPLAYED_NAME.equals(name.toUpperCase()) || ROOT_APP_DISPLAYED_NAME_WITH_SLASH.equals(name.toUpperCase())) {
            return ROOT_APP_PATH_EMPTY_STRING;
        } else {
            return name;
        }
    }
}
