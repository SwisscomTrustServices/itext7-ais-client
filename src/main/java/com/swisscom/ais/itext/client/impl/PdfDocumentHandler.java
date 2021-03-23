package com.swisscom.ais.itext.client.impl;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.impl.container.PdfHashSignatureContainer;
import com.swisscom.ais.itext.client.impl.container.PdfSignatureContainer;
import com.swisscom.ais.itext.client.impl.signer.PdfDocumentSigner;
import com.swisscom.ais.itext.client.model.DigestAlgorithm;
import com.swisscom.ais.itext.client.model.SignatureType;
import com.swisscom.ais.itext.client.model.Trace;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.utils.IdGenerator;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;

import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.*;

import javax.annotation.Nonnull;

public class PdfDocumentHandler implements Closeable {

    private final String inputFilePath;
    private final String outputFilePath;
    private final Trace trace;
    private final VerboseLevel loggingLevel;

    private String id;
    private PdfReader pdfReader;
    private PdfWriter pdfWriter;
    private ByteArrayOutputStream inMemoryStream;
    private PdfDocumentSigner pdfSigner;
    private PdfDocument pdfDocument;
    private String outputFileTempPath;
    private byte[] documentHash;
    private DigestAlgorithm digestAlgorithm;

    PdfDocumentHandler(@Nonnull String inputFilePath, @Nonnull String outputFilePath, Trace trace,
                       VerboseLevel loggingLevel) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.trace = trace;
        this.loggingLevel = loggingLevel;
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

        inMemoryStream = new ByteArrayOutputStream();
        pdfWriter = new PdfWriter(inMemoryStream, new WriterProperties().addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_0));
        StampingProperties stampingProperties = new StampingProperties();
        pdfReader = createPdfReader();
        pdfSigner = new PdfDocumentSigner(pdfReader, pdfWriter, hasSignature ? stampingProperties.useAppendMode() : stampingProperties);

        Calendar signDate = Calendar.getInstance();
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

        if (!isTimestampSignature) {
            // Add 3 Minutes to move signing time within the OnDemand Certificate Validity
            // This is only relevant in case the signature does not include a timestamp
            // See section 5.8.5.1 of the Reference Guide
            // todo It is ok also for the static workflow???
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
//        ReaderProperties readerProperties = new ReaderProperties();
//        return new PdfReader(inputFilePath, Objects.nonNull(pdfPassword) ? readerProperties.setPassword(pdfPassword.getBytes()) : readerProperties);
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
            throw new AisClientException(
                String.format("Could not apply signature because source file contains a certification that does not allow "
                              + "any changes to the document with id %s", trace.getId()));
        }

        if (Soap._debugMode) {
            System.out.println("\nEstimated signature size: " + estimatedSize);
            System.out.println("Actual signature size:      " + externalSignature.length);
            System.out.println("Remaining size:             " + (estimatedSize - externalSignature.length));
        }

        if (estimatedSize < externalSignature.length) {
            throw new AisClientException(String.format("Not enough space for signature in the document with id %s. The estimated size needs to be " +
                                                       " increased with %d bytes.", trace.getId(), externalSignature.length - estimatedSize));
        }

        OutputStream outputStream = null;
        try {
            pdfSigner.signWithAuthorizedSignature(new PdfSignatureContainer(externalSignature), estimatedSize);

            outputFileTempPath = Files.createTempFile("signed", "-temp.pdf").toString();
            outputStream = new FileOutputStream(outputFileTempPath);
            inMemoryStream.writeTo(outputStream);

            if (Soap._debugMode) {
                System.out.println("\nOK writing signature to " + outputFileTempPath);
            }

            pdfDocument.close();
            inMemoryStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to embed the signature in the document - %s", trace.getId()));
        } finally {
            closeResource(outputStream);
        }
    }

    /**
     * Add external revocation information to DSS Dictionary, to enable Long Term Validation (LTV) in Adobe Reader
     *
     * @param encodedOcspEntries List of OCSP response in base64 encoding
     * @param encodedCrlEntries  List of CRL response in base64 encoding
     */
    // todo maybe try to embed the revocation info in the previous step
    public void addValidationInformation(List<String> encodedOcspEntries, List<String> encodedCrlEntries) {
        if (ObjectUtils.allNull(encodedOcspEntries, encodedCrlEntries)) {
            return;
        }

        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new AisClientException("Could not apply revocation information (LTV) to the DSS Dictionary. Document contains a certification "
                                         + " that does not allow any changes.");
        }

        try {
            Collection<byte[]> ocsp = new ArrayList<>();
            Collection<byte[]> crl = new ArrayList<>();

            if (Objects.nonNull(encodedOcspEntries)) {
                for (String encodedOcsp : encodedOcspEntries) {
                    OCSPResp ocspResp = new OCSPResp(new ByteArrayInputStream(Base64.decode(encodedOcsp)));
                    BasicOCSPResp basicResp = (BasicOCSPResp) ocspResp.getResponseObject();

                    if (Soap._debugMode) {
                        System.out.println("\nEmbedding OCSP Response...");
                        System.out.println("Status                : " + ((ocspResp.getStatus() == 0) ? "GOOD" : "BAD"));
                        System.out.println("Produced at           : " + basicResp.getProducedAt());
                        System.out.println("This Update           : " + basicResp.getResponses()[0].getThisUpdate());
                        System.out.println("Next Update           : " + basicResp.getResponses()[0].getNextUpdate());
                        System.out.println("X509 Cert Issuer      : " + basicResp.getCerts()[0].getIssuer());
                        System.out.println("X509 Cert Subject     : " + basicResp.getCerts()[0].getSubject());
                        System.out.println("Certificate ID        : " + basicResp.getResponses()[0].getCertID().getSerialNumber().toString() + " ("
                                           + basicResp.getResponses()[0].getCertID().getSerialNumber().toString(16).toUpperCase() + ")");
                    }

                    ocsp.add(basicResp.getEncoded());
                }
            }

            if (Objects.nonNull(encodedCrlEntries)) {
                for (String encodedCrl : encodedCrlEntries) {
                    X509CRL
                        x509crl =
                        (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(new ByteArrayInputStream(Base64.decode(encodedCrl)));

                    if (Soap._debugMode) {
                        System.out.println("\nEmbedding CRL...");
                        System.out.println("IssuerDN                    : " + x509crl.getIssuerDN());
                        System.out.println("This Update                 : " + x509crl.getThisUpdate());
                        System.out.println("Next Update                 : " + x509crl.getNextUpdate());
                        System.out.println("No. of Revoked Certificates : "
                                           + ((x509crl.getRevokedCertificates() == null) ? "0" : x509crl.getRevokedCertificates().size()));
                    }

                    crl.add(x509crl.getEncoded());
                }
            }

            PdfReader reader = new PdfReader(outputFileTempPath);
            PdfWriter writer = new PdfWriter(outputFilePath);
            PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().preserveEncryption().useAppendMode());
            LtvVerification validation = new LtvVerification(pdfDocument);

            // remove the for-statement because we want to add the revocation information to the latest signature only.
            List<String> signatureNames = new SignatureUtil(pdfDocument).getSignatureNames();
            String signatureName = signatureNames.get(signatureNames.size() - 1);
            // Add the CRL/OCSP validation information to the DSS Dictionary
            boolean addVerification = validation.addVerification(signatureName, ocsp, crl, null);

            validation.merge();

            if (Soap._debugMode) {
                if (addVerification) {
                    System.out.println("\nOK merging LTV validation information to " + outputFilePath);
                } else {
                    System.out.println("\nFAILED merging LTV validation information to " + outputFilePath);
                }
            }

            pdfDocument.close();

            boolean delete = new File(outputFileTempPath).delete();
            if (delete) {
                System.out.println("\nTemp file deleted.");
            }
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
        return java.util.Base64.getEncoder().encodeToString(documentHash);
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
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
            // log this on debug
            System.out.printf("Failed to close the resources. Reason: %s", e.getMessage());
        }
    }
}
