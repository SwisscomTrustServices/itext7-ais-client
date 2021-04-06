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
package com.swisscom.ais.itext.client.rest.config;

import com.swisscom.ais.itext.client.common.PropertiesLoader;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

public class RestClientConfiguration extends PropertiesLoader<RestClientConfiguration.Builder> {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 10;
    private static final int DEFAULT_SOCKET_TIMEOUT_IN_SEC = 10;
    private static final int DEFAULT_RESPONSE_TIMEOUT_IN_SEC = 20;
    private static final String DEFAULT_SERVICE_SIGN_URL = "https://ais.swisscom.com/AIS-Server/rs/v1.0/sign";
    private static final String DEFAULT_SERVICE_PENDING_URL = "https://ais.swisscom.com/AIS-Server/rs/v1.0/pending";

    private String serviceSignUrl;
    private String servicePendingUrl;
    private String clientKeyFile;
    private String clientKeyPassword;
    private String clientCertificateFile;
    private String serverCertificateFile;
    private int maxTotalConnections;
    private int maxConnectionsPerRoute;
    private int connectionTimeoutInSec;
    private int responseTimeoutInSec;

    public RestClientConfiguration() {
        super();
    }

    public RestClientConfiguration(String serviceSignUrl, String servicePendingUrl, String clientKeyFile, String clientKeyPassword,
                                   String clientCertificateFile, String serverCertificateFile, int maxTotalConnections, int maxConnectionsPerRoute,
                                   int connectionTimeoutInSec, int responseTimeoutInSec) {
        this.serviceSignUrl = serviceSignUrl;
        this.servicePendingUrl = servicePendingUrl;
        this.clientKeyFile = clientKeyFile;
        this.clientKeyPassword = clientKeyPassword;
        this.clientCertificateFile = clientCertificateFile;
        this.serverCertificateFile = serverCertificateFile;
        this.maxTotalConnections = maxTotalConnections;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.connectionTimeoutInSec = connectionTimeoutInSec;
        this.responseTimeoutInSec = responseTimeoutInSec;
    }

    public static RestClientConfiguration.Builder builder() {
        return new Builder();
    }

    public String getServiceSignUrl() {
        return serviceSignUrl;
    }

    public String getServicePendingUrl() {
        return servicePendingUrl;
    }

    public String getClientKeyFile() {
        return clientKeyFile;
    }

    public String getClientKeyPassword() {
        return clientKeyPassword;
    }

    public String getClientCertificateFile() {
        return clientCertificateFile;
    }

    public String getServerCertificateFile() {
        return serverCertificateFile;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public int getConnectionTimeoutInSec() {
        return connectionTimeoutInSec;
    }

    public int getResponseTimeoutInSec() {
        return responseTimeoutInSec;
    }

    @Override
    protected RestClientConfiguration.Builder fromConfigurationProvider(ConfigurationProvider provider) {
        return builder()
            .withServiceSignUrl(extractStringProperty(provider, "server.rest.signUrl"))
            .withServicePendingUrl(extractStringProperty(provider, "server.rest.pendingUrl"))
            .withClientKeyFile(extractStringProperty(provider, "client.auth.keyFile"))
            .withClientKeyPassword(extractSecretProperty(provider, "client.auth.keyPassword"))
            .withClientCertificateFile(extractStringProperty(provider, "client.cert.file"))
            .withServerCertificateFile(extractStringProperty(provider, "server.cert.file"))
            .withMaxTotalConnections(extractIntProperty(provider, "client.http.maxTotalConnections"))
            .withMaxConnectionsPerRoute(extractIntProperty(provider, "client.http.maxConnectionsPerRoute"))
            .withConnectionTimeoutInSec(extractIntProperty(provider, "client.http.connectionTimeoutInSeconds"))
            .withResponseTimeoutInSec(extractIntProperty(provider, "client.http.responseTimeoutInSeconds"));
    }

    private void validate() {
        ValidationUtils.notBlank(serviceSignUrl, "The serviceSignUrl parameter of the REST client configuration must not be empty");
        ValidationUtils.notBlank(servicePendingUrl, "The servicePendingUrl parameter of the REST client configuration must not be empty");
        ValidationUtils.notBlank(clientKeyFile, "The clientKeyFile parameter of the REST client configuration must not be empty");
        ValidationUtils.notBlank(clientCertificateFile, "The clientCertificateFile parameter of the REST client configuration must not be empty");
        ValidationUtils.notBlank(serverCertificateFile, "The serverCertificateFile parameter of the REST client configuration must not be empty");
        ValidationUtils.isPositive(maxTotalConnections, "The maxTotalConnections parameter of the REST client configuration must not be empty");
        ValidationUtils.isPositive(maxConnectionsPerRoute, "The maxConnectionsPerRoute parameter of the REST client configuration must not be empty");
        ValidationUtils.isPositive(connectionTimeoutInSec, "The connectionTimeoutInSec parameter of the REST client configuration must not be empty");
        ValidationUtils.isPositive(responseTimeoutInSec, "The responseTimeoutInSec parameter of the REST client configuration must not be empty");
    }

    public static class Builder {
        private String serviceSignUrl = DEFAULT_SERVICE_SIGN_URL;
        private String servicePendingUrl = DEFAULT_SERVICE_PENDING_URL;
        private String clientKeyFile;
        private String clientKeyPassword;
        private String clientCertificateFile;
        private String serverCertificateFile;
        private int maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
        private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        private int connectionTimeoutInSec = DEFAULT_SOCKET_TIMEOUT_IN_SEC;
        private int responseTimeoutInSec = DEFAULT_RESPONSE_TIMEOUT_IN_SEC;

        Builder() {
        }

        public Builder withServiceSignUrl(String serviceSignUrl) {
            this.serviceSignUrl = serviceSignUrl;
            return this;
        }

        public Builder withServicePendingUrl(String servicePendingUrl) {
            this.servicePendingUrl = servicePendingUrl;
            return this;
        }

        public Builder withClientKeyFile(String clientKeyFile) {
            this.clientKeyFile = clientKeyFile;
            return this;
        }

        public Builder withClientKeyPassword(String clientKeyPassword) {
            this.clientKeyPassword = clientKeyPassword;
            return this;
        }

        public Builder withClientCertificateFile(String clientCertificateFile) {
            this.clientCertificateFile = clientCertificateFile;
            return this;
        }

        public Builder withServerCertificateFile(String serverCertificateFile) {
            this.serverCertificateFile = serverCertificateFile;
            return this;
        }

        public Builder withMaxTotalConnections(int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
            return this;
        }

        public Builder withMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
            this.maxConnectionsPerRoute = maxConnectionsPerRoute;
            return this;
        }

        public Builder withConnectionTimeoutInSec(int connectionTimeoutInSec) {
            this.connectionTimeoutInSec = connectionTimeoutInSec;
            return this;
        }

        public Builder withResponseTimeoutInSec(int responseTimeoutInSec) {
            this.responseTimeoutInSec = responseTimeoutInSec;
            return this;
        }

        public RestClientConfiguration build() {
            RestClientConfiguration restClientConfiguration = new RestClientConfiguration(
                serviceSignUrl, servicePendingUrl, clientKeyFile, clientKeyPassword, clientCertificateFile, serverCertificateFile,
                maxTotalConnections, maxConnectionsPerRoute, connectionTimeoutInSec, responseTimeoutInSec);
            restClientConfiguration.validate();
            return restClientConfiguration;
        }

        @Override
        public String toString() {
            return "RestClientConfiguration.Builder{" +
                   "serviceSignUrl='" + serviceSignUrl + '\'' +
                   ", servicePendingUrl='" + servicePendingUrl + '\'' +
                   ", clientKeyFile='" + clientKeyFile + '\'' +
                   ", clientKeyPassword='" + clientKeyPassword + '\'' +
                   ", clientCertificateFile='" + clientCertificateFile + '\'' +
                   ", serverCertificateFile='" + serverCertificateFile + '\'' +
                   ", maxTotalConnections=" + maxTotalConnections +
                   ", maxConnectionsPerRoute=" + maxConnectionsPerRoute +
                   ", connectionTimeoutInSec=" + connectionTimeoutInSec +
                   ", responseTimeoutInSec=" + responseTimeoutInSec +
                   '}';
        }
    }
}
