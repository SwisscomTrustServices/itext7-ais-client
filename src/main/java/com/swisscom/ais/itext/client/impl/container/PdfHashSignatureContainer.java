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
