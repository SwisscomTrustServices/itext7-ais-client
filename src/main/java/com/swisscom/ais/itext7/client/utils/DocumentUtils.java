package com.swisscom.ais.itext7.client.utils;


import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.model.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentUtils {

    public static List<PdfDocumentHandler> prepareMultipleDocumentsForSigning(List<PdfMetadata> documentsMetadata, SignatureMode signatureMode,
                                                                               SignatureType signatureType, UserData userData, Trace trace, Logger clientLogger) {
        clientLogger.info("Preparing document(s) for signing with {} signature... - {}", signatureMode.getValue(), trace.getId());
        return documentsMetadata.stream()
                .map(docMetadata -> prepareOneDocumentForSigning(docMetadata, signatureMode, signatureType, userData, trace))
                .collect(Collectors.toList());
    }


    public static PdfDocumentHandler prepareOneDocumentForSigning(PdfMetadata documentMetadata, SignatureMode signatureMode, SignatureType signatureType,
                                                            AbstractUserData userData, Trace trace) {
        try {
            PdfDocumentHandler newDocument = new PdfDocumentHandler(documentMetadata.getInputStream(), documentMetadata.getOutputStream(), trace);
            newDocument.prepareForSigning(documentMetadata.getDigestAlgorithm(), signatureType, userData);
            return newDocument;
        } catch (Exception e) {
            throw new AisClientException(String.format("Failed to prepare the document for %s signing - %s",
                    signatureMode.getValue(), trace.getId()), e);
        }
    }
}
