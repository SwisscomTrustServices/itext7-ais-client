package com.swisscom.ais.itext.client;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;

import java.io.Closeable;
import java.util.List;

/**
 * Sign multiple PDF documents using the following signature modes:
 * <ul>
 *     <li>static</li>
 *     <li>on demand</li>
 *     <li>on demand with step up</li>
 *     <li>timestamp</li>
 * </ul>
 */
public interface AisClient extends Closeable {

    /**
     * Sign multiple PDF documents with <em>static certificate</em>.
     *
     * @param documentsMetadata the documents metadata
     * @param userData          the user specific data which will be used to sign a document (e.g. the signature claimed identity name or key). See
     *                          {@link UserData}.
     * @return the status of the signature result
     * @throws AisClientException if the signature acquisition process from the AIS service fails
     */
    SignatureResult signWithStaticCertificate(List<PdfMetadata> documentsMetadata, UserData userData);

    /**
     * Sign multiple PDF documents with <em>on demand certificate</em>.
     *
     * @param documentsMetadata the documents metadata
     * @param userData          the user specific data which will be used to sign a document (e.g. the signature claimed identity name or key). See
     *                          {@link UserData}.
     * @return the status of the signature result
     * @throws AisClientException if the signature acquisition process from the AIS service fails
     */
    SignatureResult signWithOnDemandCertificate(List<PdfMetadata> documentsMetadata, UserData userData);

    /**
     * Sign multiple PDF documents with <em>on demand certificate and step up</em>.
     *
     * @param documentsMetadata the documents metadata
     * @param userData          the user specific data which will be used to sign a document (e.g. the signature claimed identity name or key). See
     *                          {@link UserData}.
     * @return the status of the signature result
     * @throws AisClientException if the signature acquisition process from the AIS service fails
     */
    SignatureResult signWithOnDemandCertificateAndStepUp(List<PdfMetadata> documentsMetadata, UserData userData);

    /**
     * Sign multiple PDF documents with <em>timestamp</em>.
     *
     * @param documentsMetadata the documents metadata
     * @param userData          the user specific data which will be used to sign a document (e.g. the signature claimed identity name or key). See
     *                          {@link UserData}.
     * @return the status of the signature result
     * @throws AisClientException if the signature acquisition process from the AIS service fails
     */
    SignatureResult signWithTimestamp(List<PdfMetadata> documentsMetadata, UserData userData);

}
