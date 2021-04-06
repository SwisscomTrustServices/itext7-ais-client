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
package com.swisscom.ais.itext.client.utils;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.model.Trace;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.compare.ComparableUtils;

import java.util.Objects;
import java.util.function.Predicate;

public class ValidationUtils {

    private static final String DELIMITER = " - ";

    public static void notBlank(String value, String errorMessage) {
        validateValue(value, StringUtils::isNotBlank, errorMessage, null);
    }

    public static void notBlank(String value, String errorMessage, Trace trace) {
        validateValue(value, StringUtils::isNotBlank, errorMessage, trace);
    }

    public static void notNull(Object value, String errorMessage, Trace trace) {
        validateValue(value, Objects::nonNull, errorMessage, trace);
    }

    public static void allNotNull(String errorMessage, Trace trace, Object... values) {
        validateValue(values, ObjectUtils::allNotNull, errorMessage, trace);
    }

    public static void between(int value, int minValue, int maxValue, String errorMessage) {
        validateValue(value, v -> ComparableUtils.is(v).between(minValue, maxValue), errorMessage, null);
    }

    public static void isPositive(int value, String errorMessage) {
        validateValue(value, val -> val > 0, errorMessage, null);
    }

    private static <T> void validateValue(T value, Predicate<T> validValuePredicate, String errorMessage, Trace trace) {
        if (!validValuePredicate.test(value)) {
            String message = Objects.nonNull(trace) ? String.join(DELIMITER, errorMessage, trace.getId()) : errorMessage;
            throw new AisClientException(message);
        }
    }
}
