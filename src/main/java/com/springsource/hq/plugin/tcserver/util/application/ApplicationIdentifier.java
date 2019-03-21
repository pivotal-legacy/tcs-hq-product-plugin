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

public final class ApplicationIdentifier implements Comparable<ApplicationIdentifier> {

    private final String name;

    private final int version;

    public ApplicationIdentifier(String applicationIdentifierString) {
        String[] components = applicationIdentifierString.split("##");

        this.name = components[0];

        if (components.length > 1) {
            this.version = Integer.parseInt(components[1]);
        } else {
            this.version = 0;
        }
    }

    public ApplicationIdentifier(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApplicationIdentifier other = (ApplicationIdentifier) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name);

        if (version > 0) {
            builder.append(" revision ");
            builder.append(this.version);
        }

        return builder.toString();
    }

    public int compareTo(ApplicationIdentifier o) {
        int comparison = this.name.compareTo(o.name);
        if (comparison == 0) {
            comparison = this.version - o.version;
        }
        return comparison;
    }
}
