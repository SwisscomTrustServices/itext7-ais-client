package com.swisscom.ais.itext7.client.rest;

import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.SignRequest;
import com.swisscom.ais.itext7.client.rest.model.signresp.etsi.SignResponse;

import java.io.Closeable;

public interface ETSIRestClient extends Closeable {
    SignResponse signWithETSI(SignRequest signingRequest, Trace trace);
}
