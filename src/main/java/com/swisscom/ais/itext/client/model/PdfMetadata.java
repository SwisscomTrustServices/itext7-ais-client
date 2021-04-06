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

    public PdfMetadata(String inputFilePath, String outputFilePath, DigestAlgorithm digestAlgorithm) {
        this(inputFilePath, outputFilePath);
        this.digestAlgorithm = digestAlgorithm;
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
        ValidationUtils.notBlank(inputFilePath, "The inputFromFile cannot be null or empty", trace);
        ValidationUtils.notBlank(outputFilePath, "The outputToFile cannot be null or empty", trace);
        ValidationUtils.notNull(digestAlgorithm, "The digest algorithm for a PDF handle cannot be NULL", trace);
    }
}
