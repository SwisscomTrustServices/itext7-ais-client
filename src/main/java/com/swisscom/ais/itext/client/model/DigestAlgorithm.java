package com.swisscom.ais.itext.client.model;

import com.swisscom.ais.itext.client.common.AisClientException;

import java.util.Arrays;

public enum DigestAlgorithm {

    SHA256("SHA-256", "http://www.w3.org/2001/04/xmlenc#sha256"),
    SHA384("SHA-384", "http://www.w3.org/2001/04/xmldsig-more#sha384"),
    SHA512("SHA-512", "http://www.w3.org/2001/04/xmlenc#sha512");

    /**
     * Name of the algorithm (to be used with Java CE / security provider).
     */
    private final String digestAlgorithm;

    /**
     * Uri of the algorithm (to be used in the AIS API).
     */
    private final String digestUri;

    /**
     * Set name and uri of hash algorithm
     *
     * @param digestAlgorithm Name of hash algorithm
     * @param digestUri       Uri of hash algorithm
     */
    DigestAlgorithm(String digestAlgorithm, String digestUri) {
        this.digestAlgorithm = digestAlgorithm;
        this.digestUri = digestUri;
    }

    public static DigestAlgorithm getByValue(String algorithmValue) {
        return Arrays.stream(values())
            .filter(item -> item.getDigestAlgorithm().equalsIgnoreCase(algorithmValue))
            .findFirst()
            .orElseThrow(() -> new AisClientException(String.format("Invalid digest algorithm value: %s", algorithmValue)));
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public String getDigestUri() {
        return digestUri;
    }

}