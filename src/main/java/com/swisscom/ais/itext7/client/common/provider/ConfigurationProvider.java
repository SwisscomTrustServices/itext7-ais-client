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
package com.swisscom.ais.itext7.client.common.provider;

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
