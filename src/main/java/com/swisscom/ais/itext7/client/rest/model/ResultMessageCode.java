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
package com.swisscom.ais.itext7.client.rest.model;

import java.util.Arrays;

public enum ResultMessageCode implements DocumentedEnum {

    INVALID_PASSWORD("urn:swisscom:names:sas:1.0:status:InvalidPassword",
                     "User entered an invalid password, as part of the Step Up phase"),
    INVALID_OTP("urn:swisscom:names:sas:1.0:status:InvalidOtp",
                "User entered an invalid OTP, as part of the Step Up phase");

    private final String uri;
    private final String description;

    ResultMessageCode(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public static ResultMessageCode getByUri(String uri) {
        return Arrays
            .stream(values())
            .filter(value -> value.getUri().equals(uri))
            .findFirst()
            .orElse(null);
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
