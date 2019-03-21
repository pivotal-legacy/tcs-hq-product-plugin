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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.util.List;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.DefaultJvmOptionsConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment.JvmOptionsConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;
import com.thoughtworks.xstream.XStream;

public class DefaultServerConfigManager implements ServerConfigManager {

    private final SettingsFactory settingsFactory = new FileReadingSettingsFactory();

    private final JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    private final XStream xstream = new XStream();

    private final SettingsRepository settingsRepository;

    public DefaultServerConfigManager() throws PluginException {
        this.settingsRepository = new FileSettingsRepository();
    }

    public Settings getConfiguration(ConfigResponse config) throws PluginException {
        return settingsFactory.create(config);
    }

    public void saveConfiguration(ConfigResponse config) throws PluginException {
        Settings settings;
        try {
            settings = (Settings) xstream.fromXML(config.getValue("SETTINGS"));
        } catch (Exception e) {
            throw new PluginException("Unable to read settings to save.  Cause: " + e.getMessage());
        }
        settingsRepository.saveConfiguration(config, settings);
    }

    public void revertToPreviousConfiguration(ConfigResponse config) throws PluginException {
        settingsRepository.revertToPreviousConfiguration(config);
    }

    public void putFile(ConfigResponse config) throws PluginException {
        settingsRepository.putFile(config);
    }

    public void prepareFile(ConfigResponse config) throws PluginException {
        settingsRepository.prepareFile(config);
    }

    public String getFile(ConfigResponse config) throws PluginException {
        return settingsFactory.getFile(config);
    }

    public void copyFile(ConfigResponse config) throws PluginException {
        settingsRepository.copyFile(config);
    }

    public boolean fileExists(ConfigResponse config) throws PluginException {
        return settingsRepository.fileExists(config);
    }

    @SuppressWarnings("unchecked")
    public void putJvmOpts(ConfigResponse config) throws PluginException {
        List<String> strJvmOpts;
        try {
            strJvmOpts = (List<String>) xstream.fromXML(config.getValue("JVM_OPTS"));
        } catch (Exception e) {
            throw new PluginException("Unable to read settings to save.  Cause: " + e.getMessage());
        }
        Environment environment = settingsFactory.createEnvironment(config);
        JvmOptions jvmOptions = jvmOptionsConverter.convert(strJvmOpts);
        environment.setJvmOptions(jvmOptions);
        settingsRepository.saveEnvironment(config, environment);
    }

    public List<String> getJvmOpts(ConfigResponse config) throws PluginException {
        Environment environment = settingsFactory.createEnvironment(config);
        return jvmOptionsConverter.convert(environment.getJvmOptions());
    }

}
