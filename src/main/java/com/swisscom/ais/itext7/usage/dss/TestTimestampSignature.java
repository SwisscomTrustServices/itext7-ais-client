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
package com.swisscom.ais.itext7.usage.dss;

import com.swisscom.ais.itext7.client.model.SignatureMode;

import java.util.Properties;

/**
 * Test with an Timestamp signature that shows how to access all the configuration available and load it from a
 * properties file. The same configuration can also be tweaked by hand or via some framework (e.g. Spring, Guice, etc).
 */
public class TestTimestampSignature extends SignatureTest {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(TestTimestampSignature.class.getResourceAsStream("/local-config.properties"));

        sign(properties, SignatureMode.TIMESTAMP);
    }
}
