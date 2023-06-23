package com.swisscom.ais.itext7.client.rest.model.signresp.etsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EtsiValidationInfo {

    @JsonProperty("ocsp")
    private List<String> ocsp;
    @JsonProperty("crl")
    private List<String> crl;

    public List<String> getOcsp() {
        return ocsp;
    }

    public void setOcsp(List<String> ocsp) {
        this.ocsp = ocsp;
    }

    public List<String> getCrl() {
        return crl;
    }

    public void setCrl(List<String> crl) {
        this.crl = crl;
    }
}
