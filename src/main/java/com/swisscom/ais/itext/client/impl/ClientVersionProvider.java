package com.swisscom.ais.itext.client.impl;

import com.swisscom.ais.itext.client.common.Loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ClientVersionProvider {

    private static final Logger log = LoggerFactory.getLogger(Loggers.CLIENT);

    private static final String PROPERTIES_RESOURCE_NAME = "/build.properties";

    private boolean isVersionInfoAvailable = false;
    private String versionInfo;

    public void init() {
        try (InputStream inputStream = this.getClass().getResourceAsStream(PROPERTIES_RESOURCE_NAME)) {
            if (Objects.isNull(inputStream)) {
                log.debug("No build.properties file was found in the iText AIS client JAR. Skipping version info logging");
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
            log.debug("Failed to load the AIS client version info from embedded build.properties file. Skipping version info logging");
        }
    }

    public boolean isVersionInfoAvailable() {
        return isVersionInfoAvailable;
    }

    public String getVersionInfo() {
        return versionInfo;
    }
}
