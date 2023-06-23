package com.swisscom.ais.itext7.client;

import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.model.ETSIUserData;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.signresp.etsi.SignResponse;

import java.io.Closeable;

public interface ETSIAisClient extends Closeable {
    SignResponse signOnDemandWithETSI(PdfDocumentHandler pdfDocument, ETSIUserData userData, Trace trace, String tokenForETSISigning);
}
