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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Holder for JVM configuration options
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jvm-options", propOrder = { "general", "memory", "garbageCollection", "debug", "advanced" })
public class JvmOptions implements Validator, Hierarchical<Environment> {

    private General general;

    private Memory memory;

    private GarbageCollection garbageCollection;

    private Debug debug;

    private Advanced advanced;

    private Environment parent;

    public JvmOptions() {
        general = new General();
        memory = new Memory();
        garbageCollection = new GarbageCollection();
        debug = new Debug();
        advanced = new Advanced();
    }

    @XmlElement(name = "jvm-general", required = true)
    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    @XmlElement(name = "memory", required = true)
    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    @XmlElement(name = "garbage-collection", required = true)
    public GarbageCollection getGarbageCollection() {
        return garbageCollection;
    }

    public void setGarbageCollection(GarbageCollection garbageCollection) {
        this.garbageCollection = garbageCollection;
    }

    @XmlElement(name = "debug", required = true)
    public Debug getDebug() {
        return debug;
    }

    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    @XmlElement(name = "advanced", required = true)
    public Advanced getAdvanced() {
        return advanced;
    }

    public void setAdvanced(Advanced advanced) {
        this.advanced = advanced;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        JvmOptions jvmOptions = (JvmOptions) target;
        errors.pushNestedPath("general");
        jvmOptions.getGeneral().validate(jvmOptions.getGeneral(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("memory");
        jvmOptions.getMemory().validate(jvmOptions.getMemory(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("garbageCollection");
        jvmOptions.getGarbageCollection().validate(jvmOptions.getGarbageCollection(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("debug");
        jvmOptions.getDebug().validate(jvmOptions.getDebug(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("advanced");
        jvmOptions.getAdvanced().validate(jvmOptions.getAdvanced(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        general.setParent(this);
        general.applyParentToChildren();
        memory.setParent(this);
        memory.applyParentToChildren();
        garbageCollection.setParent(this);
        garbageCollection.applyParentToChildren();
        debug.setParent(this);
        debug.applyParentToChildren();
        advanced.setParent(this);
        advanced.applyParentToChildren();
    }

    public Environment parent() {
        return parent;
    }

    public void setParent(Environment parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JvmOptions)) {
            return false;
        }
        JvmOptions jvmOptions = (JvmOptions) obj;
        return ObjectUtils.nullSafeEquals(this.getAdvanced(), jvmOptions.getAdvanced())
            && ObjectUtils.nullSafeEquals(this.getDebug(), jvmOptions.getDebug())
            && ObjectUtils.nullSafeEquals(this.getGarbageCollection(), jvmOptions.getGarbageCollection())
            && ObjectUtils.nullSafeEquals(this.getGeneral(), jvmOptions.getGeneral())
            && ObjectUtils.nullSafeEquals(this.getMemory(), jvmOptions.getMemory());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.advanced) * 29 + ObjectUtils.nullSafeHashCode(this.debug) * 29
            + ObjectUtils.nullSafeHashCode(this.garbageCollection) * 29 + ObjectUtils.nullSafeHashCode(this.general) * 29
            + ObjectUtils.nullSafeHashCode(this.memory) * 29;
    }

}
