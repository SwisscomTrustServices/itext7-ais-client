package com.swisscom.ais.itext;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import com.itextpdf.io.codec.Base64;
import com.swisscom.ais.itext.container.PdfHashSignatureContainer;
import com.swisscom.ais.itext.container.PdfSignatureContainer;
import com.swisscom.ais.itext.signer.PdfDocumentSigner;

import javax.annotation.Nonnull;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;

import java.io.*;
import java.nio.file.Files;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.*;

public class PDF implements Closeable {

    /**
     * Save file path from input file
     */
    private String inputFilePath;

    /**
     * Save file path from output file
     */
    private String outputFilePath;
    private String outputFileTempPath;

    /**
     * Save password from pdf
     */
    private String pdfPassword;

    /**
     * Save signing reason
     */
    private int certificationLevel = 0;

    /**
     * Save signing reason
     */
    private String signReason;

    /**
     * Save signing location
     */
    private String signLocation;

    /**
     * Save signing contact
     */
    private String signContact;

    /**
     * Save PdfReader
     */
    private PdfReader pdfReader;

    private PdfWriter pdfWriter;

    private PdfDocumentSigner pdfSigner;

    private PdfDocument pdfDocument;
    /**
     * Save signature appearance from pdf
     */
    private PdfSignatureAppearance pdfSignatureAppearance;

    /**
     * Save pdf signature
     */
    private PdfSignature pdfSignature;

    /**
     * Save byte array output stream for writing pdf file
     */
    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * Set parameters
     *
     * @param inputFilePath  Path from input file
     * @param outputFilePath Path from output file
     * @param pdfPassword    Password form pdf
     * @param signReason     Reason from signing
     * @param signLocation   Location for frOn signing
     * @param signContact    Contact for signing
     */
    PDF(@Nonnull String inputFilePath, @Nonnull String outputFilePath, String pdfPassword, String signReason, String signLocation, String signContact,
        int certificationLevel) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.pdfPassword = pdfPassword;
        this.signReason = signReason;
        this.signLocation = signLocation;
        this.signContact = signContact;
        this.certificationLevel = certificationLevel;
    }

    /**
     * Get file path of pdf to sign
     *
     * @return Path from pdf to sign
     */
    public String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * Add signature information (reason for signing, location, contact, date) and create hash from pdf document
     *
     * @param signDate        Date of signing
     * @param estimatedSize   The estimated size for signatures
     * @param hashAlgorithm   The hash algorithm which will be used to sign the pdf
     * @param isTimestampOnly If it is a timestamp signature. This is necessary because the filter is an other one compared to a "standard" signature
     * @return Hash of pdf as bytes
     */
    public byte[] getPdfHash(@Nonnull Calendar signDate, int estimatedSize, @Nonnull String hashAlgorithm, boolean isTimestampOnly) throws Exception {
        pdfDocument = new PdfDocument(createPdfReader());
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        boolean hasSignature = signatureUtil.getSignatureNames().size() > 0;

        byteArrayOutputStream = new ByteArrayOutputStream();
        pdfWriter = new PdfWriter(byteArrayOutputStream, new WriterProperties().addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_0));
        StampingProperties stampingProperties = new StampingProperties();
        pdfReader = createPdfReader();
        pdfSigner = new PdfDocumentSigner(pdfReader, pdfWriter, hasSignature ? stampingProperties.useAppendMode() : stampingProperties);

        pdfSignatureAppearance = pdfSigner.getSignatureAppearance()
            .setReason(getOptionalAttribute(signReason))
            .setLocation(getOptionalAttribute(signLocation))
            .setContact(getOptionalAttribute(signContact));
        pdfSigner.setSignDate(signDate);

        if (certificationLevel > 0) {
            // check: at most one certification per pdf is allowed
            if (pdfSigner.getCertificationLevel() != PdfSigner.NOT_CERTIFIED) {
                throw new Exception(
                    "Could not apply -certlevel option. At most one certification per pdf is allowed, but source pdf contained already a certification.");
            }
            pdfSigner.setCertificationLevel(certificationLevel);
        }

        Map<PdfName, PdfObject> signatureDictionary = new HashMap<>();
        signatureDictionary.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        signatureDictionary.put(PdfName.SubFilter, isTimestampOnly ? PdfName.ETSI_RFC3161 : PdfName.ETSI_CAdES_DETACHED);

        PdfHashSignatureContainer hashSignatureContainer = new PdfHashSignatureContainer(hashAlgorithm, new PdfDictionary(signatureDictionary));
        return pdfSigner.computeHash(hashSignatureContainer, estimatedSize);
    }

    private String getOptionalAttribute(String attribute) {
        return Objects.nonNull(attribute) ? attribute : "";
    }

    private PdfReader createPdfReader() throws IOException {
        ReaderProperties readerProperties = new ReaderProperties();
        return new PdfReader(inputFilePath, Objects.nonNull(pdfPassword) ? readerProperties.setPassword(pdfPassword.getBytes()) : readerProperties);
    }

    /**
     * Add a signature to pdf document
     *
     * @param externalSignature The extern generated signature
     * @param estimatedSize     Size of external signature
     */
    public void createSignedPdf(@Nonnull byte[] externalSignature, int estimatedSize) throws Exception {
        // Check if source pdf is not protected by a certification
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new Exception(
                "Could not apply signature because source file contains a certification that does not allow any changes to the document");
        }

        if (Soap._debugMode) {
            System.out.println("\nEstimated SignatureSize: " + estimatedSize);
            System.out.println("Actual    SignatureSize: " + externalSignature.length);
            System.out.println("Remaining Size         : " + (estimatedSize - externalSignature.length));
        }

        if (estimatedSize < externalSignature.length) {
            throw new IOException("\nNot enough space for signature (" + (estimatedSize - externalSignature.length) + " bytes)");
        }

        pdfSigner.signWithAuthorizedSignature(new PdfSignatureContainer(externalSignature), estimatedSize);

        outputFileTempPath = Files.createTempFile("signed", "-temp.pdf").toString();
        OutputStream outputStream = new FileOutputStream(outputFileTempPath);
        byteArrayOutputStream.writeTo(outputStream);

        if (Soap._debugMode) {
            System.out.println("\nOK writing signature to " + outputFileTempPath);
        }

        pdfDocument.close();
        byteArrayOutputStream.close();
        outputStream.close();
    }

    /**
     * Add external revocation information to DSS Dictionary, to enable Long Term Validation (LTV) in Adobe Reader
     *
     * @param ocspArr List of OCSP Responses as base64 encoded String
     * @param crlArr  List of CRLs as base64 encoded String
     */
    public void addValidationInformation(ArrayList<String> ocspArr, ArrayList<String> crlArr) throws Exception {
        if (ocspArr == null && crlArr == null) {
            return;
        }

        // Check if source pdf is not protected by a certification
        if (pdfSigner.getCertificationLevel() == PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED) {
            throw new Exception(
                "Could not apply revocation information (LTV) to the DSS Dictionary. Document contains a certification that does not allow any changes.");
        }

        Collection<byte[]> ocsp = new ArrayList<>();
        Collection<byte[]> crl = new ArrayList<>();

        // Decode each OCSP Response (String of base64 encoded form) and add it to the Collection (byte[])
        if (ocspArr != null) {
            for (String ocspBase64 : ocspArr) {
                OCSPResp ocspResp = new OCSPResp(new ByteArrayInputStream(Base64.decode(ocspBase64)));
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

                ocsp.add(basicResp.getEncoded()); // Add Basic OCSP Response to Collection (ASN.1 encoded representation of this object)
            }
        }

        // Decode each CRL (String of base64 encoded form) and add it to the Collection (byte[])
        if (crlArr != null) {
            for (String crlBase64 : crlArr) {
                X509CRL x509crl = (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(new ByteArrayInputStream(Base64.decode(crlBase64)));

                if (Soap._debugMode) {
                    System.out.println("\nEmbedding CRL...");
                    System.out.println("IssuerDN                    : " + x509crl.getIssuerDN());
                    System.out.println("This Update                 : " + x509crl.getThisUpdate());
                    System.out.println("Next Update                 : " + x509crl.getNextUpdate());
                    System.out.println("No. of Revoked Certificates : "
                                       + ((x509crl.getRevokedCertificates() == null) ? "0" : x509crl.getRevokedCertificates().size()));
                }

                crl.add(x509crl.getEncoded()); // Add CRL to Collection (ASN.1 DER-encoded form of this CRL)
            }
        }

        PdfReader reader = new PdfReader(outputFileTempPath);
        PdfWriter writer = new PdfWriter(outputFilePath);
        PdfDocument pdfDocument = new PdfDocument(reader, writer, new StampingProperties().preserveEncryption().useAppendMode());
        LtvVerification validation = new LtvVerification(pdfDocument);

        // remove the for-statement because we want to add the recovation information to the latest signature only.
        List<String> signatureNames = new SignatureUtil(pdfDocument).getSignatureNames();
        String signatureName = signatureNames.get(signatureNames.size() - 1);
        // Add the CRL/OCSP validation information to the DSS Dictionary
        boolean addVerification = validation.addVerification(signatureName, ocsp, crl, null);

        validation.merge(); // Merges the validation with any validation already in the document or creates a new one.

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
    }

    @Override
    public void close() {
        try {
            pdfReader.close();
            pdfWriter.close();
        } catch (IOException e) {
            System.err.printf("Failed to close PDF reader or writer. Reason: %s", e.getMessage());
        }
    }
}
