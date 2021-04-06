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
package com.swisscom.ais.itext.client.rest;

import com.swisscom.ais.itext.client.model.Trace;
import com.swisscom.ais.itext.client.rest.model.pendingreq.AISPendingRequest;
import com.swisscom.ais.itext.client.rest.model.signreq.AISSignRequest;
import com.swisscom.ais.itext.client.rest.model.signresp.AISSignResponse;

import java.io.Closeable;

public interface SignatureRestClient extends Closeable {

    /**
     * Perform an HTTP request of type <em>sign request</em> to the trusted server in order to retrieve the authorized signature.
     *
     * @param requestWrapper the AIS sign request wrapper object to be used when the actual request is made
     * @param trace          the unique identifiable trace of the request
     * @return the AIS sign response wrapper object of the actual request
     */
    AISSignResponse requestSignature(AISSignRequest requestWrapper, Trace trace);

    /**
     * Perform an HTTP request of type <em>pending request</em> to the trusted server in order to retrieve the authorized signature.
     *
     * @param requestWrapper the AIS pending request wrapper object to be used when the actual request is made
     * @param trace          the unique identifiable trace of the request
     * @return the AIS pending response wrapper object of the actual request
     */
    AISSignResponse pollForSignatureStatus(AISPendingRequest requestWrapper, Trace trace);
}
