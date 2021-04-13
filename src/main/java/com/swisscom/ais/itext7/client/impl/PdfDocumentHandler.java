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
package com.swisscom.ais.itext7.client.impl;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;
import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.common.Loggers;
import com.swisscom.ais.itext7.client.impl.container.PdfHashSignatureContainer;
import com.swisscom.ais.itext7.client.impl.container.PdfSignatureContainer;
import com.swisscom.ais.itext7.client.impl.signer.PdfDocumentSigner;
import com.swisscom.ais.itext7.client.model.DigestAlgorithm;
import com.swisscom.ais.itext7.client.model.SignatureType;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.model.UserData;
import com.swisscom.ais.itext7.client.utils.AisObjectUtils;
import com.swisscom.ais.itext7.client.utils.IdGenerator;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Able to handles two basic and important PDf document operations: preparing the document for signing by computing a hash based on the document
 * content and to sign the document by embedding into the document the authorized signature together with CRL and OCSP information.
 * <p>
 * High level workflow: compute the hash from the document, call the Swisscom authorized server in order to retrieve the corresponding trust
 * signature and finally sign the document.
 */
public class PdfDocumentHandler implements Closeable {

    private static final Logger processingLogger = LoggerFactory.getLogger(Loggers.PDF_PROCESSING);
    private static final String DELIMITER = "; ";

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Trace trace;

    private String id;
    private PdfReader pdfReader;
    private PdfWriter pdfWriter;
    private ByteArrayOutputStream inMemoryStream;
    private PdfDocumentSigner pdfSigner;
    private byte[] documentHash;
    private DigestAlgorithm digestAlgorithm;

    PdfDocumentHandler(InputStream inputStream, OutputStream outputStream, Trace trace) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.trace = trace;
    }

    /**
     * Add signature information (reason for signing, location, contact, date) and create the pdf document hash
     *
     * @param algorithm     hash algorithm which will be used to sign the pdf
     * @param signatureType signature type
     * @param userData      data used to fill the signature attributes
     */
    public void prepareForSigning(DigestAlgorithm algorithm, SignatureType signatureType, UserData userData)
        throws IOException, GeneralSecurityException {

        digestAlgorithm = algorithm;
        id = IdGenerator.generateDocumentId();
        inMemoryStream = new ByteArrayOutputStream();
        inMemoryStream.write(StreamUtil.inputStreamToArray(inputStream));

        boolean hasSignature = hasDocumentSignature();
        pdfReader = new PdfReader(new ByteArrayInputStream(inMemoryStream.toByteArray()), new ReaderProperties());
        inMemoryStream.reset();
        pdfWriter = new PdfWriter(inMemoryStream, new WriterProperties().addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_0));
        StampingProperties stampingProperties = new StampingProperties();
        pdfSigner = new PdfDocumentSigner(pdfReader, pdfWriter, hasSignature ? stampingProperties.useAppendMode() : stampingProperties);

        pdfSigner.getSignatureAppearance()
            .setReason(getOptionalAttribute(userData.getSignatureReason()))
            .setLocation(getOptionalAttribute(userData.getSignatureLocation()))
            .setContact(getOptionalAttribute(userData.getSignatureContactInfo()));

        if (StringUtils.isNotBlank(userData.getSignatureName())) {
            pdfSigner.setFieldName(userData.getSignatureName());
        }

        boolean isTimestampSignature = signatureType.equals(SignatureType.TIMESTAMP);
        Map<PdfName, PdfObject> signatureDictionary = new HashMap<>();
        signatureDictionary.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        signatureDictionary.put(PdfName.SubFilter, isTimestampSignature ? PdfName.ETSI_RFC3161 : PdfName.ETSI_CAdES_DETACHED);

        Calendar signDate = Calendar.getInstance();
        if (!isTimestampSignature) {
            signDate.add(Calendar.MINUTE, 3);
        }
        pdfSigner.setSignDate(signDate);

        PdfHashSignatureContainer hashSignatureContainer = new PdfHashSignatureContainer(algorithm.getDigestAlgorithm(),
                                                                                         new PdfDictionary(signatureDictionary));
        documentHash = pdfSigner.computeHash(hashSignatureContainer, signatureType.getEstimatedSignatureSizeInBytes());
    }

    private boolean hasDocumentSignature() throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(inMemoryStream.toByteArray());
             PdfReader reader = new PdfReader(is, new ReaderProperties());
             PdfDocument pdfDocument = new PdfDocument(reader)) {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            return signatureUtil.getSignatureNames().size() > 0;
        }
    }

    private String getOptionalAttribute(String attribute) {
        return Objects.nonNull(attribute) ? attribute : StringUtils.EMPTY;
    }

    /**
     * Embed the signature into the PDF document and add external revocation information to DSS Dictionary for enabling the Long Term Validation
     * (LTV) in Adobe Reader.
     *
     * @param externalSignature  external generated signature
     * @param estimatedSize      estimated size of external signature
     * @param encodedCrlEntries  encoded base64 list of CRLs
     * @param encodedOcspEntries encoded base64 list of OCSPs
     */
    public void createSignedPdf(@Nonnull byte[] externalSignature, int estimatedSize, List<String> encodedCrlEntries,
                                List<String> encodedOcspEntries) {
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new AisClientException(String.format("Could not apply signature because source file contains a certification that does not allow "
                                                       + "any changes to the document with id %s", trace.getId()));
        }

        String message = "Signature size [estimated: {}" + DELIMITER + "actual: {}" + DELIMITER + "remaining: {}" + "] - {}";
        processingLogger.debug(message, estimatedSize, externalSignature.length, estimatedSize - externalSignature.length, trace.getId());

        if (estimatedSize < externalSignature.length) {
            throw new AisClientException(String.format("Not enough space for signature in the document with id %s. The estimated size needs to be " +
                                                       " increased with %d bytes.", trace.getId(), externalSignature.length - estimatedSize));
        }

        try {
            pdfSigner.signWithAuthorizedSignature(new PdfSignatureContainer(externalSignature), estimatedSize);

            if (AisObjectUtils.anyNotNull(encodedCrlEntries, encodedOcspEntries)) {
                extendDocumentWithCrlOcspMetadata(encodedCrlEntries, encodedOcspEntries);
            } else {
                processingLogger.info("No CRL and OCSP entries were received to be embedded into the PDF - {}", trace.getId());
                outputStream.write(inMemoryStream.toByteArray());
            }

            closeResource(inMemoryStream);
            closeResource(outputStream);
        } catch (IOException | GeneralSecurityException e) {
            throw new AisClientException(String.format("Failed to embed the signature in the document - %s", trace.getId()), e);
        }
    }

    private void extendDocumentWithCrlOcspMetadata(List<String> encodedCrlEntries, List<String> encodedOcspEntries) {
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new AisClientException(String.format("Could not apply revocation information (LTV) to the DSS Dictionary. Document contains a " +
                                                       "certification that does not allow any changes - %s", trace.getId()));
        }

        List<byte[]> crl = mapEncodedEntries(encodedCrlEntries, this::mapEncodedCrl);
        List<byte[]> ocsp = mapEncodedEntries(encodedOcspEntries, this::mapEncodedOcsp);

        try (InputStream documentStream = new ByteArrayInputStream(inMemoryStream.toByteArray());
             PdfReader reader = new PdfReader(documentStream);
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().preserveEncryption().useAppendMode())) {
            LtvVerification validation = new LtvVerification(pdfDocument);

            List<String> signatureNames = new SignatureUtil(pdfDocument).getSignatureNames();
            String signatureName = signatureNames.get(signatureNames.size() - 1);
            boolean isSignatureVerificationAdded = validation.addVerification(signatureName, ocsp, crl, null);
            validation.merge();
            logSignatureVerificationInfo(isSignatureVerificationAdded);
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to embed the signature(s) in the document(s) and close the streams - %s",
                                                       trace.getId()));
        }
    }

    public String getId() {
        return id;
    }

    public byte[] getDocumentHash() {
        return documentHash;
    }

    public String getEncodedDocumentHash() {
        return Base64.getEncoder().encodeToString(documentHash);
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    private void logSignatureVerificationInfo(boolean isSignatureVerificationAdded) {
        if (isSignatureVerificationAdded) {
            processingLogger.info("Merged LTV validation information to the output stream - {}", trace.getId());
        } else {
            processingLogger.warn("Failed to merge LTV validation information to the output stream - {}", trace.getId());
        }
    }

    private List<byte[]> mapEncodedEntries(List<String> encodedEntries, Function<String, byte[]> mapperFunction) {
        return Objects.nonNull(encodedEntries)
               ? encodedEntries.stream().map(mapperFunction).collect(Collectors.toList())
               : Collections.emptyList();
    }

    private byte[] mapEncodedCrl(String encodedCrl) {
        try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(encodedCrl))) {
            X509CRL x509crl = (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(inputStream);
            logCrlInfo(x509crl);
            return x509crl.getEncoded();
        } catch (IOException | CertificateException | CRLException e) {
            throw new AisClientException(String.format("Failed to map the received encoded CRL entry - %s", trace.getId()), e);
        }
    }

    private byte[] mapEncodedOcsp(String encodedOcsp) {
        try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(encodedOcsp))) {
            OCSPResp ocspResp = new OCSPResp(inputStream);
            BasicOCSPResp basicResp = (BasicOCSPResp) ocspResp.getResponseObject();
            logOcspInfo(ocspResp, basicResp);
            return basicResp.getEncoded();
        } catch (IOException | OCSPException e) {
            throw new AisClientException(String.format("Failed to map the received encoded OCSP entry - %s", trace.getId()), e);
        }
    }

    private void logCrlInfo(X509CRL x509crl) {
        int revokedCertificatesNo = Objects.isNull(x509crl.getRevokedCertificates()) ? 0 : x509crl.getRevokedCertificates().size();

        String message = "Embedding CRL response... ["
                         + "IssuerDN: " + x509crl.getIssuerDN() + DELIMITER
                         + "This update: " + x509crl.getThisUpdate() + DELIMITER
                         + "Next update: " + x509crl.getNextUpdate() + DELIMITER
                         + "No. of revoked certificates: " + revokedCertificatesNo
                         + "] - " + trace.getId();
        processingLogger.debug(message);
    }

    private void logOcspInfo(OCSPResp ocspResp, BasicOCSPResp basicResp) {
        SingleResp response = basicResp.getResponses()[0];
        BigInteger serialNumber = response.getCertID().getSerialNumber();
        X509CertificateHolder firstCertificate = basicResp.getCerts()[0];

        String message = "Embedding OCSP response... ["
                         + "Status: " + (ocspResp.getStatus() == 0 ? "OK" : "NOK") + DELIMITER
                         + "Produced at: " + basicResp.getProducedAt() + DELIMITER
                         + "This update: " + response.getThisUpdate() + DELIMITER
                         + "Next update: " + response.getNextUpdate() + DELIMITER
                         + "X509 cert issuer: " + firstCertificate.getIssuer() + DELIMITER
                         + "X509 cert subject: " + firstCertificate.getSubject() + DELIMITER
                         + "Certificate ID: " + serialNumber.toString() + "(" + serialNumber.toString(16).toUpperCase() + ")"
                         + "] - " + trace.getId();
        processingLogger.debug(message);
    }

    @Override
    public void close() {
        closeResource(pdfReader);
        closeResource(pdfWriter);
        closeResource(inMemoryStream);
        closeResource(inputStream);
        closeResource(outputStream);
    }

    private void closeResource(Closeable resource) {
        try {
            if (Objects.nonNull(resource)) {
                resource.close();
            }
        } catch (IOException e) {
            processingLogger.debug("Failed to close the resource - {}. Reason: {}", trace.getId(), e.getMessage());
        }
    }
}
