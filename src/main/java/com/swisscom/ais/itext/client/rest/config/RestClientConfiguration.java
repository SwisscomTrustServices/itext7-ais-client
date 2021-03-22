package com.swisscom.ais.itext.client.rest.config;

import com.swisscom.ais.itext.client.common.PropertiesLoader;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

public class RestClientConfiguration extends PropertiesLoader {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 10;
    private static final int DEFAULT_SOCKET_TIMEOUT_IN_SEC = 10;
    private static final int DEFAULT_RESPONSE_TIMEOUT_IN_SEC = 20;
    private static final String DEFAULT_SERVICE_SIGN_URL = "https://ais.swisscom.com/AIS-Server/rs/v1.0/sign";
    private static final String DEFAULT_SERVICE_PENDING_URL = "https://ais.swisscom.com/AIS-Server/rs/v1.0/pending";

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

    public String getServiceSignUrl() {
        return serviceSignUrl;
    }

    public void setServiceSignUrl(String serviceSignUrl) {
        ValidationUtils.notEmpty(serviceSignUrl, "The serviceSignUrl parameter of the REST client configuration must not be empty", null);
        this.serviceSignUrl = serviceSignUrl;
    }

    public String getServicePendingUrl() {
        return servicePendingUrl;
    }

    public void setServicePendingUrl(String servicePendingUrl) {
        ValidationUtils.notEmpty(serviceSignUrl, "The servicePendingUrl parameter of the REST client configuration must not be empty", null);
        this.servicePendingUrl = servicePendingUrl;
    }

    public String getClientKeyFile() {
        return clientKeyFile;
    }

    public void setClientKeyFile(String clientKeyFile) {
        ValidationUtils.notEmpty(serviceSignUrl, "The clientKeyFile parameter of the REST client configuration must not be empty", null);
        this.clientKeyFile = clientKeyFile;
    }

    public String getClientKeyPassword() {
        return clientKeyPassword;
    }

    public void setClientKeyPassword(String clientKeyPassword) {
        this.clientKeyPassword = clientKeyPassword;
    }

    public String getClientCertificateFile() {
        return clientCertificateFile;
    }

    public void setClientCertificateFile(String clientCertificateFile) {
        ValidationUtils.notEmpty(serviceSignUrl, "The clientCertificateFile parameter of the REST client configuration must not be empty", null);
        this.clientCertificateFile = clientCertificateFile;
    }

    public String getServerCertificateFile() {
        return serverCertificateFile;
    }

    public void setServerCertificateFile(String serverCertificateFile) {
        ValidationUtils.notEmpty(serviceSignUrl, "The serverCertificateFile parameter of the REST client configuration must not be empty", null);
        this.serverCertificateFile = serverCertificateFile;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        ValidationUtils.notEmpty(serviceSignUrl, "The maxTotalConnections parameter of the REST client configuration must not be empty", null);
        this.maxTotalConnections = maxTotalConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        ValidationUtils.notEmpty(serviceSignUrl, "The maxConnectionsPerRoute parameter of the REST client configuration must not be empty", null);
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public int getConnectionTimeoutInSec() {
        return connectionTimeoutInSec;
    }

    public void setConnectionTimeoutInSec(int connectionTimeoutInSec) {
        ValidationUtils.notEmpty(serviceSignUrl, "The connectionTimeoutInSec parameter of the REST client configuration must not be empty", null);
        this.connectionTimeoutInSec = connectionTimeoutInSec;
    }

    public int getResponseTimeoutInSec() {
        return responseTimeoutInSec;
    }

    public void setResponseTimeoutInSec(int responseTimeoutInSec) {
        ValidationUtils.notEmpty(serviceSignUrl, "The responseTimeoutInSec parameter of the REST client configuration must not be empty", null);
        this.responseTimeoutInSec = responseTimeoutInSec;
    }

    @Override
    public void setFromConfigurationProvider(ConfigurationProvider provider) {
        setServiceSignUrl(extractStringProperty(provider, "server.rest.signUrl"));
        setServicePendingUrl(extractStringProperty(provider, "server.rest.pendingUrl"));
        setClientKeyFile(extractStringProperty(provider, "client.auth.keyFile"));
        setClientKeyPassword(provider.getProperty("client.auth.keyPassword"));
        setClientCertificateFile(extractStringProperty(provider, "client.cert.file"));
        setServerCertificateFile(extractStringProperty(provider, "server.cert.file"));
        setMaxTotalConnections(extractIntProperty(provider, "client.http.maxTotalConnections"));
        setMaxConnectionsPerRoute(extractIntProperty(provider, "client.http.maxConnectionsPerRoute"));
        setConnectionTimeoutInSec(extractIntProperty(provider, "client.http.connectionTimeoutInSeconds"));
        setResponseTimeoutInSec(extractIntProperty(provider, "client.http.responseTimeoutInSeconds"));
    }
}
