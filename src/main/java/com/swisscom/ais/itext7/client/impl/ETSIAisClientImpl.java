package com.swisscom.ais.itext7.client.impl;

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.licensekey.LicenseKeyException;
import com.swisscom.ais.itext7.client.ETSIAisClient;
import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.common.Loggers;
import com.swisscom.ais.itext7.client.model.ETSIUserData;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.ETSIRestClient;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.DocumentDigests;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.SignRequest;
import com.swisscom.ais.itext7.client.rest.model.signresp.etsi.SignResponse;
import com.swisscom.ais.itext7.client.utils.IdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.swisscom.ais.itext7.client.model.SignatureType.CMS;

public class ETSIAisClientImpl implements ETSIAisClient {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);

    private final String itextLicence;
    private final ETSIRestClient restClient;

    public ETSIAisClientImpl(ETSIRestClient restClient, String itextLicence) {
        this.itextLicence = itextLicence;
        this.restClient = restClient;
        initialize();
    }

    private void initialize() {
        if (StringUtils.isNotBlank(itextLicence)) {
            try {
                LicenseKey.loadLicenseFile(itextLicence);
                LicenseKey.scheduledCheck(null);
                String[] licenseeInfo = LicenseKey.getLicenseeInfo();
                clientLogger.info("Successfully load the {} iText license granted for company {}, with name {}, email {}, having version {} and "
                                + "producer line {}. Is license expired: {}.", licenseeInfo[8], licenseeInfo[2], licenseeInfo[0], licenseeInfo[1],
                        licenseeInfo[6], licenseeInfo[4], licenseeInfo[7]);
            } catch (LicenseKeyException e) {
                clientLogger.error("Failed to load the iText license: {}", e.getMessage());
            }
        }
    }

    @Override
    public SignResponse signOnDemandWithETSI(PdfDocumentHandler pdfDocument, ETSIUserData userData, Trace trace, String tokenForETSISigning) throws AisClientException {
        SignResponse etsiSignResponse = performSign(pdfDocument, tokenForETSISigning, trace, userData);
        List<String> crl = etsiSignResponse.getEtsiValidationInfo().getCrl();
        List<String> ocsp = etsiSignResponse.getEtsiValidationInfo().getOcsp();
        pdfDocument.createSignedPdf(Base64.getDecoder().decode(etsiSignResponse.getSignatureObject().get(0).getBytes()), CMS.getEstimatedSignatureSizeInBytes(), crl, ocsp);
        return etsiSignResponse;
    }

    private SignResponse performSign(PdfDocumentHandler documentsToSign, String token, Trace trace, ETSIUserData userData) {

        SignRequest signingRequest = new SignRequest();
        signingRequest.setSAD(token);
        signingRequest.setRequestID(IdGenerator.generateRequestId());
        signingRequest.setCredentialID(userData.getCredentialID());
        signingRequest.setProfile(userData.getProfile());
        signingRequest.setSignatureFormat(userData.getSignatureFormat());
        DocumentDigests documentDigests = new DocumentDigests();
        documentDigests.setHashAlgorithmOID(userData.getHashAlgorithmOID());


        documentDigests.setHashes(Collections.singletonList(documentsToSign.getEncodedDocumentHash()));
        signingRequest.setConformanceLevel(userData.getConformanceLevel());
        signingRequest.setDocumentDigests(documentDigests);

        return restClient.signWithETSI(signingRequest, trace);
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(restClient)) {
            restClient.close();
        }
    }
}
