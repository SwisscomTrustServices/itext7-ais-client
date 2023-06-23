package com.swisscom.ais.itext7.client.rest.model.signreq.etsi.rax;

import java.util.List;

public class AuthRequest {

    private String credentialID;
    private String hashAlgorithmOID;
    private List<DocumentsDigests> documentDigests;

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getHashAlgorithmOID() {
        return hashAlgorithmOID;
    }

    public void setHashAlgorithmOID(String hashAlgorithmOID) {
        this.hashAlgorithmOID = hashAlgorithmOID;
    }

    public List<DocumentsDigests> getDocumentDigests() {
        return documentDigests;
    }

    public void setDocumentDigests(List<DocumentsDigests> documentsDigests) {
        this.documentDigests = documentsDigests;
    }
}
