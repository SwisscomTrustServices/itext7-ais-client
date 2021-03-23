package com.swisscom.ais.itext.client.impl.utils;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.rest.model.ResultMajorCode;
import com.swisscom.ais.itext.client.rest.model.signresp.AISSignResponse;
import com.swisscom.ais.itext.client.rest.model.signresp.Result;
import com.swisscom.ais.itext.client.rest.model.signresp.ScCRLs;
import com.swisscom.ais.itext.client.rest.model.signresp.ScExtendedSignatureObject;
import com.swisscom.ais.itext.client.rest.model.signresp.ScOCSPs;
import com.swisscom.ais.itext.client.rest.model.signresp.ScRevocationInformation;

import org.apache.commons.lang3.ObjectUtils;

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
        return ObjectUtils.allNotNull(response, response.getSignResponse(), response.getSignResponse().getOptionalOutputs(),
                                      response.getSignResponse().getOptionalOutputs().getScStepUpAuthorisationInfo(),
                                      response.getSignResponse().getOptionalOutputs().getScStepUpAuthorisationInfo().getScResult(),
                                      response.getSignResponse().getOptionalOutputs().getScStepUpAuthorisationInfo().getScResult().getScConsentURL());
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
        return ObjectUtils.allNotNull(response, response.getSignResponse(), response.getSignResponse().getResult()) &&
               resultCode.getUri().equals(response.getSignResponse().getResult().getResultMajor());
    }

    private static <T> List<String> extractScRevocationInfo(AISSignResponse response, Function<ScRevocationInformation, T> wrapperExtractFunction,
                                                            Function<T, List<String>> infoExtractFunction) {
        if (ObjectUtils.allNotNull(response, response.getSignResponse(), response.getSignResponse().getOptionalOutputs(),
                                   response.getSignResponse().getOptionalOutputs().getScRevocationInformation(),
                                   wrapperExtractFunction.apply(response.getSignResponse().getOptionalOutputs().getScRevocationInformation()))) {
            List<String> revocationInfo = infoExtractFunction.apply(wrapperExtractFunction.apply(
                response.getSignResponse().getOptionalOutputs().getScRevocationInformation()));
            return Objects.nonNull(revocationInfo) ? revocationInfo : Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
