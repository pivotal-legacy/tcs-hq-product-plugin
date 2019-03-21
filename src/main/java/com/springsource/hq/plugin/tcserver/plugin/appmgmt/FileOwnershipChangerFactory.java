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


/**
 * A <code>FileOwnershipChangerFactory</code> can be used to get a <code>FileOwnershipChangerFactory</code> instance
 * that is appropriate for the current OS.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 * 
 */
public interface FileOwnershipChangerFactory {

    /**
     * Gets a file ownership changer that's appropriate for the current OS
     * 
     * @return A file ownership changer instance
     */
    FileOwnershipChanger getFileOwnershipChanger();
}
