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
package com.swisscom.ais.itext7.client.cli;

import com.swisscom.ais.itext7.client.common.Loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

class ClientVersionProvider {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);

    private static final String PROPERTIES_RESOURCE_NAME = "/build.properties";

    private boolean isVersionInfoAvailable = false;
    private String versionInfo;

    public void initialize() {
        try (InputStream inputStream = this.getClass().getResourceAsStream(PROPERTIES_RESOURCE_NAME)) {
            if (Objects.isNull(inputStream)) {
                clientLogger.debug("No build.properties file was found in the iText AIS client JAR. Skipping version info logging");
                return;
            }
            Properties properties = new Properties();
            properties.load(inputStream);

            String version = properties.getProperty("build.version");
            String timestamp = properties.getProperty("build.timestamp");
            String gitId = properties.getProperty("build.git.id");

            StringBuilder builder = new StringBuilder();
            if (Objects.nonNull(version)) {
                builder.append("version ").append(version);
                builder.append(", built on ").append(timestamp);
            }
            if (Objects.nonNull(gitId)) {
                builder.append(", git #").append(gitId);
            }

            versionInfo = builder.toString();
            isVersionInfoAvailable = true;
        } catch (Exception ignored) {
            clientLogger.debug("Failed to load the AIS client version info from embedded build.properties file. Skipping version info logging");
        }
    }

    public boolean isVersionInfoAvailable() {
        return isVersionInfoAvailable;
    }

    public String getVersionInfo() {
        return versionInfo;
    }
}
