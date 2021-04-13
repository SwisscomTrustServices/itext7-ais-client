# On PAdES Long Term Validation support

_PAdES_ (PDF Advanced Electronic Signatures) is a set of restrictions and extensions to PDF making it suitable 
for Electronic Signatures.

* The signature is included in a data structure in the PDF as a CMS binary encoded object
* _Validation Data_ is the data necessary to validate an electronic signature: CA Certificate(s), OCSP, CRL
* A _LTV (Long-Term Validation)_ signature is valid after the signing certificate is expired, even after 
  the Validation Data is not available online anymore
  
## How to issue PAdES LTV signatures with Swisscom's All-In Signing Service 
When using the Swisscom AIS service directly, via the exposed REST API, the following steps must be taken to ensure support
for PAdES LTV signatures:

* the _SignatureStandard_ element must be set to _PAdES-Baseline_ in order for AIS to correctly process and embed 
  in the signature object the corresponding attributes as defined by the standard
* the _AddTimestamp_ element must be present, in order for the timestamp to be included in the signature
* the _AddRevocationInformation_ element must be present, so the validation information is delivered by the service
  in the Signing Response
* for CMS signatures (both static and on-demand) the type of the _AddRevocationInformation_ is not necessary, 
  since it will automatically match the defined signature standard

Here is an example of an AIS Sign Request to trigger a PAdES LTV signature:
```json
{
    "SignRequest": {
        "@Profile": "http://ais.swisscom.ch/1.1",
        "@RequestID": "ID-e43649cb-d814-4644-ae69-b2f018bc16fb",
        "InputDocuments": {
            ...
        },
        "OptionalInputs": {
            "AddTimestamp": {
                "@Type": "urn:ietf:rfc:3161"
            },
            "AdditionalProfile": [
                "http://ais.swisscom.ch/1.0/profiles/ondemandcertificate",
                "http://ais.swisscom.ch/1.1/profiles/redirect",
                "urn:oasis:names:tc:dss:1.0:profiles:asynchronousprocessing"
            ],
            "ClaimedIdentity": {
                "Name": "..."
            },
            "SignatureType": "urn:ietf:rfc:3369",
            "sc.AddRevocationInformation": {},
            "sc.CertificateRequest": {
                ...
            },
            "sc.SignatureStandard": "PAdES-Baseline"
        }
    }
}
```

After a successful signature, the returned response looks like this:
```json
{
    "SignResponse": {
        "@Profile": "http://ais.swisscom.ch/1.1",
        "@RequestID": "ID-e43649cb-d814-4644-ae69-b2f018bc16fb",
        "OptionalOutputs": {
            "sc.APTransID": "ID-71d075ed-78a7-45fa-a7b5-389d5bbd657d",
            "sc.RevocationInformation": {
                "sc.CRLs": {
                    "sc.CRL": "MIIG...PZcDg=="
                },
                "sc.OCSPs": {
                    "sc.OCSP": "MIII...HEuXA=="
                }
            },
            "sc.StepUpAuthorisationInfo": {
                "sc.Result": {
                    "sc.SerialNumber": "SAS01182dz4w06itj"
                }
            }
        },
        "Result": {
            "ResultMajor": "urn:oasis:names:tc:dss:1.0:resultmajor:Success"
        },
        "SignatureObject": {
            "Base64Signature": {
                "$": "MIJh...wcS5c",
                "@Type": "urn:ietf:rfc:3369"
            }
        }
    }
}
```

In the response above, please take note of the 
_SignResponse - OptionalOutputs - sc.RevocationInformation_ node, which contains the 
OCSP and CRL entries that need to be embedded in the final PDF, together with the digital 
signature. The server might return one OCSP and one CRL content, like in the example above,
or multiple entries for each one of them.

## Final processing of PDF

### To ensure the signature is LTV enabled

You must ensure that the Validation Information is included in the document. Considerations:

* The __signature__ validation information must be available in the document
* The __timestamp__ validation information must also be available in the document
* For PAdES signatures, the Validation Information is embedded in the signature object as an unauthenticated attribute
* The validation information for both the signature and the timestamp are delivered as separated objects in the _**OptionalOutputs**_ element
* It's up to the signing application (i.e. the one invoking the service) to embed this information in the PDF

The delivered OCSPs and CRLs (see example above) must be included in the DSS dictionary object. 
See the PDF specification for further information.

### To ensure your signed PDF is PAdES B-T compliant

You must set the subfilter as the PAdES-defined "ETSI.CAdES.detached" (and NOT "adbe.pkcs7.detached“).

### To ensure your signed PDF is PAdES LTA compliant

The document must include two timestamps:

* The one included in the signature, this is already ensured by AIS if the "AddTimestamp" element is included in the sign request as described above
* An additional one, issued by timestamping the already signed document sending an additional sign request with timestamp as signature type

## References

- https://documents.swisscom.com/product/1000255-Digital_Signing_Service/Documents/Reference_Guide/Reference_Guide-All-in-Signing-Service-en.pdf
- https://en.wikipedia.org/wiki/PDF
- https://en.wikipedia.org/wiki/PAdES
- http://www.etsi.org/deliver/etsi_ts%5C102700_102799%5C10277804%5C01.01.02_60%5Cts_10277804v010102p.pdf
