# Configure the AIS client
To use the AIS client, you first have to [obtain it (or build it)](build-or-download.md), then you have to configure it. The way you configure
the client depends a lot on how you plan to use the client and integrate it in your project/setup.

## Properties files
The AIS client can be configured from a Java properties file. Here is an example of such a file:

```properties
# The license file path. Also, env placeholder is supported (e.g. ${AIS-PRIVATE-KEY-SECRET})
license.file=${ITEXT_LICENSE_FILE_PATH}
# The AIS server REST URL for sending the Signature requests
server.rest.signUrl=https://ais.swisscom.com/AIS-Server/rs/v1.0/sign
# The AIS server REST URL for sending the Signature status poll requests (Pending requests)
server.rest.pendingUrl=https://ais.swisscom.com/AIS-Server/rs/v1.0/pending
# The AIS server trusted CA certificate file
server.cert.file=/home/user/ais-server.crt
# --
# The client's private key file (corresponding to the public key attached to the client's certificate)
client.auth.keyFile=/home/user/ais-client.key
# The password of the client's private key. This can be left blank if the private key is not protected with a password.
# Also, env placeholder is supported (e.g. ${AIS-PRIVATE-KEY-SECRET}).
client.auth.keyPassword=secret
# The client's certificate file
client.cert.file=/home/user/ais-client.crt
# The maximum number of connections that the HTTP client used by the AIS client can create and reuse simultaneously
client.http.maxTotalConnections=20
# The maximum number of connections PER ROUTE that the HTTP client used by the AIS client can use
client.http.maxConnectionsPerRoute=10
# The HTTP connection timeout in SECONDS (the maximum time allowed for the HTTP client to wait for the TCP socket connection
# to be established until the request is dropped and the client gives up).
client.http.connectionTimeoutInSeconds=10
# The HTTP response timeout in SECONDS (the maximum time allowed for the HTTP client to wait for the response to be received
# for any one request until the request is dropped and the client gives up).
client.http.responseTimeoutInSeconds=20
# The interval IN SECONDS for the client to poll for signature status (for each parallel request).
client.poll.intervalInSeconds=10
# The total number of rounds (including the first Pending request) that the client runs for each parallel request. After this
# number of rounds of calling the Pending endpoint for an ongoing request, the client gives up and signals a timeout for that
# respective request.
client.poll.rounds=10
# --
# The standard to use for creating the signature.
# Choose from: DEFAULT, CAdES, PDF, PAdES, PAdES-Baseline, PLAIN.
# Leave it empty and the client will use sensible defaults.
signature.standard=PAdES-Baseline
# The type and method of revocation information to receive from the server.
# Choose from: DEFAULT, CAdES, PDF, PAdES, PAdES-Baseline, BOTH, PLAIN.
# Leave it empty and the client will use sensible defaults.
signature.revocationInformation=PAdES
# Whether to add a timestamp to the signature or not. Default is true.
# Leave it empty and the client will use sensible defaults.
signature.addTimestamp=true
# --
# The AIS Claimed Identity name. The right Claimed Identity (and key, see below) must be used for the right signature type.
signature.claimedIdentityName=ais-90days-trial
# The AIS Claimed Identity key. The key together with the name (see above) is used for starting the correct signature type.
signature.claimedIdentityKey=keyEntity
# The client's Subject DN to which the certificate is bound.
signature.distinguishedName=cn=TEST User, givenname=Max, surname=Maximus, c=US, serialnumber=abcdefabcdefabcdefabcdefabcdef
# --
# The language (one of "en", "fr", "de", "it") to be used during the Step Up interaction with the mobile user.
signature.stepUp.language=en
# The MSISDN (in international format) of the mobile user to interact with during the Step Up phase.
signature.stepUp.msisdn=40799999999
# The message to present to the mobile user during the Step Up phase.
signature.stepUp.message=Please confirm the signing of the document
# The mobile user's Serial Number to validate during the Step Up phase. If this number is different than the one registered on the server
# side for the mobile user, the request will fail.
signature.stepUp.serialNumber=
# --
# The name to embed in the signature to be created.
signature.name=TEST Signer
# The reason for this signature to be created.
signature.reason=Testing signature
# The location where the signature is created.
signature.location=Testing location
# The contact info to embed in the signature to be created.
signature.contactInfo=tester.test@test.com
```

Once you create this file and configure its properties accordingly, it can either be picked up by the AIS client when you use it via its 
CLI interface, or you can use it to populate the objects that configure the client.

*CLI usage:*
```shell
./bin/itext7-ais-client.sh -type onDemand-stepUp -config config.properties -input local-sample-doc.pdf -output test-sign.pdf
```

##Programmatic usage
*First approach (using directly the client):*
```java
Properties properties = new Properties();
properties.load(TestClass.class.getResourceAsStream("/config.properties"));

AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(properties).build();
RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();
SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

try (AisClient aisClient = new AisClientImpl(new AisRequestService(), aisConfig, restClient)) {
    UserData userData = new UserData()
        .fromProperties(properties)
        .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
        .withRevocationInformation(RevocationInformation.PADES_BASELINE)
        .withSignatureStandard(SignatureStandard.CADES)
        .build();

    String inputFilePath = properties.getProperty("local.test.inputFile");
    String outputFilePath = properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
    PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath), DigestAlgorithm.SHA256);

    SignatureResult result = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
    System.out.println("Finish to sign the document(s) with the status: " + result);
}
```

*Second approach (using a signing service):*
```java
Properties properties = new Properties();
properties.load(TestOnDemandSignature.class.getResourceAsStream("/local-config.properties"));

AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(properties).build();
RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();
SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

try (AisClient client = new AisClientImpl(new AisRequestService(), aisConfig, restClient)) {
    SigningService signingService = new SigningService(client);

    String inputFilePath = properties.getProperty("local.test.inputFile");     
    String outputFilePath = properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
    PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath));

    UserData userData = new UserData()
        .fromProperties(properties)
        .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
        .build();

    SignatureResult result = signingService.performSignings(Collections.singletonList(document), SignatureMode.ON_DEMAND_WITH_STEP_UP, userData);
    System.out.println("Finish to sign the document(s) with the status: " + result);
}
```

## Programmatic configuration
All the fields that are used for configuring the AIS client can also be populated by hand (or by some other means, e.g. Spring framework).
In order not to repeat content, please see the 
[TestFullyProgrammaticConfiguration](../src/main/java/com/swisscom/ais/itext7/TestFullyProgrammaticConfiguration.java) sample class for how 
this can be implemented.

## Spring way
The AIS client is [Spring framework](https://spring.io/) friendly. While not using Spring as a dependency, it is implemented so that you 
can easily configure it and use it as a Spring bean.

As the section above demonstrated, the AIS client can easily be configured in a programmatic way. This means that the AIS client can be used
as a Spring bean and have its properties be populated in a _Configuration_ bean or via XML configuration.

Moreover, in a [Spring Boot](https://spring.io/projects/spring-boot) setup, you can easily integrate the configuration of the AIS client in
the central _application.yml_ configuration file, using the 
[ConfigurationProvider](../src/main/java/com/swisscom/ais/itext7/client/common/provider/ConfigurationProvider.java) interface.

For example, the following simple _ConfigurationProvider_ implementation will load the AIS client configuration from the Spring Boot's 
_application.yml_ file:

```java
import com.swisscom.ais.client.utils.ConfigurationProvider;
import org.springframework.core.env.Environment;

public class ConfigurationProviderSpringImpl implements ConfigurationProvider {

    private final Environment environment;

    public ConfigurationProviderSpringImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getProperty(String name) {
        // add any prefix here, so that you neatly organize AIS client's config in your application.yml
        return environment.getProperty("swisscom.ais-client." + name);
    }

}
```

So, then, add the following to your _application.yml_ file:

```yaml
swisscom:
  ais-client:
    # The license file path. Also, env placeholder is supported (e.g. ${AIS-PRIVATE-KEY-SECRET})
    license.file: ${ITEXT_LICENSE_FILE_PATH}
    server:
      # The AIS server REST URL for sending the Signature requests
      rest.signUrl: https://ais.swisscom.com/AIS-Server/rs/v1.0/sign
      # The AIS server REST URL for sending the Signature status poll requests (Pending requests)
      rest.pendingUrl: https://ais.swisscom.com/AIS-Server/rs/v1.0/pending
      # The AIS server trusted CA certificate file
      cert.file: /home/user/ais-server.crt
    client:
      # The client's private key file (corresponding to the public key attached to the client's certificate)
      auth.keyFile: /home/user/ais-client.key
      # The password of the client's private key. This can be left blank if the private key is not protected with a password
      # Also, env placeholder is supported (e.g. ${AIS-PRIVATE-KEY-SECRET}).
      auth.keyPassword: secret
      # The client's certificate file
      cert.file: /home/user/ais-client.crt
      # The maximum number of connections that the HTTP client used by the AIS client can create and reuse simultaneously
      http.maxTotalConnections: 20
      # The maximum number of connections PER ROUTE that the HTTP client used by the AIS client can use
      http.maxConnectionsPerRoute: 10
      # The HTTP connection timeout in SECONDS (the maximum time allowed for the HTTP client to wait for the TCP socket connection
      # to be established until the request is dropped and the client gives up).
      http.connectionTimeoutInSeconds: 10
      # The HTTP response timeout in SECONDS (the maximum time allowed for the HTTP client to wait for the response to be received
      # for any one request until the request is dropped and the client gives up).
      http.responseTimeoutInSeconds: 20
      # The interval IN SECONDS for the client to poll for signature status (for each parallel request).
      poll.intervalInSeconds: 10
      # The total number of rounds (including the first Pending request) that the client runs for each parallel request. After this
      # number of rounds of calling the Pending endpoint for an ongoing request, the client gives up and signals a timeout for that
      # respective request.
      poll.rounds: 10
    signature:
      # The standard to use for creating the signature.
      # Choose from: DEFAULT, CAdES, PDF, PAdES, PAdES-Baseline, PLAIN.
      # Leave it empty and the client will use sensible defaults.
      standard: PAdES-Baseline
      # The type and method of revocation information to receive from the server.
      # Choose from: DEFAULT, CAdES, PDF, PAdES, PAdES-Baseline, BOTH, PLAIN.
      # Leave it empty and the client will use sensible defaults.
      revocationInformation: PAdES
      # Whether to add a timestamp to the signature or not. Default is true.
      # Leave it empty and the client will use sensible defaults.
      addTimestamp: true
      # The AIS Claimed Identity name. The right Claimed Identity (and key, see below) must be used for the right signature type.
      claimedIdentityName: ais-90days-trial
      # The AIS Claimed Identity key. The key together with the name (see above) is used for starting the correct signature type.
      claimedIdentityKey: keyEntity
      # The client's Subject DN to which the certificate is bound.
      distinguishedName: "cn: TEST User, givenname: Max, surname: Maximus, c: US, serialnumber: abcdefabcdefabcdefabcdefabcdef"
      # The language (one of "en", "fr", "de", "it") to be used during the Step Up interaction with the mobile user.
      stepUp:
        language: en
        # The MSISDN (in international format) of the mobile user to interact with during the Step Up phase.
        msisdn: 40799999999
        # The message to present to the mobile user during the Step Up phase.
        message: Please confirm the signing of the document
        # The mobile user's Serial Number to validate during the Step Up phase. If this number is different than the one registered on the server
        # side for the mobile user, the request will fail.
        serialNumber: 
      # The name to embed in the signature to be created.
      name: TEST Signer
      # The reason for this signature to be created.
      reason: Testing signature
      # The location where the signature is created.
      location: Testing location
      # The contact info to embed in the signature to be created.
      contactInfo: tester.test@test.com
```