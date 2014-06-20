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
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Garbage collection related JVM options
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "garbage-collection")
public class GarbageCollection implements Validator, Hierarchical<JvmOptions> {

    private Long maxGCPauseMillis;

    private Long maxGCMinorPauseMillis;

    private JvmOptions parent;

    @XmlAttribute(name = "max-gc-pause-millis")
    public Long getMaxGCPauseMillis() {
        return maxGCPauseMillis;
    }

    public void setMaxGCPauseMillis(Long maxGCPauseMillis) {
        this.maxGCPauseMillis = maxGCPauseMillis;
    }

    @XmlAttribute(name = "max-gc-minor-pause-millis")
    public Long getMaxGCMinorPauseMillis() {
        return maxGCMinorPauseMillis;
    }

    public void setMaxGCMinorPauseMillis(Long maxGCMinorPauseMillis) {
        this.maxGCMinorPauseMillis = maxGCMinorPauseMillis;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        GarbageCollection garbageCollection = (GarbageCollection) target;

        // max gc pause
        if (garbageCollection.getMaxGCPauseMillis() != null) {
            if (garbageCollection.getMaxGCPauseMillis() < 1) {
                errors.rejectValue("maxGCPauseMillis", "configuration.startup.garbageCollection.maxGCPauseMillis.tooLow");
            }
        }

        // max gc pause
        if (garbageCollection.getMaxGCMinorPauseMillis() != null) {
            if (garbageCollection.getMaxGCMinorPauseMillis() < 1) {
                errors.rejectValue("maxGCMinorPauseMillis", "configuration.startup.garbageCollection.maxGCMinorPauseMillis.tooLow");
            }
        }

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
        if (!(obj instanceof GarbageCollection)) {
            return false;
        }
        GarbageCollection garbageCollection = (GarbageCollection) obj;
        return ObjectUtils.nullSafeEquals(this.getMaxGCMinorPauseMillis(), garbageCollection.getMaxGCMinorPauseMillis())
            && ObjectUtils.nullSafeEquals(this.getMaxGCPauseMillis(), garbageCollection.getMaxGCPauseMillis());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.maxGCMinorPauseMillis) * 29 + ObjectUtils.nullSafeHashCode(this.maxGCPauseMillis) * 29;
    }

}
