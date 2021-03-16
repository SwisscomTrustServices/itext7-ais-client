package com.swisscom.ais.itext.client.model;

@FunctionalInterface
public interface ConsentUrlCallback {

    void onConsentUrlReceived(String consentUrl, UserData userData);
}
