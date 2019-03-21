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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General settings for data sources.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jdbc-general")
public class General implements Validator, Hierarchical<DataSource> {

    private String jndiName;

    private DataSource parent;

    @XmlAttribute(name = "jndi-name", required = true)
    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        General general = (General) target;
        if (!errors.hasFieldErrors("jndiName")) {
            if (!StringUtils.hasText(general.getJndiName())) {
                errors.rejectValue("jndiName", "resource.dataSource.general.jndiName.required");
            } else {
                if (general.parent() != null) {
                    // detect duplicate jndi names
                    for (DataSource dataSource : general.parent().parent().getDataSources()) {
                        General g = dataSource.getGeneral();
                        if (g != general && ObjectUtils.nullSafeEquals(general.getJndiName(), g.getJndiName())) {
                            errors.reject("resource.dataSource.general.jndiName.unique", new Object[] { general.getJndiName() }, null);
                        }
                    }
                }
            }
        }
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public DataSource parent() {
        return parent;
    }

    public void setParent(DataSource parent) {
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
        return ObjectUtils.nullSafeEquals(this.getJndiName(), general.getJndiName());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.jndiName) * 29;
    }

}
