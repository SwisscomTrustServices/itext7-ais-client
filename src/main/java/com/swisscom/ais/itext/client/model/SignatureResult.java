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

/**
 * Enumerates all the "normal" cases in which a signature can be stopped. These are caused by a correct functioning of the
 * AIS client and server and with a particular behaviour of the user (timeout, cancel of signature).
 */
public enum SignatureResult {

    /**
     * The signature finished successfully. The signatures are already embedded in the PDF documents (see {@link PdfMetadata}).
     */
    SUCCESS,

    /**
     * The user cancelled the signature.
     */
    USER_CANCEL,

    /**
     * The user did not respond in a timely manner.
     */
    USER_TIMEOUT,

    /**
     * The provided user serial number (part of the StepUp process) does not match the one on the server side.
     */
    SERIAL_NUMBER_MISMATCH,

    /**
     * The user failed to properly authenticate for the signature.
     */
    USER_AUTHENTICATION_FAILED,

    /**
     * The request is missing the required MSISDN parameter. This can happen sometimes in the context of the on-demand flow,
     * depending on the user's server configuration (e.g. the enforceStepUpAuthentication flag is true). As an alternative,
     * the on-demand with step-up flow can be used instead.
     */
    INSUFFICIENT_DATA_WITH_ABSENT_MSISDN

}
