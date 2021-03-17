package com.swisscom.ais.itext.client.impl.utils;

import com.swisscom.ais.itext.client.rest.model.ResultMajorCode;
import com.swisscom.ais.itext.client.rest.model.signresp.AISSignResponse;
import com.swisscom.ais.itext.client.rest.model.signresp.Result;

import org.apache.commons.lang3.ObjectUtils;

public class ResponseHelper {

    public static boolean responseIsMajorSuccess(AISSignResponse response) {
        return ObjectUtils.allNotNull(response, response.getSignResponse(), response.getSignResponse().getResult()) &&
               ResultMajorCode.SUCCESS.getUri().equals(response.getSignResponse().getResult().getResultMajor());
    }

    public static String getResponseResultSummary(AISSignResponse response) {
        Result result = response.getSignResponse().getResult();
        return String.format("Major=[%s], Minor=[%s], Message=[%s]", result.getResultMajor(), result.getResultMinor(), result.getResultMessage());
    }
}
