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
package com.swisscom.ais.itext.client.impl.signer;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class PdfDocumentSigner extends PdfSigner {

    public PdfDocumentSigner(PdfReader reader, OutputStream outputStream, StampingProperties properties) throws IOException {
        super(reader, outputStream, properties);
    }

    public byte[] computeHash(IExternalSignatureContainer externalHashContainer, int estimatedSize) throws GeneralSecurityException, IOException {
        if (closed) {
            throw new PdfException(PdfException.ThisInstanceOfPdfSignerAlreadyClosed);
        }

        PdfSignature signatureDictionary = new PdfSignature();
        PdfSignatureAppearance appearance = getSignatureAppearance();
        signatureDictionary.setReason(appearance.getReason());
        signatureDictionary.setLocation(appearance.getLocation());
        signatureDictionary.setSignatureCreator(appearance.getSignatureCreator());
        signatureDictionary.setContact(appearance.getContact());
        signatureDictionary.setDate(new PdfDate(getSignDate()));
        externalHashContainer.modifySigningDictionary(signatureDictionary.getPdfObject());
        cryptoDictionary = signatureDictionary;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        preClose(exc);

        InputStream dataRangeStream = getRangeStream();
        return externalHashContainer.sign(dataRangeStream);
    }

    public void signWithAuthorizedSignature(IExternalSignatureContainer externalSignatureContainer, int estimatedSize)
        throws GeneralSecurityException, IOException {
        InputStream dataRangeStream = getRangeStream();
        byte[] authorizedSignature = externalSignatureContainer.sign(dataRangeStream);

        if (estimatedSize < authorizedSignature.length) {
            throw new IOException("Not enough space");
        }

        byte[] paddedSignature = new byte[estimatedSize];
        System.arraycopy(authorizedSignature, 0, paddedSignature, 0, authorizedSignature.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSignature).setHexWriting(true));
        close(dic2);

        closed = true;
    }
}
