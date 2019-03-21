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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General JVM options
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jvm-general")
public class General implements Validator, Hierarchical<JvmOptions> {

    private Boolean server;

    private JvmOptions parent;

    @XmlAttribute(name = "server")
    public Boolean getServer() {
        return server;
    }

    public void setServer(Boolean server) {
        this.server = server;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // General general = (General) target;
        // TODO define validation rules
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public JvmOptions parent() {
        return parent;
    }

    public void setParent(JvmOptions parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof General)) {
            return false;
        }
        General general = (General) obj;
        return ObjectUtils.nullSafeEquals(this.getServer(), general.getServer());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.server) * 29;
    }

}
