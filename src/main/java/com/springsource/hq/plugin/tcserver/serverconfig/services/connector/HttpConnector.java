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

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Settings for HTTP based connectors. (https://tomcat.apache.org/tomcat-6.0-doc/config/http.html)
 * 
 * <p>
 * Java, APR and NIO HTTP connectors are supported. SSL options are supported for Java and APR.
 * 
 * @author Scott Andrews
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "http-connector")
public class HttpConnector extends Connector implements Validator {

    private static final long DEFAULT_ACCEPT_COUNT = 100l;

    private static final long DEFAULT_MAX_KEEP_ALIVE_REQUESTS = 100l;

    private static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11Protocol";

    private static final boolean DEFAULT_SECURE = false;

    private static final boolean DEFAULT_SSL_ENABLED = false;

    private static final String DEFAULT_SSL_PROTOCOL = "all";

    private static final String DEFAULT_SSL_CIPHER_SUITE = "ALL";

    private static final String DEFAULT_SSL_VERIFY_CLIENT = "none";

    private static final long DEFAULT_SSL_VERIFY_DEPTH = 10l;

    /**
     * The maximum queue length for incoming connection requests when all possible request processing threads are in
     * use. Any requests received when the queue is full will be refused. The default value is 100 (per Filip).
     */
    private Long acceptCount = DEFAULT_ACCEPT_COUNT;

    /**
     * The certificate encoding algorithm to be used. If not specified, this defaults to the Sun implementation
     * (SunX509). For IBM JVMs you should use the value IbmX509. For other vendors, consult the JVM documentation for
     * the correct value.
     */
    private String algorithm;

    /**
     * The alias used to for the server certificate in the keystore. If not specified the first key read in the keystore
     * will be used.
     */
    private String keyAlias;

    /**
     * The pathname of the keystore file where you have stored the server certificate to be loaded. If not specified,
     * the pathname is the file ".keystore" in the operating system home directory of the user that is running tc
     * Runtime. If your keystoreType doesn't need a file use "" (empty string) for this parameter.
     */
    private String keystoreFile;

    /**
     * The password used to access the server certificate from the specified keystore file. If not specified, the
     * default value is "changeit".
     */
    private String keystorePass;

    /**
     * The maximum number of HTTP requests which can be pipelined until the connection is closed by the server. Setting
     * this attribute to 1 will disable HTTP/1.0 keep-alive, as well as HTTP/1.1 keep-alive and pipelining. Setting this
     * to -1 will allow an unlimited amount of pipelined or keep-alive HTTP requests. If not specified, this attribute
     * is set to 100.
     */
    private Long maxKeepAliveRequests = DEFAULT_MAX_KEEP_ALIVE_REQUESTS;

    /**
     * Sets the protocol to handle incoming traffic. The default value is HTTP/1.1 and configures the
     * org.apache.coyote.http11.Http11Protocol. This is the blocking Java connector. If the PATH(Windows) or
     * LD_LIBRARY_PATH(on most unix system) environment variables contain the Tomcat native library, the APR connector
     * will automatically be configured. Please be advised that the APR connector has different settings for HTTPS than
     * the default Java connector. Other values for this attribute are, but not limited to:
     * org.apache.coyote.http11.Http11Protocol - same as HTTP/1.1 org.apache.coyote.http11.Http11NioProtocol - non
     * blocking Java connector org.apache.coyote.http11.Http11AprProtocol - the APR connector. Take a look at our
     * Connector Comparison chart. The configuration for both Java connectors are identical, both for http and https.
     * For more information on the APR connector and APR specific SSL settings please visit the APR documentation
     */
    private String protocol = DEFAULT_PROTOCOL;

    /**
     * Set this attribute to true if you wish to have calls to request.isSecure() to return true for requests received
     * by this Connector. You would want this on an SSL Connector or a non SSL connector that is receiving data from a
     * SSL accelerator, like a crypto card, a SSL appliance or even a webserver. The default value is false.
     */
    private Boolean secure = DEFAULT_SECURE;

    /**
     * Use this attribute to enable SSL traffic on a connector. To turn on SSL handshake/encryption/decryption on a
     * connector set this value to true. The default value is false. When turning this value true you will want to set
     * the scheme and the secure attributes as well to pass the correct request.getScheme() and request.isSecure()
     * values to the servlets See SSL Support for more information.
     */
    private Boolean SSLEnabled = DEFAULT_SSL_ENABLED;

    // APR SSL Settings

    /**
     * Protocol which may be used for communicating with clients. The default is "all", with other acceptable values
     * being "SSLv2", "SSLv3", "TLSv1", and "SSLv2+SSLv3".
     */
    private String SSLProtocol = DEFAULT_SSL_PROTOCOL;

    /**
     * Ciphers which may be used for communicating with clients. The default is "ALL", with other acceptable values
     * being a list of ciphers, with ":" used as the delimiter (see OpenSSL documentation for the list of ciphers
     * supported).
     */
    private String SSLCipherSuite = DEFAULT_SSL_CIPHER_SUITE;

    /**
     * Name of the file that contains the server certificate. The format is PEM-encoded.
     */
    private String SSLCertificateFile;

    /**
     * Name of the file that contains the server private key. The format is PEM-encoded. The default value is the value
     * of "SSLCertificateFile" and in this case both certificate and private key have to be in this file (NOT
     * RECOMMENDED).
     */
    private String SSLCertificateKeyFile;

    /**
     * Pass phrase for the encrypted private key. If "SSLPassword" is not provided, the callback fonction should prompt
     * for the pass phrase.
     */
    private String SSLPassword;

    /**
     * Ask client for certificate. The default is "none", meaning the client will not have the opportunity to submit a
     * certificate. Other acceptable values include "optional", "require" and "optionalNoCA".
     */
    private String SSLVerifyClient = DEFAULT_SSL_VERIFY_CLIENT;

    /**
     * Maximum verification depth for client certificates. The default is "10".
     */
    private Long SSLVerifyDepth = DEFAULT_SSL_VERIFY_DEPTH;

    /**
     * See the mod_ssl documentation.
     */
    private String SSLCACertificateFile;

    /**
     * See the mod_ssl documentation.
     */
    private String SSLCACertificatePath;

    /**
     * See the mod_ssl documentation.
     */
    private String SSLCertificateChainFile;

    /**
     * See the mod_ssl documentation.
     */
    private String SSLCARevocationFile;

    /**
     * See the mod_ssl documentation.
     */
    private String SSLCARevocationPath;

    @XmlAttribute(name = "accept-count")
    public Long getAcceptCount() {
        return acceptCount;
    }

    @XmlAttribute(name = "algorithm")
    public String getAlgorithm() {
        return algorithm;
    }

    @XmlAttribute(name = "key-alias")
    public String getKeyAlias() {
        return keyAlias;
    }

    @XmlAttribute(name = "keystore-file")
    public String getKeystoreFile() {
        return keystoreFile;
    }

    @XmlAttribute(name = "keystore-pass")
    public String getKeystorePass() {
        return keystorePass;
    }

    @XmlAttribute(name = "max-keep-alive-requests")
    public Long getMaxKeepAliveRequests() {
        return maxKeepAliveRequests;
    }

    @XmlAttribute(name = "protocol")
    public String getProtocol() {
        return protocol;
    }

    @XmlAttribute(name = "secure")
    public Boolean getSecure() {
        return secure;
    }

    @XmlAttribute(name = "ssl-enabled")
    public Boolean getSSLEnabled() {
        return SSLEnabled;
    }

    public void setAcceptCount(Long acceptCount) {
        this.acceptCount = acceptCount;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public void setMaxKeepAliveRequests(Long maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public void setSSLEnabled(Boolean enabled) {
        SSLEnabled = enabled;
    }

    // APR SSL settings

    @XmlAttribute(name = "ssl-protocol")
    public String getSSLProtocol() {
        return SSLProtocol;
    }

    public void setSSLProtocol(String protocol) {
        SSLProtocol = protocol;
    }

    @XmlAttribute(name = "ssl-cipher-suite")
    public String getSSLCipherSuite() {
        return SSLCipherSuite;
    }

    public void setSSLCipherSuite(String cipherSuite) {
        SSLCipherSuite = cipherSuite;
    }

    @XmlAttribute(name = "ssl-certificate-file")
    public String getSSLCertificateFile() {
        return SSLCertificateFile;
    }

    public void setSSLCertificateFile(String certificateFile) {
        SSLCertificateFile = certificateFile;
    }

    @XmlAttribute(name = "ssl-certificate-key-file")
    public String getSSLCertificateKeyFile() {
        return SSLCertificateKeyFile;
    }

    public void setSSLCertificateKeyFile(String certificateKeyFile) {
        SSLCertificateKeyFile = certificateKeyFile;
    }

    @XmlAttribute(name = "ssl-password")
    public String getSSLPassword() {
        return SSLPassword;
    }

    public void setSSLPassword(String password) {
        SSLPassword = password;
    }

    @XmlAttribute(name = "ssl-verify-client")
    public String getSSLVerifyClient() {
        return SSLVerifyClient;
    }

    public void setSSLVerifyClient(String verifyClient) {
        SSLVerifyClient = verifyClient;
    }

    @XmlAttribute(name = "ssl-verify-depth")
    public Long getSSLVerifyDepth() {
        return SSLVerifyDepth;
    }

    public void setSSLVerifyDepth(Long verifyDepth) {
        SSLVerifyDepth = verifyDepth;
    }

    @XmlAttribute(name = "ssl-ca-certificate-file")
    public String getSSLCACertificateFile() {
        return SSLCACertificateFile;
    }

    public void setSSLCACertificateFile(String certificateFile) {
        SSLCACertificateFile = certificateFile;
    }

    @XmlAttribute(name = "ssl-ca-certificate-path")
    public String getSSLCACertificatePath() {
        return SSLCACertificatePath;
    }

    public void setSSLCACertificatePath(String certificatePath) {
        SSLCACertificatePath = certificatePath;
    }

    @XmlAttribute(name = "ssl-certificate-chain-file")
    public String getSSLCertificateChainFile() {
        return SSLCertificateChainFile;
    }

    public void setSSLCertificateChainFile(String certificateChainFile) {
        SSLCertificateChainFile = certificateChainFile;
    }

    @XmlAttribute(name = "ssl-ca-revocation-file")
    public String getSSLCARevocationFile() {
        return SSLCARevocationFile;
    }

    public void setSSLCARevocationFile(String revocationFile) {
        SSLCARevocationFile = revocationFile;
    }

    @XmlAttribute(name = "ssl-ca-revocation-path")
    public String getSSLCARevocationPath() {
        return SSLCARevocationPath;
    }

    public void setSSLCARevocationPath(String revocationPath) {
        SSLCARevocationPath = revocationPath;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HttpConnector httpConnector = (HttpConnector) target;
        super.validate(httpConnector, errors);
        if (!errors.hasFieldErrors("acceptCount")) {
            if (httpConnector.getAcceptCount() != null) {
                if (httpConnector.getAcceptCount() < 0) {
                    errors.rejectValue("acceptCount", "service.connector.http.acceptCount.tooLow");
                }
            }
        }
        if (!errors.hasFieldErrors("maxKeepAliveRequests")) {
            if (httpConnector.getMaxKeepAliveRequests() != null) {
                if (httpConnector.getMaxKeepAliveRequests() < -1) {
                    errors.rejectValue("maxKeepAliveRequests", "service.connector.http.maxKeepAliveRequests.tooLow");
                }
            }
        }
        if (!errors.hasFieldErrors("SSLVerifyDepth")) {
            if (httpConnector.getSSLVerifyDepth() != null) {
                if (httpConnector.getSSLVerifyDepth() < 0) {
                    errors.rejectValue("SSLVerifyDepth", "service.connector.http.SSLVerifyDepth.tooLow");
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HttpConnector)) {
            return false;
        }
        HttpConnector connector = (HttpConnector) obj;
        return ObjectUtils.nullSafeEquals(this.getAcceptCount(), connector.getAcceptCount())
            && ObjectUtils.nullSafeEquals(this.getAlgorithm(), connector.getAlgorithm())
            && ObjectUtils.nullSafeEquals(this.getKeyAlias(), connector.getKeyAlias())
            && ObjectUtils.nullSafeEquals(this.getKeystoreFile(), connector.getKeystoreFile())
            && ObjectUtils.nullSafeEquals(this.getKeystorePass(), connector.getKeystorePass())
            && ObjectUtils.nullSafeEquals(this.getMaxKeepAliveRequests(), connector.getMaxKeepAliveRequests())
            && ObjectUtils.nullSafeEquals(this.getProtocol(), connector.getProtocol())
            && ObjectUtils.nullSafeEquals(this.getSecure(), connector.getSecure())
            && ObjectUtils.nullSafeEquals(this.getSSLEnabled(), connector.getSSLEnabled())
            && ObjectUtils.nullSafeEquals(this.getSSLCACertificateFile(), connector.getSSLCACertificateFile())
            && ObjectUtils.nullSafeEquals(this.getSSLCACertificatePath(), connector.getSSLCACertificatePath())
            && ObjectUtils.nullSafeEquals(this.getSSLCARevocationFile(), connector.getSSLCARevocationFile())
            && ObjectUtils.nullSafeEquals(this.getSSLCARevocationPath(), connector.getSSLCARevocationPath())
            && ObjectUtils.nullSafeEquals(this.getSSLCertificateChainFile(), connector.getSSLCertificateChainFile())
            && ObjectUtils.nullSafeEquals(this.getSSLCertificateFile(), connector.getSSLCertificateFile())
            && ObjectUtils.nullSafeEquals(this.getSSLCertificateKeyFile(), connector.getSSLCertificateKeyFile())
            && ObjectUtils.nullSafeEquals(this.getSSLCipherSuite(), connector.getSSLCipherSuite())
            && ObjectUtils.nullSafeEquals(this.getSSLPassword(), connector.getSSLPassword())
            && ObjectUtils.nullSafeEquals(this.getSSLProtocol(), connector.getSSLProtocol())
            && ObjectUtils.nullSafeEquals(this.getSSLVerifyClient(), connector.getSSLVerifyClient())
            && ObjectUtils.nullSafeEquals(this.getSSLVerifyDepth(), connector.getSSLVerifyDepth()) && super.equals(connector);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.acceptCount) * 29 + ObjectUtils.nullSafeHashCode(this.algorithm) * 29
            + ObjectUtils.nullSafeHashCode(this.keyAlias) * 29 + ObjectUtils.nullSafeHashCode(this.keystoreFile) * 29
            + ObjectUtils.nullSafeHashCode(this.keystorePass) * 29 + ObjectUtils.nullSafeHashCode(this.maxKeepAliveRequests) * 29
            + ObjectUtils.nullSafeHashCode(this.protocol) * 29 + ObjectUtils.nullSafeHashCode(this.secure) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLEnabled) * 29 + ObjectUtils.nullSafeHashCode(this.SSLCACertificateFile) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLCACertificatePath) * 29 + ObjectUtils.nullSafeHashCode(this.SSLCARevocationFile) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLCARevocationPath) * 29 + ObjectUtils.nullSafeHashCode(this.SSLCertificateChainFile) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLCertificateFile) * 29 + ObjectUtils.nullSafeHashCode(this.SSLCertificateKeyFile) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLCipherSuite) * 29 + ObjectUtils.nullSafeHashCode(this.SSLPassword) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLProtocol) * 29 + ObjectUtils.nullSafeHashCode(this.SSLVerifyClient) * 29
            + ObjectUtils.nullSafeHashCode(this.SSLVerifyDepth) * 29 + super.hashCode() * 29;
    }

}
