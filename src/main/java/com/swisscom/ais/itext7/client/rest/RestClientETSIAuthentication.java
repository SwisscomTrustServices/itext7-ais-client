package com.swisscom.ais.itext7.client.rest;

import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.etsi.auth.TokenRequest;
import com.swisscom.ais.itext7.client.rest.model.etsi.auth.TokenResponse;

public interface RestClientETSIAuthentication {
    TokenResponse getToken(TokenRequest tokenRequest, Trace trace);
}
