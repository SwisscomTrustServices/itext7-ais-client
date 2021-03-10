package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.CliArgument;
import com.swisscom.ais.itext.client.model.SignatureType;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.utils.FileUtils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ArgumentsService {

    public static final String DEFAULT_SUFFIX = "-signed-#time";
    private static final String ARG_PREFIX_1 = "-";
    private static final String ARG_PREFIX_2 = "--";
    private static final String VERSION_INFO_PLACEHOLDER = "${versionInfo}";
    private static final String DEFAULT_CONFIG_FILE = "sign-pdf.properties";
    private static final List<ImmutablePair<String, String>> CONFIG_PROPERTIES_FILES = Arrays.asList(
        ImmutablePair.of("/cli/sign-pdf-sample.properties", "sign-pdf.properties"),
        ImmutablePair.of("/cli/sign-pdf-help.properties", "sign-pdf-help.properties"),
        ImmutablePair.of("/cli/logback-sample.xml", "logback.xml"));
    private static final String ILLEGAL_ARGUMENT_MESSAGE = "The provided argument '%s' is not recognized. "
                                                           + "Use -help argument to see the detailed options.";
    private static final String TRANSACTION_ID_PLACEHOLDER = "#TRANSID#";
    private final ClientVersionProvider versionProvider;
    private boolean isVerboseLevelActive = false;

    public ArgumentsService(ClientVersionProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    public Optional<ArgumentsContext> parseArguments(String[] args, String startDirPath) {
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
                    argumentsContext.setVerboseLevel(VerboseLevel.BASIC);
                    isVerboseLevelActive = true;
                    break;
                }
                case MEDIUM_VERBOSITY: {
                    argumentsContext.setVerboseLevel(VerboseLevel.MEDIUM);
                    isVerboseLevelActive = true;
                    break;
                }
                case HIGH_VERBOSITY: {
                    argumentsContext.setVerboseLevel(VerboseLevel.HIGH);
                    isVerboseLevelActive = true;
                    break;
                }
                case INPUT: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::addInputFile, "Input file name is missing.", this::validateInputFile);
                    break;
                }
                case OUTPUT: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setOutputFile, "Output file name is missing.",
                                             outputFileName -> validateOutputFile(outputFileName, argumentsContext.getInputFiles()));
                    break;
                }
                case SUFFIX: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setSuffix, "Suffix value is missing.");
                    break;
                }
                case CONFIG: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setConfigFile,
                                             "Config file name is missing. Use the -init argument to provide the sample config files.",
                                             this::validateInputFile);
                    break;
                }
                case SIGNATURE_TYPE: {
                    extractAndAssignArgValue(argsIterator,
                                             signatureTypeValue -> setSignatureType(signatureTypeValue, argumentsContext::setSignatureType),
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
                case STEP_UP_MESSAGE: {
                    extractAndAssignArgValue(argsIterator, stepUpMessage -> setStepUpMessage(stepUpMessage, argumentsContext::setStepUpMessage),
                                             "StepUpMsg value is missing.");
                    break;
                }
                case STEP_UP_LANGUAGE: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpLanguage, "StepUpLang value is missing.");
                    break;
                }
                case STEP_UP_SERIAL_NO: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setStepUpSerialNo, "StepUpSerialNo value is missing.");
                    break;
                }
                case SIGNATURE_REASON: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setSignatureReason, "Signature reason value is missing.");
                    break;
                }
                case SIGNATURE_LOCATION: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setSignatureLocation, "Signature location value is missing.");
                    break;
                }
                case SIGNATURE_CONTACT_INFO: {
                    extractAndAssignArgValue(argsIterator, argumentsContext::setSignatureContactInfo, "Signature contact value is missing.");
                    break;
                }
                case CERTIFICATION_LEVEL: {
                    extractAndAssignArgValue(argsIterator,
                                             certificationLevel -> setCertificationLevel(certificationLevel, argumentsContext::setCertificationLevel),
                                             "Certification level value is missing.");
                    break;
                }
            }
        }
        validateContext(argumentsContext);
        return Optional.of(argumentsContext);
    }

    public boolean isVerboseLevelActive() {
        return isVerboseLevelActive;
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

    private void showHelp() {
        String usageText = FileUtils.readUsageText();
        if (versionProvider.isVersionInfoAvailable()) {
            usageText = usageText.replace(VERSION_INFO_PLACEHOLDER, versionProvider.getVersionInfo());
        }
        System.out.println(usageText);
    }

    private void extractAndAssignArgValue(Iterator<String> argsIterator, Consumer<String> argValueConsumer, String validationErrorMsg) {
        extractAndAssignArgValue(argsIterator, argValueConsumer, validationErrorMsg, null);
    }

    private void extractAndAssignArgValue(Iterator<String> argsIterator, Consumer<String> argValueConsumer, String validationErrorMsg,
                                          Consumer<String> argValueValidator) {
        if (argsIterator.hasNext()) {
            String argValue = argsIterator.next();
            if (Objects.nonNull(argValueValidator)) {
                argValueValidator.accept(argValue);
            }
            argValueConsumer.accept(argValue);
        } else {
            printErrorMessage(validationErrorMsg);
        }
    }

    private void validateInputFile(String fileName) {
        File pdfToSign = new File(fileName);
        if (!pdfToSign.isFile() || !pdfToSign.canRead()) {
            String message = String.format("File %s is not a file or can not be read.", pdfToSign.getAbsolutePath());
            printErrorMessage(message, true);
        }
    }

    private void validateOutputFile(String outputFileName, List<String> inputFileNames) {
        String message = null;
        Optional<String> filesOverlap = inputFileNames.stream().filter(inputFileName -> inputFileName.equals(outputFileName)).findFirst();
        if (filesOverlap.isPresent()) {
            message = "Source file equals target file.";
        } else if (new File(outputFileName).isFile()) {
            message = "Target file already exists.";
        } else {
            try {
                new File(outputFileName);
            } catch (Exception e) {
                message = "Can not create target file in given path.";
            }
        }
        printErrorMessage(message, true);
    }

    private void setSignatureType(String signatureTypeValue, Consumer<SignatureType> signatureTypeConsumer) {
        Optional<SignatureType> signatureType = SignatureType.getByTypeValue(signatureTypeValue);
        if (!signatureType.isPresent()) {
            String message = String.format(ILLEGAL_ARGUMENT_MESSAGE, signatureTypeValue);
            printErrorMessage(message, true);
        }
        signatureType.ifPresent(signatureTypeConsumer);
    }

    private void setStepUpMessage(String stepUpMessage, Consumer<String> argValueConsumer) {
        argValueConsumer.accept(stepUpMessage.replaceAll(TRANSACTION_ID_PLACEHOLDER, UUID.randomUUID().toString()));
    }

    private void setCertificationLevel(String certificationLevel, Consumer<Integer> consumer) {
        try {
            int level = Integer.parseInt(certificationLevel);
            if (level < 1 || level > 3) {
                printErrorMessage(String.format("Provided certification level value '%s' is not 1, 2 or 3.", certificationLevel));
            } else {
                consumer.accept(level);
            }
        } catch (NumberFormatException e) {
            printErrorMessage(String.format("Provided certification level value '%s' is not 1, 2 or 3.", certificationLevel));
        }
    }

    private void validateContext(ArgumentsContext context) {
        if (Objects.isNull(context.getConfigFile())) {
            context.setConfigFile(DEFAULT_CONFIG_FILE);
        }
        if (ObjectUtils.allNull(context.getOutputFile(), context.getSuffix())) {
            context.setSuffix(DEFAULT_SUFFIX);
        }
        if (context.getInputFiles().isEmpty()) {
            printErrorMessage("Input file name is missing.", true);
        }
        if (ObjectUtils.allNotNull(context.getOutputFile(), context.getSuffix())) {
            printErrorMessage("Both output and suffix are configured. Only one of them can be used.", true);
        }
        if (Objects.nonNull(context.getOutputFile()) && !context.getInputFiles().isEmpty()) {
            printErrorMessage("Cannot use output with multiple input files. Please use suffix instead.", true);
        }
        if (Objects.isNull(context.getSignatureType())) {
            printErrorMessage("Signature type is missing.", true);
        }
    }

    private void printErrorMessage(String error) {
        printErrorMessage(error, false);
    }

    private void printErrorMessage(String error, boolean shouldThrowException) {
        if (StringUtils.isNotBlank(error)) {
            if (isVerboseLevelActive) {
                System.out.println(String.join(StringUtils.SPACE, "Error:", error, "Use -help argument to see the detailed options."));
            }
            if (shouldThrowException) {
                throw new IllegalArgumentException(error);
            }
        }
    }
}
