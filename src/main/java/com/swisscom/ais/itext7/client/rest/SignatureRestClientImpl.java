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
package com.swisscom.ais.itext7.client.rest;

import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.pendingreq.AISPendingRequest;
import com.swisscom.ais.itext7.client.rest.model.signreq.dss.AISSignRequest;
import com.swisscom.ais.itext7.client.rest.model.signresp.dss.AISSignResponse;

public class SignatureRestClientImpl extends AbstractRestClient implements SignatureRestClient {

    public SignatureRestClient withConfiguration(RestClientConfiguration config) {
        super.autoConf(config);
        return this;
    }

    @Override
    public AISSignResponse requestSignature(AISSignRequest requestWrapper, Trace trace) {
        return executeRequest(SIGN_REQUEST_OPERATION, config.getServiceSignUrl(), requestWrapper, AISSignResponse.class, trace);
    }

    @Override
    public AISSignResponse pollForSignatureStatus(AISPendingRequest requestWrapper, Trace trace) {
        return executeRequest(PENDING_REQUEST_OPERATION, config.getServicePendingUrl(), requestWrapper, AISSignResponse.class, trace);
    }

}
