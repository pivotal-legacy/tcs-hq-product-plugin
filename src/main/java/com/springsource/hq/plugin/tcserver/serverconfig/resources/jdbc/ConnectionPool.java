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

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import javax.xml.bind.annotation.XmlType;

import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Base class for specific connection pool implementations
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "connection-pool")
public abstract class ConnectionPool implements Validator, Hierarchical<DataSource> {

    private DataSource parent;

    public void applyParentToChildren() {
        // no-op, no children
    }

    public DataSource parent() {
        return parent;
    }

    public void setParent(DataSource parent) {
        this.parent = parent;
    }

}
