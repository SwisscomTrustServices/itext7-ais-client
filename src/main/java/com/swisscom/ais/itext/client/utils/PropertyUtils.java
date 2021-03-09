package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;

import org.apache.commons.lang3.StringUtils;

public class PropertyUtils {

    public static String extractStringProperty(ConfigurationProvider provider, String propertyName) {
        return extractProperty(provider, propertyName);
    }

    public static int extractIntProperty(ConfigurationProvider provider, String propertyName) {
        return Integer.parseInt(extractProperty(provider, propertyName));
    }

    private static String extractProperty(ConfigurationProvider provider, String propertyName) {
        String propertyValue = provider.getProperty(propertyName);
        validateProperty(propertyName, propertyValue);
        return propertyValue;
    }

    private static void validateProperty(String propertyName, String propertyValue) {
        if (StringUtils.isBlank(propertyValue)) {
            throw new IllegalStateException(String.format("Invalid configuration. The [%s] property is missing or is empty.", propertyName));
        }
    }
}
