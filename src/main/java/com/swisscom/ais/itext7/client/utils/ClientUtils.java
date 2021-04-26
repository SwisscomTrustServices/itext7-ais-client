package com.swisscom.ais.itext7.client.utils;

import com.swisscom.ais.itext7.client.AisClient;
import com.swisscom.ais.itext7.client.model.PdfMetadata;
import com.swisscom.ais.itext7.client.model.SignatureMode;
import com.swisscom.ais.itext7.client.model.SignatureResult;
import com.swisscom.ais.itext7.client.model.UserData;

import java.util.List;

public class ClientUtils {

    public static SignatureResult sign(AisClient client, List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) {
        switch (signatureMode) {
            case TIMESTAMP:
                return client.signWithTimestamp(pdfsMetadata, userData);
            case STATIC:
                return client.signWithStaticCertificate(pdfsMetadata, userData);
            case ON_DEMAND:
                return client.signWithOnDemandCertificate(pdfsMetadata, userData);
            case ON_DEMAND_WITH_STEP_UP:
                return client.signWithOnDemandCertificateAndStepUp(pdfsMetadata, userData);
            default:
                throw new IllegalArgumentException(String.format("Invalid signature mode. Can not sign the document(s) with the %s signature. - %s",
                                                                 signatureMode, userData.getTransactionId()));
        }
    }
}
