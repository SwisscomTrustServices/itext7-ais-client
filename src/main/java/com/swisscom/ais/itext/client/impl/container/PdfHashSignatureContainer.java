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
package com.swisscom.ais.itext.client.impl.container;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class PdfHashSignatureContainer implements IExternalSignatureContainer {

    private final String hashAlgorithm;
    private final PdfDictionary signatureDictionary;

    public PdfHashSignatureContainer(String hashAlgorithm, PdfDictionary signatureDictionary) {
        this.hashAlgorithm = hashAlgorithm;
        this.signatureDictionary = signatureDictionary;
    }

    @Override
    public byte[] sign(InputStream rangeStream) throws GeneralSecurityException {
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        return digestRangeInputStream(rangeStream, messageDigest);
    }

    @Override
    public void modifySigningDictionary(PdfDictionary existingSignatureDictionary) {
        existingSignatureDictionary.putAll(signatureDictionary);
    }

    private byte[] digestRangeInputStream(InputStream rangeStream, MessageDigest messageDigest) {
        try {
            return DigestAlgorithms.digest(rangeStream, messageDigest);
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
