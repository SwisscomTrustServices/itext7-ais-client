package com.swisscom.ais.itext.client.config;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import java.io.IOException;
import java.util.Properties;

public class AisClientConfiguration {

    private static final String INTERVAL_IN_SECONDS_POLL_PROPERTY = "client.poll.intervalInSeconds";
    private static final String ROUNDS_POLL_PROPERTY = "client.poll.rounds";

    private int signaturePollingIntervalInSeconds = 10;

    private int signaturePollingRounds = 10;

    public int getSignaturePollingIntervalInSeconds() {
        return signaturePollingIntervalInSeconds;
    }

    public void setSignaturePollingIntervalInSeconds(int signaturePollingIntervalInSeconds) {
        if (signaturePollingIntervalInSeconds < 1 || signaturePollingIntervalInSeconds > 300) {
            throw new AisClientException("The signaturePollingIntervalInSeconds parameter of the AIS client "
                                         + "configuration must be between 1 and 300 seconds");
        }
        this.signaturePollingIntervalInSeconds = signaturePollingIntervalInSeconds;
    }

    public int getSignaturePollingRounds() {
        return signaturePollingRounds;
    }

    public void setSignaturePollingRounds(int signaturePollingRounds) {
        if (signaturePollingRounds < 1 || signaturePollingRounds > 100) {
            throw new AisClientException("The signaturePollingRounds parameter of the AIS client "
                                         + "configuration must be between 1 and 100 rounds");
        }
        this.signaturePollingRounds = signaturePollingRounds;
    }

    public void setFromPropertiesClasspathFile(String fileName) {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(fileName));
            setFromProperties(properties);
        } catch (IOException exception) {
            throw new AisClientException(String.format("Failed to load AIS client properties from classpath file: [%s]", fileName), exception);
        }
    }

    public void setFromProperties(Properties properties) {
        setFromConfigurationProvider(new ConfigurationProviderPropertiesImpl(properties));
    }

    public void setFromConfigurationProvider(ConfigurationProvider provider) {
        setSignaturePollingIntervalInSeconds(PropertyUtils.extractIntProperty(provider, INTERVAL_IN_SECONDS_POLL_PROPERTY));
        setSignaturePollingRounds(PropertyUtils.extractIntProperty(provider, ROUNDS_POLL_PROPERTY));
    }
}
