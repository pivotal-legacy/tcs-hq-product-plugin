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

package com.springsource.hq.plugin.tcserver.plugin.discovery;

import java.io.File;

public final class TcRuntime80Detector extends TcRuntimeDetector {

    @Override
    protected boolean isTcRuntimeInstance(String catalinaHome, String catalinaBase) {
        return super.isTcRuntimeInstance(catalinaHome, catalinaBase) && (isMyTomcatVersion(catalinaHome, catalinaBase));
    }

    protected boolean isMyTomcatVersion(String catalinaHome, String catalinaBase) {
        boolean tomcat8 = new File(catalinaHome, TOMCAT_8_SPECIFIC_JAR).exists() && ! new File(catalinaHome, TOMCAT_85_SPECIFIC_JAR).exists();
        return tomcat8;
    }
}
