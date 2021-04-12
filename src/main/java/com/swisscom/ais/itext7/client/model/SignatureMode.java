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
package com.swisscom.ais.itext7.client.model;

import java.util.Arrays;

/**
 * Represents the modes that a PDF document can be signed:
 * <ul>
 *     <li>static</li>
 *     <li>on demand</li>
 *     <li>on demand with step up</li>
 *     <li>timestamp</li>
 * </ul>
 */
public enum SignatureMode {

    TIMESTAMP("timestamp"), STATIC("static"), ON_DEMAND("ondemand"), ON_DEMAND_WITH_STEP_UP("ondemand-stepup");

    private final String value;

    SignatureMode(String value) {
        this.value = value;
    }

    public static SignatureMode getByValue(String signatureValue) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equalsIgnoreCase(signatureValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid signature value provided: %s", signatureValue)));
    }

    public String getValue() {
        return value;
    }

}
