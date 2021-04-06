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
package com.swisscom.ais.itext.client.model;

import java.util.Arrays;

public enum CliArgument {
    INIT("init"), INPUT("input"), OUTPUT("output"), SUFFIX("suffix"), CONFIG("config"), HELP("help"),
    SIGNATURE_TYPE("type"), BASIC_VERBOSITY("v"), MEDIUM_VERBOSITY("vv"), HIGH_VERBOSITY("vvv");

    private final String value;

    CliArgument(String value) {
        this.value = value;
    }

    public static CliArgument getByValue(String argValue) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equalsIgnoreCase(argValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid CLI argument value provided: %s", argValue)));
    }

    public String getValue() {
        return value;
    }
}
