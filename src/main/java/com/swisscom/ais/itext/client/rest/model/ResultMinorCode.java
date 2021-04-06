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
package com.swisscom.ais.itext.client.rest.model;

import java.util.Arrays;

public enum ResultMinorCode implements DocumentedEnum {

    AUTHENTICATION_FAILED("http://ais.swisscom.ch/1.0/resultminor/AuthenticationFailed",
                          "Request authentication failed. For example, the customer used an unknown certificate."),
    CANT_SERVE_TIMELY("http://ais.swisscom.ch/1.0/resultminor/CantServeTimely",
                      "The request could not be processed on time. The subsystem might be overloaded."),
    INSUFFICIENT_DATA("http://ais.swisscom.ch/1.0/resultminor/InsufficientData",
                      "The request could not be completed, because some information is missing."),
    SERVICE_INACTIVE("http://ais.swisscom.ch/1.0/resultminor/ServiceInactive",
                     "The requested service is inactive (or not defined at all)."),
    SIGNATURE_ERROR("http://ais.swisscom.ch/1.0/resultminor/SignatureError",
                    "An error occurred, while creating a signature."),
    SERIAL_NUMBER_MISMATCH("http://ais.swisscom.ch/1.1/resultminor/subsystem/StepUp/SerialNumberMismatch",
                           "During a step-up authentication, the optional unique serial number was provided in the request but did not match the one of the user’s mobile number."),
    SERVICE_ERROR("http://ais.swisscom.ch/1.1/resultminor/subsystem/StepUp/service",
                  "A service error occurred during the step-up authentication. Error details are included in the error message."),
    STEPUP_INVALID_STATUS("http://ais.swisscom.ch/1.1/resultminor/subsystem/StepUp/status",
                          "An unknown status code of the step-up subsystem was included in the fault response."),
    STEPUP_TIMEOUT("http://ais.swisscom.ch/1.1/resultminor/subsystem/StepUp/timeout",
                   "The transaction expired before the step-up authorization was completed."),
    STEPUP_CANCEL("http://ais.swisscom.ch/1.1/resultminor/subsystem/StepUp/cancel",
                  "The user canceled the step-up authorization."),
    TIMESTAMP_ERROR("http://ais.swisscom.ch/1.0/resultminor/TimestampError",
                    "An error occurred, while creating a timestamp."),
    UNEXPECTED_DATA("http://ais.swisscom.ch/1.0/resultminor/UnexpectedData",
                    "The request contains unexpected (wrong or misleading) data."),
    UNKNOWN_CUSTOMER("http://ais.swisscom.ch/1.0/resultminor/UnknownCustomer",
                     "The customer is unknown."),
    UNKNOWN_SERVICE_ENTITY("http://ais.swisscom.ch/1.0/resultminor/UnknownServiceEntity",
                           "The service entity (static key pair or the On Demand CA server) could not found. Maybe the customer does not have access to it."),
    UNSUPPORTED_DIGEST_ALGORITHM("http://ais.swisscom.ch/1.0/resultminor/UnsupportedDigestAlgorithm",
                                 "The request contains a document hashed with unsupported or weak digest algorithms."),
    UNSUPPORTED_PROFILE("http://ais.swisscom.ch/1.0/resultminor/UnsupportedProfile",
                        "The request contained unknown profile URI."),
    STEPUP_TRANSPORT_ERROR("http://ais.swisscom.ch/1.1/resultminor:subsystem/StepUp/transport",
                           "A subsystem transport error occurred."),
    GENERAL_ERROR("urn:oasis:names:tc:dss:1.0:resultminor:GeneralError",
                  "A general internal error occurred.");

    private final String uri;
    private final String description;

    ResultMinorCode(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public static ResultMinorCode getByUri(String uri) {
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