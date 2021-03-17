package com.swisscom.ais.itext.client.model;

import com.swisscom.ais.itext.client.utils.ValidationUtils;

public class PdfMetadata {

    private String inputFilePath;

    private String outputFilePath;

    private DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA512;

    public PdfMetadata() {
    }

    public PdfMetadata(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public void validate(Trace trace) {
        ValidationUtils.notEmpty(inputFilePath, "The inputFromFile cannot be null or empty", trace);
        ValidationUtils.notEmpty(outputFilePath, "The outputToFile cannot be null or empty", trace);
        ValidationUtils.notNull(digestAlgorithm, "The digest algorithm for a PDF handle cannot be NULL", trace);
    }
}
