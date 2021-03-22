package com.swisscom.ais.itext.client.common;

import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

public abstract class PropertiesLoader {

    public abstract void setFromConfigurationProvider(ConfigurationProvider provider);

    public void setFromPropertiesClasspathFile(String fileName) {
        setFromProperties(loadPropertiesFromClasspathFile(this.getClass(), fileName));
    }

    public void setFromPropertiesFile(String fileName) {
        setFromProperties(PropertyUtils.loadPropertiesFromFile(fileName));
    }

    public void setFromProperties(Properties properties) {
        setFromConfigurationProvider(new ConfigurationProviderPropertiesImpl(properties));
    }

    public Properties loadPropertiesFromClasspathFile(Class<?> clazz, String filepath) {
        try {
            Properties properties = new Properties();
            properties.load(clazz.getResourceAsStream(filepath));
            return properties;
        } catch (IOException e) {
            throw new AisClientException(String.format("Failed to load data properties from the classpath file: %s", filepath), e);
        }
    }

    public String extractStringProperty(ConfigurationProvider provider, String propertyName) {
        return extractProperty(provider, propertyName);
    }

    public int extractIntProperty(ConfigurationProvider provider, String propertyName) {
        return Integer.parseInt(extractProperty(provider, propertyName));
    }

    public <T> T extractProperty(ConfigurationProvider provider, String propertyName, Function<String, T> mapperFunction, T defaultValue) {
        String propertyValue = provider.getProperty(propertyName);
        return StringUtils.isNotBlank(propertyValue) ? mapperFunction.apply(propertyValue) : defaultValue;
    }

    private String extractProperty(ConfigurationProvider provider, String propertyName) {
        String propertyValue = provider.getProperty(propertyName);
        validateProperty(propertyName, propertyValue);
        return propertyValue;
    }

    private void validateProperty(String propertyName, String propertyValue) {
        if (StringUtils.isBlank(propertyValue)) {
            throw new IllegalStateException(String.format("Invalid configuration. The [%s] property is missing or is empty.", propertyName));
        }
    }
}
