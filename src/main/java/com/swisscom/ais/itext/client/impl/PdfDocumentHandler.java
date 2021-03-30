package com.swisscom.ais.itext.client.impl;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.impl.container.PdfHashSignatureContainer;
import com.swisscom.ais.itext.client.impl.container.PdfSignatureContainer;
import com.swisscom.ais.itext.client.impl.signer.PdfDocumentSigner;
import com.swisscom.ais.itext.client.model.DigestAlgorithm;
import com.swisscom.ais.itext.client.model.SignatureType;
import com.swisscom.ais.itext.client.model.Trace;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.utils.AisObjectUtils;
import com.swisscom.ais.itext.client.utils.IdGenerator;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class PdfDocumentHandler implements Closeable {

    private static final Logger processingLogger = LoggerFactory.getLogger(Loggers.PDF_PROCESSING);

    private final String inputFilePath;
    private final String outputFilePath;
    private final Trace trace;

    private String id;
    private PdfReader pdfReader;
    private PdfWriter pdfWriter;
    private ByteArrayOutputStream inMemoryStream;
    private PdfDocumentSigner pdfSigner;
    private PdfDocument pdfDocument;
    private String outputTempFilePath;
    private byte[] documentHash;
    private DigestAlgorithm digestAlgorithm;

    PdfDocumentHandler(@Nonnull String inputFilePath, @Nonnull String outputFilePath, @Nonnull Trace trace) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.trace = trace;
    }

    /**
     * Add signature information (reason for signing, location, contact, date) and create the pdf document hash
     *
     * @param algorithm     the hash algorithm which will be used to sign the pdf
     * @param signatureType the signature type
     * @param userData      the data used to fill the signature attributes
     */
    public void prepareForSigning(DigestAlgorithm algorithm, SignatureType signatureType, UserData userData)
        throws IOException, GeneralSecurityException {

        digestAlgorithm = algorithm;
        id = IdGenerator.generateDocumentId();
        pdfDocument = new PdfDocument(createPdfReader());
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        boolean hasSignature = signatureUtil.getSignatureNames().size() > 0;

        pdfReader = createPdfReader();
        inMemoryStream = new ByteArrayOutputStream();
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

    private String getOptionalAttribute(String attribute) {
        return Objects.nonNull(attribute) ? attribute : StringUtils.EMPTY;
    }

    private PdfReader createPdfReader() throws IOException {
        return new PdfReader(inputFilePath, new ReaderProperties());
    }

    /**
     * Add a signature to pdf document
     *
     * @param externalSignature The extern generated signature
     * @param estimatedSize     Size of external signature
     */
    public void createSignedPdf(@Nonnull byte[] externalSignature, int estimatedSize) {
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new AisClientException(String.format("Could not apply signature because source file contains a certification that does not allow "
                                                       + "any changes to the document with id %s", trace.getId()));
        }

        processingLogger.debug("Estimated signature size: {}", estimatedSize);
        processingLogger.debug("Actual signature size: {}", externalSignature.length);
        processingLogger.debug("Remaining size: {}", estimatedSize - externalSignature.length);

        if (estimatedSize < externalSignature.length) {
            throw new AisClientException(String.format("Not enough space for signature in the document with id %s. The estimated size needs to be " +
                                                       " increased with %d bytes.", trace.getId(), externalSignature.length - estimatedSize));
        }

        try {
            outputTempFilePath = Files.createTempFile("signed", "-temp.pdf").toString();
            OutputStream outputStream = new FileOutputStream(outputTempFilePath);
            pdfSigner.signWithAuthorizedSignature(new PdfSignatureContainer(externalSignature), estimatedSize);

            inMemoryStream.writeTo(outputStream);

            processingLogger.debug("Writing signature to the output file {}", outputTempFilePath);

            closeResource(pdfDocument);
            closeResource(inMemoryStream);
            closeResource(outputStream);
        } catch (IOException | GeneralSecurityException e) {
            throw new AisClientException(String.format("Failed to embed the signature in the document - %s", trace.getId()));
        }
    }

    /**
     * Add external revocation information to DSS Dictionary, to enable Long Term Validation (LTV) in Adobe Reader
     *
     * @param encodedCrlEntries  List of CRL response in base64 encoding
     * @param encodedOcspEntries List of OCSP response in base64 encoding
     */
    // todo maybe try to embed the revocation info in the previous step
    public void addValidationInformation(List<String> encodedCrlEntries, List<String> encodedOcspEntries) {
        if (AisObjectUtils.allNull(encodedCrlEntries, encodedOcspEntries)) {
            return;
        }
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new AisClientException("Could not apply revocation information (LTV) to the DSS Dictionary. Document contains a certification "
                                         + " that does not allow any changes.");
        }

        List<byte[]> crl = mapEncodedEntries(encodedCrlEntries, this::mapEncodedCrl);
        List<byte[]> ocsp = mapEncodedEntries(encodedOcspEntries, this::mapEncodedOcsp);

        try (PdfReader reader = new PdfReader(outputTempFilePath);
             PdfWriter writer = new PdfWriter(outputFilePath);
             PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().preserveEncryption().useAppendMode())) {
            LtvVerification validation = new LtvVerification(pdfDocument);

            List<String> signatureNames = new SignatureUtil(pdfDocument).getSignatureNames();
            String signatureName = signatureNames.get(signatureNames.size() - 1);
            boolean isSignatureVerificationAdded = validation.addVerification(signatureName, ocsp, crl, null);
            validation.merge();
            logSignatureVerificationInfo(isSignatureVerificationAdded);

            boolean isTempFileDeleted = new File(outputTempFilePath).delete();
            logTempFileInfo(isTempFileDeleted);
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to embed the signature(s) in the document(s) and close the streams - %s",
                                                       trace.getId()));
        }
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
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
            processingLogger.info("Merged LTV validation information to the output file {}", outputFilePath);
        } else {
            processingLogger.warn("Failed to merge LTV validation information to the output file {}", outputFilePath);
        }
    }

    private void logTempFileInfo(boolean isTempFileDeleted) {
        if (isTempFileDeleted) {
            processingLogger.debug("Temp file deleted");
        } else {
            processingLogger.warn("Could not delete the temp file");
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
            throw new AisClientException("Failed to map the received encoded CRL entry", e);
        }
    }

    private byte[] mapEncodedOcsp(String encodedOcsp) {
        try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(encodedOcsp))) {
            OCSPResp ocspResp = new OCSPResp(inputStream);
            BasicOCSPResp basicResp = (BasicOCSPResp) ocspResp.getResponseObject();
            logOcspInfo(ocspResp, basicResp);
            return basicResp.getEncoded();
        } catch (IOException | OCSPException e) {
            throw new AisClientException("Failed to map the received encoded OCSP entry", e);
        }
    }

    private void logCrlInfo(X509CRL x509crl) {
        int revokedCertificatesNo = Objects.isNull(x509crl.getRevokedCertificates()) ? 0 : x509crl.getRevokedCertificates().size();
        processingLogger.debug("Embedding CRL response...");
        processingLogger.debug("IssuerDN: {}", x509crl.getIssuerDN());
        processingLogger.debug("This update: {}", x509crl.getThisUpdate());
        processingLogger.debug("Next update: {}", x509crl.getNextUpdate());
        processingLogger.debug("No. of revoked certificates: {}", revokedCertificatesNo);
    }

    private void logOcspInfo(OCSPResp ocspResp, BasicOCSPResp basicResp) {
        SingleResp response = basicResp.getResponses()[0];

        processingLogger.debug("Embedding OCSP response...");
        processingLogger.debug("Status: {}", ocspResp.getStatus() == 0 ? "OK" : "NOK");
        processingLogger.debug("Produced at: {}", basicResp.getProducedAt());
        processingLogger.debug("This Update: {}", response.getThisUpdate());
        processingLogger.debug("Next Update: {}", response.getNextUpdate());
        processingLogger.debug("X509 Cert Issuer: {}", basicResp.getCerts()[0].getIssuer());
        processingLogger.debug("X509 Cert Subject: {}", basicResp.getCerts()[0].getSubject());
        processingLogger.debug("Certificate ID: {} ({})", response.getCertID().getSerialNumber().toString(),
                               response.getCertID().getSerialNumber().toString(16).toUpperCase());
    }

    @Override
    public void close() {
        closeResource(pdfReader);
        closeResource(pdfWriter);
        closeResource(pdfDocument);
        closeResource(inMemoryStream);
    }

    private void closeResource(Closeable resource) {
        try {
            if (Objects.nonNull(resource)) {
                resource.close();
            }
        } catch (IOException e) {
            processingLogger.debug("Failed to close the resource. Reason: {}", e.getMessage());
        }
    }
}
