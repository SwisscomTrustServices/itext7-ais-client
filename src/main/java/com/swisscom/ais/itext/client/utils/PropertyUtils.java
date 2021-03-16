package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;

import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

public class PropertyUtils {

    public static Properties loadPropertiesFromFile(String filepath) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(filepath));
            return properties;
        } catch (IOException e) {
            throw new AisClientException(String.format("Failed to load data properties from the file: %s", filepath), e);
        }
    }

    public static Properties loadPropertiesFromClasspathFile(Class<?> clazz, String filepath) {
        try {
            Properties properties = new Properties();
            properties.load(clazz.getResourceAsStream(filepath));
            return properties;
        } catch (IOException e) {
            throw new AisClientException(String.format("Failed to load data properties from the classpath file: %s", filepath), e);
        }
    }

    public static String extractStringProperty(ConfigurationProvider provider, String propertyName) {
        return extractProperty(provider, propertyName);
    }

    public static int extractIntProperty(ConfigurationProvider provider, String propertyName) {
        return Integer.parseInt(extractProperty(provider, propertyName));
    }

    public static <T> T extractProperty(ConfigurationProvider provider, String propertyName, Function<String, T> mapperFunction, T defaultValue) {
        String propertyValue = provider.getProperty(propertyName);
        return StringUtils.isNotBlank(propertyValue) ? mapperFunction.apply(propertyValue) : defaultValue;
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
