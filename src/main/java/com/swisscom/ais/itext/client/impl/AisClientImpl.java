package com.swisscom.ais.itext.client.impl;

import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.impl.utils.ResponseUtils;
import com.swisscom.ais.itext.client.model.*;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.model.AdditionalProfile;
import com.swisscom.ais.itext.client.rest.model.ResultMajorCode;
import com.swisscom.ais.itext.client.rest.model.ResultMessageCode;
import com.swisscom.ais.itext.client.rest.model.ResultMinorCode;
import com.swisscom.ais.itext.client.rest.model.pendingreq.AISPendingRequest;
import com.swisscom.ais.itext.client.rest.model.signreq.AISSignRequest;
import com.swisscom.ais.itext.client.rest.model.signresp.AISSignResponse;
import com.swisscom.ais.itext.client.rest.model.signresp.Result;
import com.swisscom.ais.itext.client.rest.model.signresp.ScExtendedSignatureObject;
import com.swisscom.ais.itext.client.rest.model.signresp.SignatureObject;
import com.swisscom.ais.itext.client.service.AisRequestService;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AisClientImpl implements AisClient {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);
    private static final Logger protocolLogger = LoggerFactory.getLogger(Loggers.CLIENT_PROTOCOL);

    private final AisRequestService requestService;
    private final AisClientConfiguration configuration;
    private final SignatureRestClient restClient;

    public AisClientImpl() {
        this.requestService = new AisRequestService();
        this.configuration = new AisClientConfiguration();
        this.restClient = new SignatureRestClientImpl();
    }

    public AisClientImpl(AisRequestService requestService, AisClientConfiguration configuration, SignatureRestClient restClient) {
        this.requestService = requestService;
        this.configuration = configuration;
        this.restClient = restClient;
    }

    @Override
    public SignatureResult signWithStaticCertificate(List<PdfMetadata> documentsMetadata, UserData userData) {
        return performSigning(SignatureMode.STATIC, SignatureType.CMS, documentsMetadata, userData, null, false, false);
    }

    @Override
    public SignatureResult signWithOnDemandCertificate(List<PdfMetadata> documentsMetadata, UserData userData) {
        return performSigning(SignatureMode.ON_DEMAND, SignatureType.CMS, documentsMetadata, userData,
                              Collections.singletonList(AdditionalProfile.ON_DEMAND_CERTIFICATE), false, true);
    }

    @Override
    public SignatureResult signWithOnDemandCertificateAndStepUp(List<PdfMetadata> documentsMetadata, UserData userData) {
        return performSigning(SignatureMode.ON_DEMAND_WITH_STEP_UP, SignatureType.CMS, documentsMetadata, userData,
                              Arrays.asList(AdditionalProfile.ON_DEMAND_CERTIFICATE, AdditionalProfile.REDIRECT, AdditionalProfile.ASYNC),
                              true, true);
    }

    @Override
    public SignatureResult signWithTimestamp(List<PdfMetadata> documentsMetadata, UserData userData) {
        return performSigning(SignatureMode.TIMESTAMP, SignatureType.TIMESTAMP, documentsMetadata, userData,
                              Collections.singletonList(AdditionalProfile.TIMESTAMP), false, false);
    }

    private SignatureResult performSigning(SignatureMode signatureMode, SignatureType signatureType, List<PdfMetadata> documentsMetadata,
                                           UserData userData, List<AdditionalProfile> profiles, boolean signWithStepUp,
                                           boolean signWithCertificateRequest) {
        Trace trace = new Trace(userData.getTransactionId());
        userData.validatePropertiesForSignature(signatureMode, trace);
        documentsMetadata.forEach(docMetadata -> docMetadata.validate(trace));

        List<PdfDocumentHandler> documents = prepareMultipleDocumentsForSigning(documentsMetadata, signatureMode, signatureType, userData, trace);

        try {
            List<AdditionalProfile> additionalProfiles = prepareAdditionalProfiles(profiles, !documents.isEmpty());
            AISSignRequest signRequest = requestService.buildAisSignRequest(documents, signatureMode, signatureType, userData, additionalProfiles,
                                                                            signWithStepUp, signWithCertificateRequest);
            AISSignResponse signResponse = restClient.requestSignature(signRequest, trace);

            if (!ResponseUtils.isResponseAsyncPending(signResponse)) {
                return extractSignatureResultFromResponse(signResponse, trace);
            }
            if (signatureMode.equals(SignatureMode.ON_DEMAND_WITH_STEP_UP)) {
                signResponse = pollUntilSignatureIsComplete(signResponse, userData, trace);
            }
            if (!ResponseUtils.isResponseMajorSuccess(signResponse)) {
                return extractSignatureResultFromResponse(signResponse, trace);
            }
            finishDocumentsSigning(documents, signResponse, signatureMode, signatureType.getEstimatedSignatureSizeInBytes(), trace);
            return SignatureResult.SUCCESS;
        } catch (Exception e) {
            throw new AisClientException("Failed to communicate with the AIS service and obtain the signature(s) - " + trace.getId(), e);
        } finally {
            documents.forEach(PdfDocumentHandler::close);
        }
    }

    public AisClientConfiguration getConfiguration() {
        return configuration;
    }

    public SignatureRestClient getRestClient() {
        return restClient;
    }

    private List<PdfDocumentHandler> prepareMultipleDocumentsForSigning(List<PdfMetadata> documentsMetadata, SignatureMode signatureMode,
                                                                        SignatureType signatureType, UserData userData, Trace trace) {
        return documentsMetadata.stream()
            .map(docMetadata -> prepareOneDocumentForSigning(docMetadata, signatureMode, signatureType, userData, trace))
            .collect(Collectors.toList());
    }

    private PdfDocumentHandler prepareOneDocumentForSigning(PdfMetadata documentMetadata, SignatureMode signatureMode, SignatureType signatureType,
                                                            UserData userData, Trace trace) {
        try {
            clientLogger.info("Preparing {} document signing: {} - {}", signatureMode.getValue(), documentMetadata.getInputFilePath(), trace.getId());
            PdfDocumentHandler newDocument = new PdfDocumentHandler(documentMetadata.getInputFilePath(), documentMetadata.getOutputFilePath(), trace);
            newDocument.prepareForSigning(documentMetadata.getDigestAlgorithm(), signatureType, userData);
            return newDocument;
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to prepare the document [%s] for %s signing.",
                                                       documentMetadata.getInputFilePath(), signatureMode.getValue()), e);
        }
    }

    private List<AdditionalProfile> prepareAdditionalProfiles(List<AdditionalProfile> defaultProfiles, boolean signMultipleDocuments) {
        List<AdditionalProfile> profiles = Objects.nonNull(defaultProfiles) ? new ArrayList<>(defaultProfiles) : new ArrayList<>();
        if (signMultipleDocuments) {
            profiles.add(AdditionalProfile.BATCH);
        }
        return profiles;
    }

    private SignatureResult extractSignatureResultFromResponse(AISSignResponse response, Trace trace) {
        if (ObjectUtils.anyNull(response, response.getSignResponse(), response.getSignResponse().getResult(),
                                response.getSignResponse().getResult().getResultMajor())) {
            throw new AisClientException(String.format("Incomplete response received from the AIS service: %s - %s", response, trace.getId()));
        }
        Result responseResult = response.getSignResponse().getResult();
        ResultMajorCode majorCode = ResultMajorCode.getByUri(responseResult.getResultMajor());
        ResultMinorCode minorCode = ResultMinorCode.getByUri(responseResult.getResultMinor());

        if (Objects.isNull(majorCode)) {
            throw new AisClientException(String.format("Failure response received from AIS service: %s - %s",
                                                       ResponseUtils.getResponseResultSummary(response), trace.getId()));
        }

        switch (majorCode) {
            case SUCCESS: {
                return SignatureResult.SUCCESS;
            }
            case PENDING: {
                return SignatureResult.USER_TIMEOUT;
            }
            case SUBSYSTEM_ERROR: {
                Optional<SignatureResult> signatureResult = extractSignatureResultFromMinorCode(minorCode, responseResult);
                if (signatureResult.isPresent()) {
                    return signatureResult.get();
                }
                break;
            }
        }

        throw new AisClientException(String.format("Failure response received from AIS service: %s - %s",
                                                   ResponseUtils.getResponseResultSummary(response), trace.getId()));
    }

    private Optional<SignatureResult> extractSignatureResultFromMinorCode(ResultMinorCode minorCode, Result responseResult) {
        if (Objects.isNull(minorCode)) {
            return Optional.empty();
        }
        switch (minorCode) {
            case SERIAL_NUMBER_MISMATCH:
                return Optional.of(SignatureResult.SERIAL_NUMBER_MISMATCH);
            case STEPUP_TIMEOUT:
                return Optional.of(SignatureResult.USER_TIMEOUT);
            case STEPUP_CANCEL:
                return Optional.of(SignatureResult.USER_CANCEL);
            case SERVICE_ERROR:
                if (Objects.nonNull(responseResult.getResultMessage())) {
                    ResultMessageCode messageCode = ResultMessageCode.getByUri(responseResult.getResultMessage().get$());
                    if (Objects.nonNull(messageCode)) {
                        switch (messageCode) {
                            case INVALID_PASSWORD: // falls through
                            case INVALID_OTP:
                                return Optional.of(SignatureResult.USER_AUTHENTICATION_FAILED);
                        }
                    }
                }
                break;
        }
        return Optional.empty();
    }

    private AISSignResponse pollUntilSignatureIsComplete(AISSignResponse signResponse, UserData userData, Trace trace) {
        AISSignResponse localResponse = signResponse;
        try {
            if (checkForConsentUrlInTheResponse(localResponse, userData, trace)) {
                TimeUnit.SECONDS.sleep(configuration.getSignaturePollingIntervalInSeconds());
            }
            for (int round = 0; round < configuration.getSignaturePollingRounds(); round++) {
                protocolLogger.debug("Polling for signature status, round {}/{} - {}", round + 1, configuration.getSignaturePollingRounds(),
                                     trace.getId());
                AISPendingRequest pendingRequest = requestService.buildAisPendingRequest(ResponseUtils.extractResponseId(localResponse), userData);
                localResponse = restClient.pollForSignatureStatus(pendingRequest, trace);
                checkForConsentUrlInTheResponse(localResponse, userData, trace);
                if (ResponseUtils.isResponseAsyncPending(localResponse)) {
                    TimeUnit.SECONDS.sleep(configuration.getSignaturePollingIntervalInSeconds());
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to poll AIS for the status of the signature(s) - %s", trace.getId()), e);
        }
        return localResponse;
    }

    private boolean checkForConsentUrlInTheResponse(AISSignResponse response, UserData userData, Trace trace) {
        if (ResponseUtils.hasResponseStepUpConsentUrl(response)) {
            if (Objects.nonNull(userData.getConsentUrlCallback())) {
                userData.getConsentUrlCallback().onConsentUrlReceived(ResponseUtils.extractStepUpConsentUrl(response), userData);
            } else {
                clientLogger.warn("Consent URL was received from AIS, but no consent URL callback was configured (in UserData). This transaction "
                                  + " will probably fail - {}", trace.getId());
            }
            return true;
        }
        return false;
    }

    private void finishDocumentsSigning(List<PdfDocumentHandler> documents, AISSignResponse response, SignatureMode signatureMode,
                                        int signatureEstimatedSize, Trace trace) {
        List<String> encodedCrlEntries = ResponseUtils.extractScCRLs(response);
        List<String> encodedOcspEntries = ResponseUtils.extractScOCSPs(response);

        boolean containsSingleDocument = documents.size() == 1;
        documents.forEach(document -> {
            clientLogger.info("Finalizing the signature for document: {} - {}", document.getOutputFilePath(), trace.getId());
            String encodedSignature = extractEncodedSignature(response, containsSingleDocument, signatureMode, document);
            document.createSignedPdf(Base64.getDecoder().decode(encodedSignature), signatureEstimatedSize);
            document.addValidationInformation(encodedOcspEntries, encodedCrlEntries);
        });
    }

    private String extractEncodedSignature(AISSignResponse response, boolean containsSingleDocument, SignatureMode signatureMode,
                                           PdfDocumentHandler document) {
        if (containsSingleDocument) {
            SignatureObject signatureObject = response.getSignResponse().getSignatureObject();
            return signatureMode.equals(SignatureMode.TIMESTAMP) ? signatureObject.getTimestamp().getRFC3161TimeStampToken()
                                                                 : signatureObject.getBase64Signature().get$();
        }
        ScExtendedSignatureObject signatureObject = ResponseUtils.extractEncodedSignatureByDocumentId(document.getId(), response);
        return signatureMode.equals(SignatureMode.TIMESTAMP) ? signatureObject.getTimestamp().getRFC3161TimeStampToken()
                                                             : signatureObject.getBase64Signature().get$();
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(restClient)) {
            restClient.close();
        }
    }
}
