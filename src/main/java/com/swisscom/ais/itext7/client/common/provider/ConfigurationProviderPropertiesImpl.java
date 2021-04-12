/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext7.client.common.provider;

import java.util.Properties;

/**
 * Implementation of {@link ConfigurationProvider} that takes the requested properties from a standard Java {@link Properties} store.
 */
public class ConfigurationProviderPropertiesImpl implements ConfigurationProvider {

    private final Properties properties;

    public ConfigurationProviderPropertiesImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

}
