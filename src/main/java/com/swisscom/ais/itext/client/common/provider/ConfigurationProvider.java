package com.swisscom.ais.itext.client.common.provider;

/**
 * Utility adapter for allowing the REST and AIS clients to be configured using any external configuration source. Implementations of this
 * interface can, for example, extract the config from a database or from a Spring Boot configured environment (application.yml).
 */
public interface ConfigurationProvider {

    /**
     * @param name the name of the property to look for
     * @return the value of the property or <code>null</code> if the property is not defined.
     */
    String getProperty(String name);

}
