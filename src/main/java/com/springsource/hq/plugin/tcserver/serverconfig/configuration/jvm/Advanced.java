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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm;

import javax.xml.bind.annotation.XmlAttribute;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Advanced JVM options allowing the user to specify their own command line arguments
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
public class Advanced implements Validator, Hierarchical<JvmOptions> {

    private String cliArgs;

    private JvmOptions parent;

    @XmlAttribute(name = "cli-args")
    public String getCliArgs() {
        return cliArgs;
    }

    public void setCliArgs(String cliArgs) {
        this.cliArgs = cliArgs;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // Advanced advanced = (Advanced) target;
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
        if (!(obj instanceof Advanced)) {
            return false;
        }
        Advanced advanced = (Advanced) obj;
        return ObjectUtils.nullSafeEquals(this.getCliArgs(), advanced.getCliArgs());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.cliArgs) * 29;
    }

}
