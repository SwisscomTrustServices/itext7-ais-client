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
package com.swisscom.ais.itext7.client.model;

import com.swisscom.ais.itext7.client.utils.ValidationUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class PdfMetadata {

    private InputStream inputStream;
    private OutputStream outputStream;
    private DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA512;

    public PdfMetadata() {
    }

    public PdfMetadata(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public PdfMetadata(InputStream inputStream, OutputStream outputStream, DigestAlgorithm digestAlgorithm) {
        this(inputStream, outputStream);
        this.digestAlgorithm = digestAlgorithm;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public void validate(Trace trace) {
        ValidationUtils.notNull(inputStream, "The document input stream cannot be null", trace);
        ValidationUtils.notNull(outputStream, "The document output stream cannot be null", trace);
        ValidationUtils.notNull(digestAlgorithm, "The digest algorithm for a PDF handle cannot be NULL", trace);
    }
}
