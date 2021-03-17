package com.swisscom.ais.itext.client.impl;

import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.impl.utils.ResponseHelper;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.SignatureType;
import com.swisscom.ais.itext.client.model.Trace;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.rest.model.AdditionalProfile;
import com.swisscom.ais.itext.client.rest.model.ResultMajorCode;
import com.swisscom.ais.itext.client.rest.model.ResultMessageCode;
import com.swisscom.ais.itext.client.rest.model.ResultMinorCode;
import com.swisscom.ais.itext.client.rest.model.signreq.AISSignRequest;
import com.swisscom.ais.itext.client.rest.model.signresp.AISSignResponse;
import com.swisscom.ais.itext.client.rest.model.signresp.Result;
import com.swisscom.ais.itext.client.service.AisRequestService;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AisClientImpl implements AisClient {

    private static final Logger logClient = LoggerFactory.getLogger(Loggers.CLIENT);
    private static final Logger logProtocol = LoggerFactory.getLogger(Loggers.CLIENT_PROTOCOL);

    private final AisRequestService requestService;
    private final AisClientConfiguration configuration;

    public AisClientImpl() {
        this.requestService = new AisRequestService();
        this.configuration = new AisClientConfiguration();
    }

    public AisClientImpl(AisRequestService requestService, AisClientConfiguration configuration) {
        this.requestService = requestService;
        this.configuration = configuration;
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
            // todo
            AISSignResponse signResponse = null;

            if (isNotSuccessfulResponse(signResponse)) {
                return extractSignatureResultFromResponse(signResponse, trace);
            }
            // todo
//            finishDocumentsSigning(documents, signResponse, signatureMode, trace);
            return SignatureResult.SUCCESS;
        } catch (Exception e) {
            throw new AisClientException("Failed to communicate with the AIS service and obtain the signature(s) - " + trace.getId(), e);
        } finally {
            documents.forEach(PdfDocumentHandler::close);
        }
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
            logClient.info("Preparing {} document signing : {} - {}", signatureMode.getValue(), documentMetadata.getInputFilePath(), trace.getId());
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

    private boolean isNotSuccessfulResponse(AISSignResponse response) {
        return !ResponseHelper.responseIsMajorSuccess(response);
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
                                                       ResponseHelper.getResponseResultSummary(response), trace.getId()));
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
                                                   ResponseHelper.getResponseResultSummary(response), trace.getId()));
    }

    public Optional<SignatureResult> extractSignatureResultFromMinorCode(ResultMinorCode minorCode, Result responseResult) {
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

    @Override
    public void close() throws IOException {
        // todo
    }
}
