# Swisscom iText7 AIS Java client

A Java client library for using the [Swisscom All-in Signing Service (AIS)](https://www.swisscom.ch/en/business/enterprise/offer/security/all-in-signing-service.html)
to sign and/or timestamp PDF documents. The library can be used either as a project dependency or as a command-line tool for batch operations.
It relies on the [iText](https://itextpdf.com/en) library for PDF processing.

## Demo Video

* https://swisscom-my.sharepoint.com/:v:/p/paul_muntean/EVx1PH7CcWxCrReIFIb1Ax4BukjpKN0M-EwauAvtgfDgaw?e=lukdMQ

or alternativelly if you cannot access the first link, you need to download the raw video file using the following link.

* https://github.com/SwisscomTrustServices/itext7-ais/blob/main/iText7%20client%20demo.mp4

## Getting started

To start using the Swisscom AIS service and this client library, do the following:
1. Acquire an [iText license](https://itextpdf.com/en/how-buy)
2. [Get authentication details to use with the AIS client](docs/get-authentication-details.md).
3. [Build or download the AIS client binary package](docs/build-or-download.md)
4. [Configure the AIS client for your use case](docs/configure-the-AIS-client.md)
5. Use the AIS client, either [programmatically](docs/use-the-AIS-client-programmatically.md) or from the [command line](docs/use-the-AIS-client-via-CLI.md)

Other topics of interest might be:
* [On PAdES Long Term Validation support](docs/pades-long-term-validation.md)

## Quick examples

The rest of this page provides some quick examples for using the AIS client. Please see the links
above for detailed instructions on how to get authentication data, download and configure
the AIS client. The following snippets assume that you are already set up.

### Command line usage
Get a help listing by calling the client without any parameters:
```shell
./bin/ais-client.sh
```
or
```shell
./bin/ais-client.sh -help
```
Get a default configuration file set in the current folder using the _-init_ parameter:
```shell
./bin/ais-client.sh -init
```
Apply an On Demand signature with Step Up on a local PDF file:
```shell
./bin/ais-client.sh -type ondemand-stepup -input local-sample-doc.pdf -output test-sign.pdf
```
You can also add the following parameters for extra help:

- _-v_: verbose log output (sets most of the client loggers to debug)
- _-vv_: even more verbose log output (sets all the client loggers to debug, plus the Apache HTTP Client to debug, showing input and output HTTP traffic)
- _-config_: select a custom properties file for configuration (by default it looks for the one named _sign-pdf.properties_)

More than one file can be signed/timestamped at once:
```shell
./bin/ais-client.sh -type ondemand-stepup -input doc1.pdf -input doc2.pdf -input doc3.pdf
```

You don't have to specify the output file:
```shell
./bin/ais-client.sh -type ondemand-stepup -input doc1.pdf
```
The output file name is composed of the input file name plus a configurable _suffix_ (by default it is "-signed-#time", where _#time_
is replaced at runtime with the current date and time). You can customize this suffix:
```shell
./bin/ais-client.sh -type ondemand-stepup -input doc1.pdf -suffix -output-#time 
```

### Programmatic usage
Once you add the AIS client library as a dependency to your project, you can configure it in the following way:
```java
    // configuration for the REST client; this is done once per application lifetime
    RestClientConfiguration restConfig = RestClientConfiguration.builder()
        .withServiceSignUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/sign")
        .withServicePendingUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/pending")
        .withServerCertificateFile("/home/user/ais-server.crt")
        .withClientKeyFile("/home/user/ais-client.key")
        .withClientKeyPassword("secret")
        .withClientCertificateFile("/home/user/ais-client.crt")
        .build();

    SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

    // load the AIS client config; this is done once per application lifetime
    // Use the ${...} placeholder in order to access env vars
    AisClientConfiguration aisConfig = new AisClientConfiguration(10, 10, "${ITEXT_LICENSE_FILE_PATH}");

    try (AisClient aisClient = new AisClientImpl(aisConfig, restClient)) {
        // third, configure a UserData instance with details about this signature
        // this is done for each signature (can also be created once and cached on a per-user basis)
        UserData userData = UserData.builder()
            .withClaimedIdentityName("ais-90days-trial")
            .withClaimedIdentityKey("keyEntity")
            .withDistinguishedName("cn=TEST User, givenname=Max, surname=Maximus, c=US, serialnumber=abcdefabcdefabcdefabcdefabcdef")
            .withStepUpLanguage("en")
            .withStepUpMessage("Please confirm the signing of the document")
            .withStepUpMsisdn("40799999999")
            .withSignatureReason("For testing purposes")
            .withSignatureLocation("Topeka, Kansas")
            .withSignatureContactInfo("test@test.com")
            .withSignatureStandard(SignatureStandard.PDF)
            .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
            .build();

        // fourth, populate a PdfMetadata with details about the document to be signed. More than one PdfMetadata can be given
        PdfMetadata document = new PdfMetadata(new FileInputStream("/home/user/input.pdf"),
                                               new FileOutputStream("/home/user/signed-output.pdf"), DigestAlgorithm.SHA256);

        // finally, do the signature
        SignatureResult result = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
        if (result == SignatureResult.SUCCESS) {
            // yay!
        }
    }
```

## References

- [Swisscom All-In Signing Service homepage](https://www.swisscom.ch/en/business/enterprise/offer/security/all-in-signing-service.html)
- [Swisscom All-In Signing Service reference documentation (PDF)](http://documents.swisscom.com/product/1000255-Digital_Signing_Service/Documents/Reference_Guide/Reference_Guide-All-in-Signing-Service-en.pdf)
- [Swisscom Trust Services documentation](https://trustservices.swisscom.com/en/downloads/)
- [iText library](https://itextpdf.com/en)
