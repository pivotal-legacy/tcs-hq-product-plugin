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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for JSP defaults (http://svn.apache.org/viewvc/tomcat/tc6.0.x/trunk/conf/web.xml?view=markup)
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jsp-defaults")
public class JspDefaults implements Validator, Hierarchical<ServerDefaults> {

    private static final long DEFAULT_CHECK_INTERVAL = 0l;

    private static final boolean DEFAULT_CLASS_DEBUG_INFO = true;

    private static final boolean DEFAULT_DEVELOPMENT = true;

    private static final boolean DEFAULT_DISPLAY_SOURCE_FRAGMENT = true;

    private static final boolean DEFAULT_DUMP_SMAP = false;

    private static final boolean DEFAULT_ENABLE_POOLING = true;

    private static final boolean DEFAULT_ERROR_ON_USE_BEAN_INVALID_CLASS = true;

    private static final boolean DEFAULT_FORK = true;

    private static final boolean DEFAULT_GEN_STR_AS_CHAR_ARRAY = false;

    private static final String DEFAULT_IE_CLASS_ID = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

    private static final String DEFAULT_JAVA_ENCODING = "UTF8";

    private static final boolean DEFAULT_KEEP_GENERATED = true;

    private static final boolean DEFAULT_MAPPED_FILE = true;

    private static final long DEFAULT_MODIFICATION_TEST_INTERVAL = 4l;

    private static final boolean DEFAULT_SUPPRESS_SMAP = false;

    private static final boolean DEFAULT_TRIM_SPACES = false;

    private static final boolean DEFAULT_X_POWERED_BY = false;

    /**
     * If development is false and checkInterval is greater than zero, background compilations are enabled.
     * checkInterval is the time in seconds between checks to see if a JSP page (and its dependent files) needs to be
     * recompiled. [0]
     */
    private Long checkInterval = DEFAULT_CHECK_INTERVAL;

    /**
     * Should the class file be compiled with debugging information? [true]
     */
    private Boolean classdebuginfo = DEFAULT_CLASS_DEBUG_INFO;

    /**
     * Which compiler Ant should use to compile JSP pages. See the jasper documentation for more information.
     */
    private String compiler;

    /**
     * Compiler source VM default is System.properties java.specification.version > 1.4 [1.5] else [1.4]
     */
    private String compilerSourceVM;

    /**
     * Compiler target VM default is System.properties java.specification.version > 1.4 [1.5] else [1.4]
     */
    private String compilerTargetVM;

    /**
     * Is Jasper used in development mode? If true, the frequency at which JSPs are checked for modification may be
     * specified via the modificationTestInterval parameter. [true]
     */
    private Boolean development = DEFAULT_DEVELOPMENT;

    /**
     * Should a source fragment be included in exception messages? [true]
     */
    private Boolean displaySourceFragment = DEFAULT_DISPLAY_SOURCE_FRAGMENT;

    /**
     * Should the SMAP info for JSR45 debugging be dumped to a file? [false] False if suppressSmap is true
     */
    private Boolean dumpSmap = DEFAULT_DUMP_SMAP;

    /**
     * Determines whether tag handler pooling is enabled [true]
     */
    private Boolean enablePooling = DEFAULT_ENABLE_POOLING;

    /**
     * Allows specifying the Options class used to configure Jasper. If not present, the default EmbeddedServletOptions
     * will be used.
     */
    private String engineOptionsClass;

    /**
     * Should Jasper issue an error when the value of the class attribute in an useBean action is not a valid bean
     * class? [true]
     */
    private Boolean errorOnUseBeanInvalidClassAttribute = DEFAULT_ERROR_ON_USE_BEAN_INVALID_CLASS;

    /**
     * Tell Ant to fork compiles of JSP pages so that a separate JVM is used for JSP page compiles from the one tc Runtime
     * is running in. [true]
     */
    private Boolean fork = DEFAULT_FORK;

    /**
     * Should text strings be generated as char arrays, to improve performance in some cases? [false]
     */
    private Boolean genStrAsCharArray = DEFAULT_GEN_STR_AS_CHAR_ARRAY;

    /**
     * The class-id value to be sent to Internet Explorer when using <jsp:plugin> tags.
     * [clsid:8AD9C840-044E-11D1-B3E9-00805F499D93]
     */
    private String ieClassId = DEFAULT_IE_CLASS_ID;

    /**
     * Java file encoding to use for generating java source files. [UTF8]
     */
    private String javaEncoding = DEFAULT_JAVA_ENCODING;

    /**
     * Should we keep the generated Java source code for each page instead of deleting it? [true]
     */
    private Boolean keepgenerated = DEFAULT_KEEP_GENERATED;

    /**
     * Should we generate static content with one print statement per input line, to ease debugging? [true]
     */
    private Boolean mappedfile = DEFAULT_MAPPED_FILE;

    /**
     * Causes a JSP (and its dependent files) to not be checked for modification during the specified time interval (in
     * seconds) from the last time the JSP was checked for modification. A value of 0 will cause the JSP to be checked
     * on every access Used in development mode only. [4]
     */
    private Long modificationTestInterval = DEFAULT_MODIFICATION_TEST_INTERVAL;

    private ServerDefaults parent;

    /**
     * What scratch directory should we use when compiling JSP pages? [default work directory for the current web
     * application]
     */
    private String scratchdir;

    /**
     * Should the generation of SMAP info for JSR45 debugging be suppressed? [false]
     */
    private Boolean suppressSmap = DEFAULT_SUPPRESS_SMAP;

    /**
     * Should white spaces in template text between actions or directives be trimmed? [false]
     */
    private Boolean trimSpaces = DEFAULT_TRIM_SPACES;

    /**
     * Determines whether X-Powered-By response header is added by generated servlet [false]
     */
    private Boolean xpoweredBy = DEFAULT_X_POWERED_BY;

    public void applyParentToChildren() {
        // no-op, no children
    }

    @XmlAttribute(name = "check-interval")
    public Long getCheckInterval() {
        return checkInterval;
    }

    @XmlAttribute(name = "classdebuginfo")
    public Boolean getClassdebuginfo() {
        return classdebuginfo;
    }

    @XmlAttribute(name = "compiler")
    public String getCompiler() {
        return compiler;
    }

    @XmlAttribute(name = "compiler-source-vm")
    public String getCompilerSourceVM() {
        return compilerSourceVM;
    }

    @XmlAttribute(name = "compiler-target-vm")
    public String getCompilerTargetVM() {
        return compilerTargetVM;
    }

    @XmlAttribute(name = "development")
    public Boolean getDevelopment() {
        return development;
    }

    @XmlAttribute(name = "display-source-fragment")
    public Boolean getDisplaySourceFragment() {
        return displaySourceFragment;
    }

    @XmlAttribute(name = "dump-smap")
    public Boolean getDumpSmap() {
        return dumpSmap;
    }

    @XmlAttribute(name = "enable-pooling")
    public Boolean getEnablePooling() {
        return enablePooling;
    }

    @XmlAttribute(name = "engine-options-class")
    public String getEngineOptionsClass() {
        return engineOptionsClass;
    }

    @XmlAttribute(name = "error-on-use-bean-invalid-class-attribute")
    public Boolean getErrorOnUseBeanInvalidClassAttribute() {
        return errorOnUseBeanInvalidClassAttribute;
    }

    @XmlAttribute(name = "fork")
    public Boolean getFork() {
        return fork;
    }

    @XmlAttribute(name = "gen-str-as-char-array")
    public Boolean getGenStrAsCharArray() {
        return genStrAsCharArray;
    }

    @XmlAttribute(name = "ie-class-id")
    public String getIeClassId() {
        return ieClassId;
    }

    @XmlAttribute(name = "java-encoding")
    public String getJavaEncoding() {
        return javaEncoding;
    }

    @XmlAttribute(name = "keepgenerated")
    public Boolean getKeepgenerated() {
        return keepgenerated;
    }

    @XmlAttribute(name = "mappedfile")
    public Boolean getMappedfile() {
        return mappedfile;
    }

    @XmlAttribute(name = "modification-test-interval")
    public Long getModificationTestInterval() {
        return modificationTestInterval;
    }

    @XmlAttribute(name = "scratchdir")
    public String getScratchdir() {
        return scratchdir;
    }

    @XmlAttribute(name = "suppress-smap")
    public Boolean getSuppressSmap() {
        return suppressSmap;
    }

    @XmlAttribute(name = "trim-spaces")
    public Boolean getTrimSpaces() {
        return trimSpaces;
    }

    @XmlAttribute(name = "xpowered-by")
    public Boolean getXpoweredBy() {
        return xpoweredBy;
    }

    public ServerDefaults parent() {
        return parent;
    }

    public void setCheckInterval(Long checkInterval) {
        this.checkInterval = checkInterval;
    }

    public void setClassdebuginfo(Boolean classdebuginfo) {
        this.classdebuginfo = classdebuginfo;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public void setCompilerSourceVM(String compilerSourceVM) {
        this.compilerSourceVM = compilerSourceVM;
    }

    public void setCompilerTargetVM(String compilerTargetVM) {
        this.compilerTargetVM = compilerTargetVM;
    }

    public void setDevelopment(Boolean development) {
        this.development = development;
    }

    public void setDisplaySourceFragment(Boolean displaySourceFragment) {
        this.displaySourceFragment = displaySourceFragment;
    }

    public void setDumpSmap(Boolean dumpSmap) {
        this.dumpSmap = dumpSmap;
    }

    public void setEnablePooling(Boolean enablePooling) {
        this.enablePooling = enablePooling;
    }

    public void setEngineOptionsClass(String engineOptionsClass) {
        this.engineOptionsClass = engineOptionsClass;
    }

    public void setErrorOnUseBeanInvalidClassAttribute(Boolean errorOnUseBeanInvalidClassAttribute) {
        this.errorOnUseBeanInvalidClassAttribute = errorOnUseBeanInvalidClassAttribute;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }

    public void setGenStrAsCharArray(Boolean genStrAsCharArray) {
        this.genStrAsCharArray = genStrAsCharArray;
    }

    public void setIeClassId(String ieClassId) {
        this.ieClassId = ieClassId;
    }

    public void setJavaEncoding(String javaEncoding) {
        this.javaEncoding = javaEncoding;
    }

    public void setKeepgenerated(Boolean keepgenerated) {
        this.keepgenerated = keepgenerated;
    }

    public void setMappedfile(Boolean mappedfile) {
        this.mappedfile = mappedfile;
    }

    public void setModificationTestInterval(Long modificationTestInterval) {
        this.modificationTestInterval = modificationTestInterval;
    }

    public void setParent(ServerDefaults parent) {
        this.parent = parent;
    }

    public void setScratchdir(String scratchdir) {
        this.scratchdir = scratchdir;
    }

    public void setSuppressSmap(Boolean suppressSmap) {
        this.suppressSmap = suppressSmap;
    }

    public void setTrimSpaces(Boolean trimSpaces) {
        this.trimSpaces = trimSpaces;
    }

    public void setXpoweredBy(Boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // JspDefaults jspDefaults = (JspDefaults) target;
        // TODO define validation rules
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JspDefaults)) {
            return false;
        }
        JspDefaults jspDefaults = (JspDefaults) obj;
        return ObjectUtils.nullSafeEquals(this.getCheckInterval(), jspDefaults.getCheckInterval())
            && ObjectUtils.nullSafeEquals(this.getClassdebuginfo(), jspDefaults.getClassdebuginfo())
            && ObjectUtils.nullSafeEquals(this.getCompiler(), jspDefaults.getCompiler())
            && ObjectUtils.nullSafeEquals(this.getCompilerSourceVM(), jspDefaults.getCompilerSourceVM())
            && ObjectUtils.nullSafeEquals(this.getCompilerTargetVM(), jspDefaults.getCompilerTargetVM())
            && ObjectUtils.nullSafeEquals(this.getDevelopment(), jspDefaults.getDevelopment())
            && ObjectUtils.nullSafeEquals(this.getDisplaySourceFragment(), jspDefaults.getDisplaySourceFragment())
            && ObjectUtils.nullSafeEquals(this.getDumpSmap(), jspDefaults.getDumpSmap())
            && ObjectUtils.nullSafeEquals(this.getEnablePooling(), jspDefaults.getEnablePooling())
            && ObjectUtils.nullSafeEquals(this.getEngineOptionsClass(), jspDefaults.getEngineOptionsClass())
            && ObjectUtils.nullSafeEquals(this.getErrorOnUseBeanInvalidClassAttribute(), jspDefaults.getErrorOnUseBeanInvalidClassAttribute())
            && ObjectUtils.nullSafeEquals(this.getFork(), jspDefaults.getFork())
            && ObjectUtils.nullSafeEquals(this.getGenStrAsCharArray(), jspDefaults.getGenStrAsCharArray())
            && ObjectUtils.nullSafeEquals(this.getIeClassId(), jspDefaults.getIeClassId())
            && ObjectUtils.nullSafeEquals(this.getJavaEncoding(), jspDefaults.getJavaEncoding())
            && ObjectUtils.nullSafeEquals(this.getKeepgenerated(), jspDefaults.getKeepgenerated())
            && ObjectUtils.nullSafeEquals(this.getMappedfile(), jspDefaults.getMappedfile())
            && ObjectUtils.nullSafeEquals(this.getModificationTestInterval(), jspDefaults.getModificationTestInterval())
            && ObjectUtils.nullSafeEquals(this.getScratchdir(), jspDefaults.getScratchdir())
            && ObjectUtils.nullSafeEquals(this.getSuppressSmap(), jspDefaults.getSuppressSmap())
            && ObjectUtils.nullSafeEquals(this.getTrimSpaces(), jspDefaults.getTrimSpaces())
            && ObjectUtils.nullSafeEquals(this.getXpoweredBy(), jspDefaults.getXpoweredBy());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.checkInterval) * 29 + ObjectUtils.nullSafeHashCode(this.classdebuginfo) * 29
            + ObjectUtils.nullSafeHashCode(this.compiler) * 29 + ObjectUtils.nullSafeHashCode(this.compilerSourceVM) * 29
            + ObjectUtils.nullSafeHashCode(this.compilerTargetVM) * 29 + ObjectUtils.nullSafeHashCode(this.development) * 29
            + ObjectUtils.nullSafeHashCode(this.displaySourceFragment) * 29 + ObjectUtils.nullSafeHashCode(this.dumpSmap) * 29
            + ObjectUtils.nullSafeHashCode(this.enablePooling) * 29 + ObjectUtils.nullSafeHashCode(this.engineOptionsClass) * 29
            + ObjectUtils.nullSafeHashCode(this.errorOnUseBeanInvalidClassAttribute) * 29 + ObjectUtils.nullSafeHashCode(this.fork) * 29
            + ObjectUtils.nullSafeHashCode(this.genStrAsCharArray) * 29 + ObjectUtils.nullSafeHashCode(this.ieClassId) * 29
            + ObjectUtils.nullSafeHashCode(this.javaEncoding) * 29 + ObjectUtils.nullSafeHashCode(this.keepgenerated) * 29
            + ObjectUtils.nullSafeHashCode(this.mappedfile) * 29 + ObjectUtils.nullSafeHashCode(this.modificationTestInterval) * 29
            + ObjectUtils.nullSafeHashCode(this.scratchdir) * 29 + ObjectUtils.nullSafeHashCode(this.suppressSmap) * 29
            + ObjectUtils.nullSafeHashCode(this.trimSpaces) * 29 + ObjectUtils.nullSafeHashCode(this.xpoweredBy) * 29;
    }

}
