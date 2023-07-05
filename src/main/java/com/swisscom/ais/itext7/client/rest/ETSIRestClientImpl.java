package com.swisscom.ais.itext7.client.rest;

import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.SignRequest;
import com.swisscom.ais.itext7.client.rest.model.signresp.etsi.SignResponse;

public class ETSIRestClientImpl extends AbstractRestClient implements ETSIRestClient {

    public ETSIRestClientImpl withConfiguration(RestClientConfiguration config) {
        super.autoConf(config);
        return this;
    }

    @Override
    public SignResponse signWithETSI(SignRequest signingRequest, Trace trace) {
        return executeRequest("SignWithETSI", config.getServiceSignUrl(), signingRequest, SignResponse.class, trace);
    }
}
