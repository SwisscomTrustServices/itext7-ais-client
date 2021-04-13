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
package com.swisscom.ais.itext7.client.service;

import com.itextpdf.licensekey.LicenseKeyException;
import com.swisscom.ais.itext7.client.AisClient;
import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.common.Loggers;
import com.swisscom.ais.itext7.client.model.PdfMetadata;
import com.swisscom.ais.itext7.client.model.SignatureMode;
import com.swisscom.ais.itext7.client.model.SignatureResult;
import com.swisscom.ais.itext7.client.model.UserData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Represents a more convenient way to use an {@link AisClient}. However, the {@link AisClient} can be directly used,
 * depending on the preferences or use case.
 */
public class SigningService {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);

    private final AisClient client;

    public SigningService(AisClient client) {
        this.client = client;
    }

    /**
     * @param pdfsMetadata  the documents metadata to be signed
     * @param signatureMode the {@linkplain SignatureMode signature mode} to be used to sign the PDFs
     * @param userData      the {@linkplain UserData user specific metadata} for signing
     * @return the {@linkplain SignatureResult signature result}
     * @throws IllegalArgumentException if the documents can not be signed with the provided signature mode
     * @throws LicenseKeyException      if the iText license was not loaded previously
     * @throws AisClientException       if the signature acquisition process from the AIS service fails
     */
    public SignatureResult performSignings(List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) {
        clientLogger.info("Start performing the signings for the input file(s). You can trace the corresponding details using the {} trace id.",
                          userData.getTransactionId());
        SignatureResult signatureResult = sign(client, pdfsMetadata, signatureMode, userData);
        clientLogger.info("Signature(s) final result: {} - {}", signatureResult, userData.getTransactionId());
        return signatureResult;
    }

    private SignatureResult sign(AisClient client, List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) {
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
