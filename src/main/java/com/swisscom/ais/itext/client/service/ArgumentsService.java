package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.CliArgument;
import com.swisscom.ais.itext.client.model.SignatureType;
import com.swisscom.ais.itext.client.model.VerbosityLevel;
import com.swisscom.ais.itext.client.utils.FileUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ArgumentsService {

    private static final String ARG_PREFIX_1 = "-";
    private static final String ARG_PREFIX_2 = "--";
    private static final String VERSION_INFO_PLACEHOLDER = "${versionInfo}";

    private static final List<ImmutablePair<String, String>> CONFIG_PROPERTIES_FILES = Arrays.asList(
        ImmutablePair.of("/cli/sign-pdf-sample.properties", "sign-pdf.properties"),
        ImmutablePair.of("/cli/sign-pdf-help.properties", "sign-pdf-help.properties"),
        ImmutablePair.of("/cli/logback-sample.xml", "logback.xml"));

    private static final String ILLEGAL_ARGUMENT_MESSAGE = "The provided argument '%s' is not recognized. "
                                                           + "Use -help argument to see the detailed options.";

    private final ClientVersionProvider versionProvider;
    // todo use this
    private boolean isVerbosityLevelActivated = false;

    public ArgumentsService(ClientVersionProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    public Optional<ArgumentsContext> parseArguments(String[] args, String startDirPath) throws Exception {
        Iterator<String> argsIterator = Arrays.stream(args).iterator();
        ArgumentsContext argumentsContext = new ArgumentsContext();

        while (argsIterator.hasNext()) {
            String currentArg = extractNextArgument(argsIterator);
            Optional<CliArgument> argument = CliArgument.getByArgumentValue(currentArg);
            if (!argument.isPresent()) {
                throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_MESSAGE, currentArg));
            }

            switch (argument.get()) {
                case INIT: {
                    writeHelperConfigFiles(startDirPath);
                    return Optional.empty();
                }
                case HELP: {
                    showHelp();
                    return Optional.empty();
                }
                case BASIC_VERBOSITY: {
                    argumentsContext.setVerbosityLevel(VerbosityLevel.BASIC);
                    isVerbosityLevelActivated = true;
                    break;
                }
                case MEDIUM_VERBOSITY: {
                    argumentsContext.setVerbosityLevel(VerbosityLevel.MEDIUM);
                    isVerbosityLevelActivated = true;
                    break;
                }
                case HIGH_VERBOSITY: {
                    argumentsContext.setVerbosityLevel(VerbosityLevel.HIGH);
                    isVerbosityLevelActivated = true;
                    break;
                }
                case INPUT: {
                    // todo add more checks
                    extractAndAssignArgValue(argsIterator, argumentsContext::addInputFile, "Input file name is missing.");
                    break;
                }
                case OUTPUT: {
                    // todo add more checks
                    extractAndAssignArgValue(argsIterator, argumentsContext::setOutputFile, "Output file name is missing.");
                    break;
                }
                case SUFFIX: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setSuffix, "Suffix value is missing.");
                    break;
                }
                case CONFIG: {
                    // todo add more checks
                    extractAndAssignArgValue(argsIterator, argumentsContext::setConfigFile,
                                             "Config file name is missing. Use the -init argument to provide the sample config files.");
                    break;
                }
                case TYPE: {
                    extractAndAssignArgValue(argsIterator, signatureTypeValue -> setSignatureType(argumentsContext, signatureTypeValue),
                                             "Signature type value is missing");
                    break;
                }
                case DISTINGUISHED_NAME: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setDistinguishedName, "Distinguished name value is missing.");
                    break;
                }
                case STEP_UP_MSISDN: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpMsisdn, "StepUpMsisdn value is missing.");
                    break;
                }
                case STEP_UP_MSG: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpMsg, "StepUpMsg value is missing.");
                    break;
                }
                case STEP_UP_LANG: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpLang, "StepUpLang value is missing.");
                    break;
                }
                case STEP_UP_SERIAL_NO: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpSerialNo, "StepUpSerialNo value is missing.");
                    break;
                }
                case REASON: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setReason, "Reason value is missing.");
                    break;
                }
                case LOCATION: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setLocation, "Location value is missing.");
                    break;
                }
                case CONTACT: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setContact, "Contact value is missing.");
                    break;
                }
                case CERTIFICATION_LEVEL: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setCertificationLevel, "Certification level value is missing.");
                    break;
                }
            }
        }
        return Optional.of(argumentsContext);
    }

    private String extractNextArgument(Iterator<String> argsIterator) {
        String currentArg = argsIterator.next().toLowerCase();
        if (!currentArg.startsWith(ARG_PREFIX_1) || !currentArg.startsWith(ARG_PREFIX_2)) {
            throw new IllegalArgumentException(String.format(
                "Invalid argument provided! The argument '%s' must starts with one of the prefixes: '%s' or '%s'.", currentArg, ARG_PREFIX_1,
                ARG_PREFIX_2));
        }
        return currentArg.startsWith(ARG_PREFIX_1) ? currentArg.substring(1) : currentArg.substring(2);
    }

    private void writeHelperConfigFiles(String startDirPath) {
        CONFIG_PROPERTIES_FILES.forEach(pair -> {
            String inputFile = pair.getLeft();
            String outputFile = pair.getRight();
            String outputPath = String.join("/", startDirPath, outputFile);
            System.out.println();
            if (new File(outputPath).exists()) {
                System.out.printf("File %s already exists! Will not be overridden!", outputFile);
            } else {
                System.out.printf("Writing %s to %s.", outputPath, outputFile);
                FileUtils.writeClasspathFile(inputFile, outputPath);
            }
        });
    }

    private void extractAndAssignArgValue(Iterator<String> argsIterator, Consumer<String> argValueConsumer, String validationErrorMsg) {
        if (argsIterator.hasNext()) {
            argValueConsumer.accept(argsIterator.next());
        } else {
            printErrorMessage(validationErrorMsg);
        }
    }

    private void setSignatureType(ArgumentsContext argumentsContext, String signatureTypeValue) {
        Optional<SignatureType> signatureType = SignatureType.getByTypeValue(signatureTypeValue);
        if (!signatureType.isPresent()) {
            throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_MESSAGE, signatureTypeValue));
        }
        argumentsContext.setSignatureType(signatureType.get());
    }

    private void showHelp() {
        String usageText = FileUtils.readUsageText();
        if (versionProvider.isVersionInfoAvailable()) {
            usageText = usageText.replace(VERSION_INFO_PLACEHOLDER, versionProvider.getVersionInfo());
        }
        System.out.println(usageText);
    }

    private void printErrorMessage(String error) {
        if (StringUtils.isNotBlank(error)) {
            System.out.println(String.join(StringUtils.SPACE, "Error:", error, "Also, use -help argument to see the detailed options."));
        }
    }
}
