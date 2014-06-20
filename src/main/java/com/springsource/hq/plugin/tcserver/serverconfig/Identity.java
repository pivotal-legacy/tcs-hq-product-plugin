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

package com.springsource.hq.plugin.tcserver.serverconfig;

/**
 * Provides human and machine readable identifiers. The human identifier is calculated while the machine identifier must
 * be explicitly set.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
public interface Identity {

    /**
     * @return the machine identifier passed to {@link Identity#setId(String)}, returning null if
     *         {@link Identity#setId(String)} is never invoked for this instance.
     */
    public String getId();

    /**
     * @param id the machine identifier
     */
    public void setId(String id);

    /**
     * Calculate a URI template variable safe, human readable identifier for this instance.
     * 
     * <p>
     * <strong>Note:</strong> URI template variable will be URL encoded, however, many web servers will not respond to
     * requests that contain a URL encoded forward slash '%2F'. Forward slashes should not be present in the identifier,
     * unless it is certain that the web server will accept them.
     * 
     * @return a human readable identifier
     */
    public String getHumanId();

}
