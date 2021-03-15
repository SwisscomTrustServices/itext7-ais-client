package com.swisscom.ais.itext.client;

import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.Signature;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.service.CliService;
import com.swisscom.ais.itext.client.utils.FileUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class SignPdfCli {

    public static void main(String[] args) {
        CliService cliService = new CliService();
        try {
            Optional<ArgumentsContext> argumentsContext = cliService.buildArgumentsContext(args);
            if (argumentsContext.isPresent()) {
                cliService.prepareSignings(argumentsContext.get());
                cliService.performSignings(argumentsContext.get());
            }
        } catch (Exception e) {
            if (cliService.isVerboseLevelActive()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    /**
     * Parse given parameters, check if all necessary parameters exist and if there are not unnecessary parameters.
     * If there are problems with parameters application will abort with exit code 1.
     * After all checks are done signing process will start.
     */
    public void runSigning(ArgumentsContext argumentsContext) throws Exception {
        // todo add a validation for each signature type before the document is started to be signed
        checkUnnecessaryArguments(argumentsContext);

        //parse signature
//        if (signature.equals(Include.Signature.SIGN) && distinguishedName != null) {
//            signature = Include.Signature.ONDEMAND;
//        } else if (signature.equals(Include.Signature.SIGN) && distinguishedName == null) {
//            signature = Include.Signature.STATIC;
//        }

        //start signing
//        if (propertyFilePath == null) {
//            System.err.println("Property File not found. Add '-config=VALUE'-parameter with correct path");
//        }

//        Soap dss_soap = new Soap(verboseMode, debugMode, propertyFilePath);
//        dss_soap
//            .sign(signature, pdfToSign, signedPDF, signingReason, signingLocation, signingContact, certificationLevel, distinguishedName, msisdn, msg,
//                  language, serialnumber);
    }

    /**
     * This method checks if there are unnecessary parameters. If there are some it will print the usage of parameters
     * and exit with code 1 (e.g. DN is given for signing with timestamp)
     */
    private void checkUnnecessaryArguments(ArgumentsContext context) {

        if (context.getSignatureType().equals(Signature.TIMESTAMP)) {
            if (context.getDistinguishedName() != null || context.getStepUpMsisdn() != null || context.getStepUpMessage() != null
                || context.getStepUpLanguage() != null) {
                if (!context.getVerboseLevel().equals(VerboseLevel.LOW)) {
                    showHelp();
                }
            }
        } else {
            if (!(context.getDistinguishedName() == null && context.getStepUpMsisdn() == null && context.getStepUpMessage() == null
                  && context.getStepUpLanguage() == null ||
                  context.getDistinguishedName() != null && context.getStepUpMsisdn() == null && context.getStepUpMessage() == null
                  && context.getStepUpLanguage() == null ||
                  context.getDistinguishedName() != null && context.getStepUpMsisdn() != null && context.getStepUpMessage() != null
                  && context.getStepUpLanguage() != null)) {
                if (!context.getVerboseLevel().equals(VerboseLevel.LOW)) {
                    showHelp();
                }
            }
        }
    }

    private void showHelp() {
        showHelp(null);
    }

    private void showHelp(String errorMessage) {
        if (Objects.nonNull(errorMessage)) {
            printError(errorMessage);
        }
        String usageText = FileUtils.readUsageText();
//        if (versionProvider.isVersionInfoAvailable()) {
//            usageText = usageText.replace(VERSION_INFO_PLACEHOLDER, versionProvider.getVersionInfo());
//        }
        System.out.println(usageText);
    }

    private void printError(String error) {
        if (StringUtils.isNotBlank(error)) {
            System.out.println("Error: " + error);
        }
    }
}
