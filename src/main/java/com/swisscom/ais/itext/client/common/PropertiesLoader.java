package com.swisscom.ais.itext.client.common;

import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

public abstract class PropertiesLoader<T> {

    private static final String ENV_VARIABLE_PREFIX = "${";
    private static final String ENV_VARIABLE_SUFFIX = "}";

    protected PropertiesLoader() {
    }

    private static void validateProperty(String propertyName, String propertyValue) {
        if (StringUtils.isBlank(propertyValue)) {
            throw new IllegalStateException(String.format("Invalid configuration. The [%s] property is missing or is empty.", propertyName));
        }
    }

    protected abstract T fromConfigurationProvider(ConfigurationProvider provider);

    public T fromPropertiesClasspathFile(String fileName) {
        return fromProperties(loadPropertiesFromClasspathFile(this.getClass(), fileName));
    }

    public T fromPropertiesFile(String fileName) {
        return fromProperties(PropertyUtils.loadPropertiesFromFile(fileName));
    }

    public T fromProperties(Properties properties) {
        return fromConfigurationProvider(new ConfigurationProviderPropertiesImpl(properties));
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

    public String extractSecretProperty(ConfigurationProvider provider, String propertyName) {
        String property = extractProperty(provider, propertyName);
        return shouldExtractFromEnvVariable(property) ? System.getenv(extractEnvPropertyName(property)) : property;
    }

    private boolean shouldExtractFromEnvVariable(String property) {
        return property.startsWith(ENV_VARIABLE_PREFIX) && property.endsWith(ENV_VARIABLE_SUFFIX);
    }

    private String extractEnvPropertyName(String property) {
        return property.substring(ENV_VARIABLE_PREFIX.length(), property.length() - ENV_VARIABLE_SUFFIX.length());
    }

    public <E> E extractProperty(ConfigurationProvider provider, String propertyName, Function<String, E> mapperFunction, E defaultValue) {
        String propertyValue = provider.getProperty(propertyName);
        return StringUtils.isNotBlank(propertyValue) ? mapperFunction.apply(propertyValue) : defaultValue;
    }

    private String extractProperty(ConfigurationProvider provider, String propertyName) {
        String propertyValue = provider.getProperty(propertyName);
        validateProperty(propertyName, propertyValue);
        return propertyValue;
    }
}
