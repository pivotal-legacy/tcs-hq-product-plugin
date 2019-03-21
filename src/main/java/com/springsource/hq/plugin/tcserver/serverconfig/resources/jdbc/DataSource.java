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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;

/**
 * Base class for specific data source implementations. Contains commons elements shared by all data sources.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "data-source", propOrder = { "general", "connection" })
public abstract class DataSource implements Validator, Hierarchical<Settings>, Identity {

    private General general;

    private Connection connection;

    private Settings parent;

    private String id;

    public DataSource() {
        this.general = new General();
        this.connection = new Connection();
    }

    @XmlElement(name = "jdbc-general", required = true)
    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    @XmlElement(name = "connection", required = true)
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Settings parent() {
        return parent;
    }

    public void setParent(Settings parent) {
        this.parent = parent;
    }

    @XmlTransient
    public String getHumanId() {
        return general.getJndiName();
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataSource)) {
            return false;
        }
        DataSource dataSource = (DataSource) obj;
        return ObjectUtils.nullSafeEquals(this.getConnection(), dataSource.getConnection())
            && ObjectUtils.nullSafeEquals(this.getGeneral(), dataSource.getGeneral());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.connection) * 29 + ObjectUtils.nullSafeHashCode(this.general) * 29;
    }

}
