package com.swisscom.ais.itext.container;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.signatures.IExternalSignatureContainer;

import java.io.InputStream;
import java.security.GeneralSecurityException;

public class PdfSignatureContainer implements IExternalSignatureContainer {

    private final byte[] authorizedSignature;

    public PdfSignatureContainer(byte[] authorizedSignature) {
        this.authorizedSignature = authorizedSignature;
    }

    @Override
    public byte[] sign(InputStream rangeStream) throws GeneralSecurityException {
        return authorizedSignature;
    }

    @Override
    public void modifySigningDictionary(PdfDictionary existingSignatureDictionary) {
    }
}
