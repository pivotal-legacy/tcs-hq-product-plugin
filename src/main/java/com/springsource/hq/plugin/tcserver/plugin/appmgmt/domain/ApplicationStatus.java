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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain;

public class ApplicationStatus {

    private String applicationName;

    private boolean hasError;

    private String resultMessage;

    public String getApplicationName() {
        return applicationName;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setApplicationName(String resourceName) {
        this.applicationName = resourceName;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

}
