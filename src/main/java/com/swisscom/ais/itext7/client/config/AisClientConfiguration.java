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
package com.swisscom.ais.itext7.client.config;

import com.swisscom.ais.itext7.client.common.PropertiesLoader;
import com.swisscom.ais.itext7.client.utils.ValidationUtils;

public class AisClientConfiguration extends PropertiesLoader<AisClientConfiguration.Builder> {

    private static final int DEFAULT_SIGNATURE_POLLING_INTERVAL_IN_SECONDS = 10;
    private static final int DEFAULT_SIGNATURE_POLLING_ROUNDS = 10;

    private int signaturePollingIntervalInSeconds;
    private int signaturePollingRounds;
    private String licenseFilePath;

    public AisClientConfiguration() {
        super();
    }

    public AisClientConfiguration(int signaturePollingIntervalInSeconds, int signaturePollingRounds, String licenseFilePath) {
        this.signaturePollingIntervalInSeconds = signaturePollingIntervalInSeconds;
        this.signaturePollingRounds = signaturePollingRounds;
        this.licenseFilePath = licenseFilePath;
    }

    public static AisClientConfiguration.Builder builder() {
        return new AisClientConfiguration.Builder();
    }

    public int getSignaturePollingIntervalInSeconds() {
        return signaturePollingIntervalInSeconds;
    }

    public int getSignaturePollingRounds() {
        return signaturePollingRounds;
    }

    public String getLicenseFilePath() {
        return licenseFilePath;
    }

    @Override
    protected AisClientConfiguration.Builder fromConfigurationProvider(ConfigurationProvider provider) {
        return builder()
            .withSignaturePollingIntervalInSeconds(extractIntProperty(provider, "client.poll.intervalInSeconds"))
            .withSignaturePollingRounds(extractIntProperty(provider, "client.poll.rounds"))
            .withLicenseFilePath(extractSecretProperty(provider, "license.file"));
    }

    private void validate() {
        ValidationUtils.between(signaturePollingIntervalInSeconds, 1, 300,
                                "The signaturePollingIntervalInSeconds parameter of the AIS client configuration must be between 1 and 300 seconds");
        ValidationUtils.between(signaturePollingIntervalInSeconds, 1, 100,
                                "The signaturePollingRounds parameter of the AIS client configuration must be between 1 and 100 seconds");
        ValidationUtils.notBlank(licenseFilePath, "The iText license file path can not be blank.");
    }

    public static class Builder {
        private int signaturePollingIntervalInSeconds = DEFAULT_SIGNATURE_POLLING_INTERVAL_IN_SECONDS;
        private int signaturePollingRounds = DEFAULT_SIGNATURE_POLLING_ROUNDS;
        private String licenseFilePath;

        Builder() {
        }

        public Builder withSignaturePollingIntervalInSeconds(int signaturePollingIntervalInSeconds) {
            this.signaturePollingIntervalInSeconds = signaturePollingIntervalInSeconds;
            return this;
        }

        public Builder withSignaturePollingRounds(int signaturePollingRounds) {
            this.signaturePollingRounds = signaturePollingRounds;
            return this;
        }

        public Builder withLicenseFilePath(String licenseFilePath) {
            this.licenseFilePath = licenseFilePath;
            return this;
        }

        public AisClientConfiguration build() {
            AisClientConfiguration aisClientConfig = new AisClientConfiguration(signaturePollingIntervalInSeconds, signaturePollingRounds,
                                                                                licenseFilePath);
            aisClientConfig.validate();
            return aisClientConfig;
        }

        @Override
        public String toString() {
            return "AisClientConfiguration.Builder{" +
                   "signaturePollingIntervalInSeconds=" + signaturePollingIntervalInSeconds +
                   ", signaturePollingRounds=" + signaturePollingRounds +
                   ", licenseFilePath='" + licenseFilePath + '\'' +
                   '}';
        }
    }
}
