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
package com.swisscom.ais.itext.client.impl.utils;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.rest.model.ResultMajorCode;
import com.swisscom.ais.itext.client.rest.model.signresp.*;
import com.swisscom.ais.itext.client.utils.AisObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ResponseUtils {

    public static boolean isResponseMajorSuccess(AISSignResponse response) {
        return isResponseMajorCode(response, ResultMajorCode.SUCCESS);
    }

    public static boolean isResponseAsyncPending(AISSignResponse response) {
        return isResponseMajorCode(response, ResultMajorCode.PENDING);
    }

    public static boolean hasResponseStepUpConsentUrl(AISSignResponse response) {
        return AisObjectUtils.allChildrenNotNull(response, AISSignResponse::getSignResponse, SignResponse::getOptionalOutputs,
                                                 OptionalOutputs::getScStepUpAuthorisationInfo, ScStepUpAuthorisationInfo::getScResult,
                                                 ScResult::getScConsentURL);
    }

    public static String extractStepUpConsentUrl(AISSignResponse response) {
        return response.getSignResponse().getOptionalOutputs().getScStepUpAuthorisationInfo().getScResult().getScConsentURL();
    }

    public static String extractResponseId(AISSignResponse response) {
        return response.getSignResponse().getOptionalOutputs().getAsyncResponseID();
    }

    public static List<String> extractScCRLs(AISSignResponse response) {
        return extractScRevocationInfo(response, ScRevocationInformation::getScCRLs, ScCRLs::getScCRL);
    }

    public static List<String> extractScOCSPs(AISSignResponse response) {
        return extractScRevocationInfo(response, ScRevocationInformation::getScOCSPs, ScOCSPs::getScOCSP);
    }

    public static ScExtendedSignatureObject extractEncodedSignatureByDocumentId(String documentId, AISSignResponse response) {
        return response.getSignResponse().getSignatureObject().getOther().getScSignatureObjects().getScExtendedSignatureObject().stream()
            .filter(encodedSignature -> encodedSignature.getWhichDocument().equals(documentId))
            .findFirst()
            .orElseThrow(() -> new AisClientException(String.format("Invalid AIS response. Cannot find the extended signature object for document"
                                                                    + " with ID=[%s]", documentId)));
    }

    public static String getResponseResultSummary(AISSignResponse response) {
        Result result = response.getSignResponse().getResult();
        return String.format("Major=[%s], Minor=[%s], Message=[%s]", result.getResultMajor(), result.getResultMinor(), result.getResultMessage());
    }

    private static boolean isResponseMajorCode(AISSignResponse response, ResultMajorCode resultCode) {
        return AisObjectUtils.allChildrenNotNull(response, AISSignResponse::getSignResponse, SignResponse::getResult) &&
               resultCode.getUri().equals(response.getSignResponse().getResult().getResultMajor());
    }

    private static <T> List<String> extractScRevocationInfo(AISSignResponse response, Function<ScRevocationInformation, T> wrapperExtracter,
                                                            Function<T, List<String>> infoExtractFunction) {
        if (AisObjectUtils.allChildrenNotNull(response, AISSignResponse::getSignResponse, SignResponse::getOptionalOutputs,
                                              OptionalOutputs::getScRevocationInformation)
            && AisObjectUtils.allNotNull(wrapperExtracter.apply(response.getSignResponse().getOptionalOutputs().getScRevocationInformation()))) {
            List<String> revocationInfo = infoExtractFunction.apply(wrapperExtracter.apply(
                response.getSignResponse().getOptionalOutputs().getScRevocationInformation()));
            return Objects.nonNull(revocationInfo) ? revocationInfo : Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
