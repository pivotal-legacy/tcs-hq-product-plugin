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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

final class ObjectNameUtils {

    private ObjectNameUtils() {

    }

    static String getManagerMBeanObjectNameForApplication(String host, Application application, boolean tomcat7) {
        String applicationIdentifier = TomcatNameUtils.convertNameToPath(application.getName());

        if (applicationIdentifier.equals("")) {
            applicationIdentifier = "/";
        } else if (!applicationIdentifier.startsWith("/")) {
            applicationIdentifier = "/" + applicationIdentifier;
        }

        if (tomcat7 && application.getVersion() > 0) {
            applicationIdentifier = applicationIdentifier + "##" + ApplicationUtils.convertVersionToPaddedString(application.getVersion());
        }

        String pathOrContext;

        if (tomcat7) {
            pathOrContext = "context";
        } else {
            pathOrContext = "path";
        }

        return "Catalina:type=Manager," + pathOrContext + "=" + applicationIdentifier + ",host=" + host;
    }
}
