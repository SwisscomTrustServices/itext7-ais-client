/**
 * Created:
 * 18.12.13 KW 51 10:42
 * </p>
 * Last Modification:
 * 18.02.2014 13:47
 * <p/>
 * Version:
 * 1.0.0
 * </p>
 * Copyright:
 * Copyright (C) 2013. All rights reserved.
 * </p>
 * License:
 * Licensed under the Apache License, Version 2.0 or later; see LICENSE.md
 * </p>
 * Author:
 * Swisscom (Schweiz) AG
 * </p>
 * **********************************************************************************************************
 * This is a wrapper class for the 'Soap' class                                                             *
 * Only program arguments will be handled                                                                   *
 * At least 'Soap' will be called with arguments                                                        *
 * **********************************************************************************************************
 */

package com.swisscom.ais.itext.client;

import com.swisscom.ais.itext.Include;
import com.swisscom.ais.itext.Soap;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.service.ArgumentsService;
import com.swisscom.ais.itext.client.utils.FileUtils;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class SignPdfCli {

    /**
     * The value is used to decide if verbose information should be print
     */
    private static boolean verboseMode = false;

    /**
     * The value is used to decide if debug information should be print
     */
    private static boolean debugMode = false;

    /**
     * The signature type. E.g. timestamp, sign, ...
     */
    private static Include.Signature signature = null;

    /**
     * Path to pdf which get a signature
     */
    private static String pdfToSign = null;

    /**
     * Path to output document with generated signature
     */
    private static String signedPDF = null;

    /**
     * Reason for signing a document.
     */
    private static String signingReason = null;

    /**
     * Location where a document was signed
     */
    private static String signingLocation = null;

    /**
     * Person who signed the document
     */
    private static String signingContact = null;

    /**
     * Certification Level
     */
    private static int certificationLevel = 0;

    /**
     * Distinguished name contains information about signer. Needed for ondemand signature
     */
    private static String distinguishedName = null;

    /**
     * Mobile phone number to send a message when signing a document. Needed for signing with MobileID/PwdOTP.
     */
    private static String msisdn = null;

    /**
     * Message which will be send to mobile phone with mobile id. Needed for signing with MobileID/PwdOTP.
     */
    private static String msg = null;

    /**
     * Language of the message which will be send to the mobile phone (or shown under the consent URL). Needed for signing with MobileID/PwdOTP.
     */
    private static String language = null;

    /**
     * MobileID/PwdOTP Serial Number
     */
    private static String serialnumber = null;

    /**
     * Path for properties file. Needed if standard path will not be used.
     */
    private static String propertyFilePath = null;

    /**
     * Main method to start AIS. This will parse given parameters e.g. input file, output file etc. and start signature
     * process. Furthermore this method prints error message if signing failed. See usage part in README to know how to
     * use it.
     *
     * @param args Arguments that will be parsed. See useage part in README for more details.
     */
    public static void main(String[] args) {
        ClientVersionProvider versionProvider = new ClientVersionProvider();
        versionProvider.init();

        SignPdfCli ais = new SignPdfCli();
        try {
            Optional<ArgumentsContext>
                argumentsContext =
                new ArgumentsService(versionProvider).parseArguments(args, new File(StringUtils.EMPTY).getAbsolutePath());
            ais.runSigning(args);
        } catch (Exception e) {
            if (debugMode || verboseMode) {
                e.printStackTrace();
            }
            System.exit(1);
        }

    }

    /**
     * Parse given parameters, check if all necessary parameters exist and if there are not unnecessary parameters.
     * If there are problems with parameters application will abort with exit code 1.
     * After all checks are done signing process will start.
     *
     * @param args argument list as described for main method
     */
    public void runSigning(String[] args) throws Exception {
        parseArguments(args);
        checkNecessaryArguments();
        checkUnnecessaryArguments();

        //parse signature
        if (signature.equals(Include.Signature.SIGN) && distinguishedName != null) {
            signature = Include.Signature.ONDEMAND;
        } else if (signature.equals(Include.Signature.SIGN) && distinguishedName == null) {
            signature = Include.Signature.STATIC;
        }

        //start signing
        if (propertyFilePath == null) {
            System.err.println("Property File not found. Add '-config=VALUE'-parameter with correct path");
        }

        Soap dss_soap = new Soap(verboseMode, debugMode, propertyFilePath);
        dss_soap
            .sign(signature, pdfToSign, signedPDF, signingReason, signingLocation, signingContact, certificationLevel, distinguishedName, msisdn, msg,
                  language, serialnumber);
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

    /**
     * Parse given arguments. If an error occurs application with exit with code 1.
     * If debug and/or verbose mode is set, an error message will be shown.
     */
    private void parseArguments(String[] args) throws Exception {

        // args can never be null. It would just be of size zero.

        String param;
        boolean type = false, infile = false, outfile = false;
        for (int i = 0; i < args.length; i++) {

            param = args[i].toLowerCase();

            if (param.contains("-type=")) {
                String signatureString = null;
                try {
                    signatureString = args[i].substring(args[i].indexOf("=") + 1).trim().toUpperCase();
                    signature = Include.Signature.valueOf(signatureString);
                    type = true;
                } catch (IllegalArgumentException e) {
                    if (debugMode || verboseMode) {
                        printError(signatureString + " is not a valid signature.");
                    }
                    showHelp();
                }
            } else if (param.contains("-infile=")) {
                pdfToSign = args[i].substring(args[i].indexOf("=") + 1).trim();
                File pdfToSignFile = new File(pdfToSign);
                if (!pdfToSignFile.isFile() || !pdfToSignFile.canRead()) {
                    if (debugMode || verboseMode) {
                        printError("File " + pdfToSign + " is not a file or can not be read.");
                    }
                    throw new Exception("File " + pdfToSign + " is not a file or can not be read.");
                }
                infile = true;
            } else if (param.contains("-outfile=")) {
                signedPDF = args[i].substring(args[i].indexOf("=") + 1).trim();
                String errorMsg = null;
                if (signedPDF.equals(pdfToSign)) {
                    errorMsg = "Source file equals target file.";
                } else if (new File(signedPDF).isFile()) {
                    errorMsg = "Target file exists.";
                } else {
                    try {
                        new File(signedPDF);
                    } catch (Exception e) {
                        errorMsg = "Can not create target file in given path.";
                    }
                }
                if (errorMsg != null) {
                    if (debugMode || verboseMode) {
                        printError(errorMsg);
                    }
                    throw new Exception(errorMsg);
                }
                outfile = true;
            } else if (param.contains("-reason")) {
                signingReason = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-location")) {
                signingLocation = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-contact")) {
                signingContact = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-certlevel")) {
                try {
                    certificationLevel = Integer.parseInt(args[i].substring(args[i].indexOf("=") + 1).trim());
                    if (certificationLevel < 1 || certificationLevel > 3) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    if (debugMode || verboseMode) {
                        showHelp("-certlevel value not between 1..3");
                    }
                }
            } else if (param.contains("-dn=")) {
                distinguishedName = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-stepupmsisdn=")) {
                msisdn = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-stepupmsg=")) {
                msg = args[i].substring(args[i].indexOf("=") + 1).trim();
                String transId = getNewTransactionId();
                msg = msg.replaceAll("#TRANSID#", transId);
            } else if (param.contains("-stepuplang=")) {
                language = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-stepupserialnumber=")) {
                serialnumber = args[i].substring(args[i].indexOf("=") + 1).trim();
            } else if (param.contains("-config=")) {
                propertyFilePath = args[i].substring(args[i].indexOf("=") + 1).trim();
                File propertyFile = new File(propertyFilePath);
                if (!propertyFile.isFile() || !propertyFile.canRead()) {
                    if (debugMode || verboseMode) {
                        printError("Property file path is set but file does not exist or can not read it: " + propertyFilePath);
                    }
                    throw new Exception("Property file path is set but file does not exist or can not read it: " + propertyFilePath);
                }
            } else if (args[i].toLowerCase().contains("-vv")) {
                debugMode = true;
            } else if (param.contains("-v")) {
                verboseMode = true;
            }
        }

        // Check existence of mandatory arguments
        if (!type) {
            showHelp("Mandatory option -type is missing");
        } else if (!infile) {
            showHelp("Mandatory option -infile is missing");
        } else if (!outfile) {
            showHelp("Mandatory option -outfile is missing");
        }

    }

    /**
     * Check if needed parameters are given. If not method will print an error and exit with code 1
     */
    private void checkNecessaryArguments() throws Exception {

        if (pdfToSign == null) {
            if (debugMode || verboseMode) {
                printError("Input file does not exist.");
            }
            throw new Exception("Input file does not exist.");
        }

        if (signedPDF == null) {
            if (debugMode || verboseMode) {
                printError("Output file does not exist.");
            }
            throw new Exception("Output file does not exist.");
        }
    }

    /**
     * This method checks if there are unnecessary parameters. If there are some it will print the usage of parameters
     * and exit with code 1 (e.g. DN is given for signing with timestamp)
     */
    private void checkUnnecessaryArguments() {

        if (signature.equals(Include.Signature.TIMESTAMP)) {
            if (distinguishedName != null || msisdn != null || msg != null || language != null) {
                if (debugMode || verboseMode) {
                    showHelp();
                }
            }
        } else {
            if (!(distinguishedName == null && msisdn == null && msg == null && language == null ||
                  distinguishedName != null && msisdn == null && msg == null && language == null ||
                  distinguishedName != null && msisdn != null && msg != null && language != null)) {
                if (debugMode || verboseMode) {
                    showHelp();
                }
            }
        }
    }

    /**
     * Return a unique transaction id
     *
     * @return transaction id
     */
    private String getNewTransactionId() {
        // secure, easy but a little bit more expensive way to get a random alphanumeric string
        return new BigInteger(30, new SecureRandom()).toString(32);
    }

}
