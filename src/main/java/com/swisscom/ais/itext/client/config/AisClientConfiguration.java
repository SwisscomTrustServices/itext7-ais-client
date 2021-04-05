package com.swisscom.ais.itext.client.config;

import com.swisscom.ais.itext.client.common.PropertiesLoader;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

public class AisClientConfiguration extends PropertiesLoader<AisClientConfiguration> {

    private static final String INTERVAL_IN_SECONDS_POLL_PROPERTY = "client.poll.intervalInSeconds";
    private static final String ROUNDS_POLL_PROPERTY = "client.poll.rounds";
    private static final String LICENSE_FILE_PROPERTY = "license.file";
    private static final int DEFAULT_SIGNATURE_POLLING_INTERVAL_IN_SECONDS = 10;
    private static final int DEFAULT_SIGNATURE_POLLING_ROUNDS = 10;

    private int signaturePollingIntervalInSeconds = DEFAULT_SIGNATURE_POLLING_INTERVAL_IN_SECONDS;
    private int signaturePollingRounds = DEFAULT_SIGNATURE_POLLING_ROUNDS;
    private String licenseFilePath;

    public AisClientConfiguration() {
        super();
    }

    public AisClientConfiguration(int signaturePollingIntervalInSeconds, int signaturePollingRounds, String licenseFilePath) {
        this.signaturePollingIntervalInSeconds = signaturePollingIntervalInSeconds;
        this.signaturePollingRounds = signaturePollingRounds;
        this.licenseFilePath = licenseFilePath;
    }

    public int getSignaturePollingIntervalInSeconds() {
        return signaturePollingIntervalInSeconds;
    }

    public void setSignaturePollingIntervalInSeconds(int signaturePollingIntervalInSeconds) {
        ValidationUtils.between(signaturePollingIntervalInSeconds, 1, 300,
                                "The signaturePollingIntervalInSeconds parameter of the AIS client configuration must be between 1 and 300 seconds");
        this.signaturePollingIntervalInSeconds = signaturePollingIntervalInSeconds;
    }

    public int getSignaturePollingRounds() {
        return signaturePollingRounds;
    }

    public void setSignaturePollingRounds(int signaturePollingRounds) {
        ValidationUtils.between(signaturePollingIntervalInSeconds, 1, 100,
                                "The signaturePollingRounds parameter of the AIS client configuration must be between 1 and 100 seconds");
        this.signaturePollingRounds = signaturePollingRounds;
    }

    public String getLicenseFilePath() {
        return licenseFilePath;
    }

    public void setLicenseFilePath(String licenseFilePath) {
        ValidationUtils.notBlank(licenseFilePath, "The iText license file path can not be blank.");
        this.licenseFilePath = licenseFilePath;
    }

    @Override
    protected AisClientConfiguration fromConfigurationProvider(ConfigurationProvider provider) {
        setSignaturePollingIntervalInSeconds(extractIntProperty(provider, INTERVAL_IN_SECONDS_POLL_PROPERTY));
        setSignaturePollingRounds(extractIntProperty(provider, ROUNDS_POLL_PROPERTY));
        setLicenseFilePath(extractSecretProperty(provider, LICENSE_FILE_PROPERTY));
        return this;
    }
}
