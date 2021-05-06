# Use the AIS client programmatically
The client library can be used as a normal project dependency, allowing your project to access
the document signing and timestamping features provided by the AIS service.

## Dependency configuration
For Maven projects, add the following in your _POM_ file:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <!-- ... -->
    
    <repositories>
        <repository>
            <id>swisscom-itext7-ais-client</id>
            <name>Swisscom iText7 AIS client</name>
            <url>https://raw.githubusercontent.com/SwisscomTrustServices/itext7-ais/main/repository</url>
        </repository>
    </repositories>
    
    <!-- ... -->

    <dependencies>
        <dependency>
            <groupId>com.swisscom.ais</groupId>
            <artifactId>itext7-ais</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

For Gradle projects, add the following in your _build.gradle_ file:
```groovy
plugins {
    id 'java'
}

// ...

repositories {
    mavenCentral()
    maven {
        url 'https://raw.githubusercontent.com/SwisscomTrustServices/itext7-ais/main/repository'
    }
}

dependencies {
    compile 'com.swisscom.ais:itext7-ais:1.0.0'
    // ...
}
```

## Using the library
This section describes the usage of the library in code. See the sample files 
in the [root source folder](../src/main/java/com/swisscom/ais/itext7) for complete examples of how to use the library in code.

First create the configuration objects, one for the REST client 
([RestClientConfiguration](../src/main/java/com/swisscom/ais/itext7/client/rest/RestClientConfiguration.java)) and one for the AIS client 
([AISClientConfiguration](../src/main/java/com/swisscom/ais/itext7/client/config/AisClientConfiguration.java)). This needs to be done once per 
application lifetime, as the AIS client, once it is created and properly configured, can be reused over and over for each incoming request. It is 
implemented in a thread-safe way and makes use of proper HTTP connection pooling in order to correctly reuse resources.

Configure the REST client:
```java
RestClientConfiguration restConfig = RestClientConfiguration.builder()
    .withServiceSignUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/sign")
    .withServicePendingUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/pending")
    // the server certificate file is optional, in case it is omitted the CA must be a trusted one
    .withServerCertificateFile("/home/user/ais-server.crt")
    .withClientKeyFile("/home/user/ais-client.key")
    .withClientKeyPassword("secret")
    .withClientCertificateFile("/home/user/ais-client.crt")
    .build();

SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);
```

Then configure the AIS client:
```java
AisClientConfiguration aisConfig = new AisClientConfiguration(10, 10, "${ITEXT_LICENSE_FILE_PATH}");
```

Finally, create the AIS client with these objects:
```java
try (AisClient aisClient = new AisClientImpl(aisConfig, restClient)) {
    // use the client here
}
```

The above example makes use of Java's _try-with-resources_ feature. If you don't use the client like this, just make sure you call its _close()_
method once you are done with it (e.g. at the shutdown of your application). Don't call this method after each request!

Once the client is up and running, you can request it to sign and/or timestamp documents. For this, a 
[UserData](../src/main/java/com/swisscom/ais/itext7/client/model/UserData.java) object is needed, to specify all the details required for the signature
or timestamp.

```java
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
    .withRevocationInformation(RevocationInformation.PADES_BASELINE)
    .withSignatureStandard(SignatureStandard.PDF)
    .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
    .build();
```

The last line is quite interesting. If you go with the _On Demand signature with Step Up_, there is a Consent URL that is generated and that
needs to be passed to the mobile user, so that he or she can access it, authenticate there and confirm the signature. The _UserData_ class
allows you to define a callback object that is invoked as soon as the URL is generated and received by the client. In the example above
the URL is just printed in the _STDOUT_ stream, but in your case you might want to display it to the user by other means (web, mobile UI, etc).
Keep in mind that this callback is performed EACH TIME the consent URL is received. For a signature request that goes into pending/polling mode,
this will happen each time the response comes back from the server. 

Third, you need one object (or more) that identifies the document to sign and/or timestamp. More than one document can be signed/timestamped at
a time.

```java
PdfMetadata document = new PdfMetadata(new FileInputStream("/home/user/input.pdf"), 
                                       new FileOutputStream("/home/user/signed-output.pdf"), DigestAlgorithm.SHA256);
```

Finally, use all these objects to create the signature:

```java
SignatureResult result = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
if (result == SignatureResult.SUCCESS) {
    // yay!
}
```

The [returned result](../src/main/java/com/swisscom/ais/itext7/client/model/SignatureResult.java) is a coder-friendly way of finding how the signature went. 
As long as the signature terminates as caused by the mobile user (success, user cancel, user timeout) then the AIS client gracefully returns a result. 
If some other error is encountered, the client throws an [AisClientException](../src/main/java/com/swisscom/ais/itext7/client/common/AisClientException.java).
