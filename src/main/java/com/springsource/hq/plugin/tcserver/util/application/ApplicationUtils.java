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

package com.springsource.hq.plugin.tcserver.util.application;

import java.util.Set;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

public final class ApplicationUtils {
    
    private static final String FORMAT_STRING_VERSION = "%06d";

    private ApplicationUtils() {
        
    }
    
    public static String getNewRevisionForApplication(Set<Application> deployedApplications, String deployPath) {
        String deployName = TomcatNameUtils.convertPathToName(deployPath);
        int latestRevision = -1;
        
        for (Application application : deployedApplications) {
            if (deployName.equals(application.getName())) {
                latestRevision = Math.max(latestRevision, application.getVersion());
            }
        }
        
        if (latestRevision > -1) {
            return String.format(FORMAT_STRING_VERSION, latestRevision + 1);
        } else {
            return null;
        }
    }   
    
    public static String convertVersionToPaddedString(int version) {
        if (version == 0) {
            return "";
        } else {
            return String.format(FORMAT_STRING_VERSION, version);
        }
    }
    
    public static String convertVersionToPaddedString(String version) {
        return convertVersionToPaddedString(Integer.parseInt(version));
    }
}
