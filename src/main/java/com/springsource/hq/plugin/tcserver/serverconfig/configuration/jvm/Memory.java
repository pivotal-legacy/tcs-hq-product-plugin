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

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Memory related JVM options
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
public class Memory implements Validator, Hierarchical<JvmOptions> {

    private Long ms;

    private Long mx;

    private Long ss;

    private Long newSize;

    private Long maxNewSize;

    private Long permSize;

    private Long maxPermSize;

    private JvmOptions parent;

    @XmlAttribute(name = "ms")
    public Long getMs() {
        return ms;
    }

    public void setMs(Long ms) {
        this.ms = ms;
    }

    @XmlAttribute(name = "mx")
    public Long getMx() {
        return mx;
    }

    public void setMx(Long mx) {
        this.mx = mx;
    }

    @XmlAttribute(name = "ss")
    public Long getSs() {
        return ss;
    }

    public void setSs(Long ss) {
        this.ss = ss;
    }

    @XmlAttribute(name = "new-size")
    public Long getNewSize() {
        return newSize;
    }

    public void setNewSize(Long newSize) {
        this.newSize = newSize;
    }

    @XmlAttribute(name = "max-new-size")
    public Long getMaxNewSize() {
        return maxNewSize;
    }

    public void setMaxNewSize(Long maxNewSize) {
        this.maxNewSize = maxNewSize;
    }

    @XmlAttribute(name = "perm-size")
    public Long getPermSize() {
        return permSize;
    }

    public void setPermSize(Long permSize) {
        this.permSize = permSize;
    }

    @XmlAttribute(name = "max-perm-size")
    public Long getMaxPermSize() {
        return maxPermSize;
    }

    public void setMaxPermSize(Long maxPermSize) {
        this.maxPermSize = maxPermSize;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Memory memory = (Memory) target;

        // heap size
        if (memory.getMs() != null) {
            if (memory.getMs() < 1) {
                errors.rejectValue("ms", "configuration.startup.memory.ms.tooLow");
            } else if (memory.getMx() != null && memory.getMs() > memory.getMx()) {
                errors.rejectValue("ms", "configuration.startup.memory.ms.tooHigh");
            }
        }
        if (memory.getMx() != null) {
            if (memory.getMs() != null && memory.getMs() > memory.getMx()) {
                errors.rejectValue("mx", "configuration.startup.memory.mx.tooLow");
            }
        }

        // young generation size
        if (memory.getNewSize() != null) {
            if (memory.getNewSize() < 1) {
                errors.rejectValue("newSize", "configuration.startup.memory.newSize.tooLow");
            } else if (memory.getMaxNewSize() != null && memory.getNewSize() > memory.getMaxNewSize()) {
                errors.rejectValue("newSize", "configuration.startup.memory.newSize.tooHigh");
            }
        }
        if (memory.getMaxNewSize() != null) {
            if (memory.getNewSize() != null && memory.getNewSize() > memory.getMaxNewSize()) {
                errors.rejectValue("maxNewSize", "configuration.startup.memory.maxNewSize.tooLow");
            }
        }

        // permgen size
        if (memory.getPermSize() != null) {
            if (memory.getPermSize() < 1) {
                errors.rejectValue("permSize", "configuration.startup.memory.permSize.tooLow");
            } else if (memory.getMaxPermSize() != null && memory.getPermSize() > memory.getMaxPermSize()) {
                errors.rejectValue("permSize", "configuration.startup.memory.permSize.tooHigh");
            }
        }
        if (memory.getMaxPermSize() != null) {
            if (memory.getPermSize() != null && memory.getPermSize() > memory.getMaxPermSize()) {
                errors.rejectValue("maxPermSize", "configuration.startup.memory.maxPermSize.tooLow");
            }
        }

        // thread stack size
        if (memory.getSs() != null) {
            if (memory.getSs() < 1) {
                errors.rejectValue("ss", "configuration.startup.memory.ss.tooLow");
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
        if (!(obj instanceof Memory)) {
            return false;
        }
        Memory memory = (Memory) obj;
        return ObjectUtils.nullSafeEquals(this.getMaxNewSize(), memory.getMaxNewSize())
            && ObjectUtils.nullSafeEquals(this.getMaxPermSize(), memory.getMaxPermSize()) && ObjectUtils.nullSafeEquals(this.getMs(), memory.getMs())
            && ObjectUtils.nullSafeEquals(this.getMx(), memory.getMx()) && ObjectUtils.nullSafeEquals(this.getNewSize(), memory.getNewSize())
            && ObjectUtils.nullSafeEquals(this.getPermSize(), memory.getPermSize()) && ObjectUtils.nullSafeEquals(this.getSs(), memory.getSs());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.maxNewSize) * 29 + ObjectUtils.nullSafeHashCode(this.maxPermSize) * 29
            + ObjectUtils.nullSafeHashCode(this.ms) * 29 + ObjectUtils.nullSafeHashCode(this.mx) * 29 + ObjectUtils.nullSafeHashCode(this.newSize)
            * 29 + ObjectUtils.nullSafeHashCode(this.permSize) * 29 + ObjectUtils.nullSafeHashCode(this.ss) * 29;
    }

}
