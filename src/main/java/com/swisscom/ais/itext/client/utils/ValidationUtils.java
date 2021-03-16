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

    public static void notEmpty(String value, String errorMessage, Trace trace) {
        validateValue(value, StringUtils::isBlank, errorMessage, trace);
    }

    public static void notNull(Object value, String errorMessage, Trace trace) {
        validateValue(value, Objects::nonNull, errorMessage, trace);
    }

    public static void allNotNull(String errorMessage, Trace trace, Object... values) {
        validateValue(values, ObjectUtils::allNotNull, errorMessage, trace);
    }

    public static void between(int value, int minValue, int maxValue, String errorMessage, Trace trace) {
        validateValue(value, v -> ComparableUtils.is(v).between(minValue, maxValue), errorMessage, trace);
    }

    private static <T> void validateValue(T value, Predicate<T> invalidValuePredicate, String errorMessage, Trace trace) {
        if (invalidValuePredicate.test(value)) {
            String message = Objects.nonNull(trace) ? String.join(DELIMITER, errorMessage, trace.getId()) : errorMessage;
            throw new AisClientException(message);
        }
    }
}
