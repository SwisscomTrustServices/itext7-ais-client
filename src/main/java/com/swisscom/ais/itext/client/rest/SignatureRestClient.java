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
