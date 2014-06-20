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

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for thread diagnostics valve.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "threadDiagnostics")
public class ThreadDiagnostics implements Validator, Hierarchical<Engine> {

    private static final Boolean DEFAULT_ENABLED = false;

    private static final Long DEFAULT_HISTORY = 1000L;

    private static final Long DEFAULT_THRESHOLD = 5000L;

    private Boolean enabled = DEFAULT_ENABLED;

    private Long history = DEFAULT_HISTORY;

    private Long threshold = DEFAULT_THRESHOLD;

    private Engine parent;

    public void applyParentToChildren() {
        // no-op, no children
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ThreadDiagnostics)) {
            return false;
        }
        ThreadDiagnostics threadDianostics = (ThreadDiagnostics) obj;
        return ObjectUtils.nullSafeEquals(this.getEnabled(), threadDianostics.getEnabled())
            && ObjectUtils.nullSafeEquals(this.getHistory(), threadDianostics.getHistory())
            && ObjectUtils.nullSafeEquals(this.getThreshold(), threadDianostics.getThreshold());
    }

    @XmlAttribute(name = "history")
    public Long getHistory() {
        return history;
    }

    @XmlAttribute(name = "enabled", required = true)
    public Boolean getEnabled() {
        return enabled;
    }

    @XmlAttribute(name = "threshold")
    public Long getThreshold() {
        return threshold;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.enabled) * 29 + ObjectUtils.nullSafeHashCode(this.history) * 29
            + ObjectUtils.nullSafeHashCode(this.threshold) * 29;
    }

    public Engine parent() {
        return parent;
    }

    public void setHistory(Long history) {
        this.history = history;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setParent(Engine parent) {
        this.parent = parent;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // ThreadDiagnostics threadDiagnostics = (ThreadDiagnostics) target;
        // TODO determine validation rules
    }

}
