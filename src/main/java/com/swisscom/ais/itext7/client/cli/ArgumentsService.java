/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext7.client.cli;

import com.swisscom.ais.itext7.client.model.SignatureMode;
import com.swisscom.ais.itext7.client.model.VerboseLevel;
import com.swisscom.ais.itext7.client.utils.AisObjectUtils;
import com.swisscom.ais.itext7.client.utils.FileUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

class ArgumentsService {

    public static final String DEFAULT_SUFFIX = "-signed-#time";
    private static final String ARG_PREFIX_1 = "-";
    private static final String ARG_PREFIX_2 = "--";
    private static final String VERSION_INFO_PLACEHOLDER = "${versionInfo}";
    private static final String DEFAULT_CONFIG_FILE = "sign-pdf.properties";
    private static final List<ImmutablePair<String, String>> CONFIG_PROPERTIES_FILES = Arrays.asList(
        ImmutablePair.of("/cli/sign-pdf-sample.properties", "sign-pdf.properties"),
        ImmutablePair.of("/cli/sign-pdf-help.properties", "sign-pdf-help.properties"),
        ImmutablePair.of("/cli/logback-sample.xml", "logback.xml"));

    private final ClientVersionProvider versionProvider;
    private boolean isVerboseLevelActive = false;

    public ArgumentsService(ClientVersionProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    public Optional<ArgumentsContext> parseArguments(String[] args, String startDirPath) {
        Iterator<String> argsIterator = Arrays.stream(args).iterator();
        ArgumentsContext.Builder argsContextBuilder = ArgumentsContext.builder();

        while (argsIterator.hasNext()) {
            String currentArg = extractNextArgument(argsIterator);
            CliArgument argument = CliArgument.getByValue(currentArg);

            switch (argument) {
                case INIT: {
                    writeHelperConfigFiles(startDirPath);
                    return Optional.empty();
                }
                case HELP: {
                    showHelp();
                    return Optional.empty();
                }
                case BASIC_VERBOSITY: {
                    argsContextBuilder.withVerboseLevel(VerboseLevel.BASIC);
                    isVerboseLevelActive = true;
                    break;
                }
                case MEDIUM_VERBOSITY: {
                    argsContextBuilder.withVerboseLevel(VerboseLevel.MEDIUM);
                    isVerboseLevelActive = true;
                    break;
                }
                case HIGH_VERBOSITY: {
                    argsContextBuilder.withVerboseLevel(VerboseLevel.HIGH);
                    isVerboseLevelActive = true;
                    break;
                }
                case INPUT: {
                    extractAndAssignArgValue(argsIterator, argsContextBuilder::withInputFile, "Input file name is missing.", this::validateInputFile);
                    break;
                }
                case OUTPUT: {
                    extractAndAssignArgValue(argsIterator, argsContextBuilder::withOutputFile, "Output file name is missing.",
                                             outputFileName -> validateOutputFile(outputFileName, argsContextBuilder.getInputFiles()));
                    break;
                }
                case SUFFIX: {
                    extractAndAssignArgValue(argsIterator, argsContextBuilder::withSuffix, "Suffix value is missing.");
                    break;
                }
                case CONFIG: {
                    extractAndAssignArgValue(argsIterator, argsContextBuilder::withConfigFile,
                                             "Config file name is missing. Use the -init argument to provide the sample config files.",
                                             this::validateInputFile);
                    break;
                }
                case SIGNATURE_TYPE: {
                    extractAndAssignArgValue(argsIterator,
                                             signatureValue -> argsContextBuilder.withSignature(SignatureMode.getByValue(signatureValue)),
                                             "Signature type value is missing");
                    break;
                }
            }
        }
        validateContext(argsContextBuilder);
        return Optional.of(argsContextBuilder.build());
    }

    public boolean isVerboseLevelActive() {
        return isVerboseLevelActive;
    }

    private String extractNextArgument(Iterator<String> argsIterator) {
        String currentArg = argsIterator.next().toLowerCase();
        if (!(currentArg.startsWith(ARG_PREFIX_1) || currentArg.startsWith(ARG_PREFIX_2))) {
            throw new IllegalArgumentException(String.format("Invalid argument provided! The argument '%s' must starts with one of the prefixes: "
                                                             + "'%s' or '%s'.", currentArg, ARG_PREFIX_1, ARG_PREFIX_2));
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
            throw new IllegalArgumentException(validationErrorMsg);
        }
    }

    private void validateInputFile(String fileName) {
        File pdfToSign = new File(fileName);
        if (!pdfToSign.isFile() || !pdfToSign.canRead()) {
            throw new IllegalArgumentException(String.format("File %s is not a file or can not be read.", pdfToSign.getAbsolutePath()));
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
        if (Objects.nonNull(message)) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateContext(ArgumentsContext.Builder context) {
        if (Objects.isNull(context.getConfigFile())) {
            context.withConfigFile(DEFAULT_CONFIG_FILE);
        }
        if (AisObjectUtils.allNull(context.getOutputFile(), context.getSuffix())) {
            context.withSuffix(DEFAULT_SUFFIX);
        }
        if (context.getInputFiles().isEmpty()) {
            throw new IllegalArgumentException("Input file name is missing.");
        }
        if (AisObjectUtils.allNotNull(context.getOutputFile(), context.getSuffix())) {
            throw new IllegalArgumentException("Both output and suffix are configured. Only one of them can be used.");
        }
        if (Objects.nonNull(context.getOutputFile()) && context.getInputFiles().size() > 1) {
            throw new IllegalArgumentException("Cannot use output with multiple input files. Please use suffix instead.");
        }
        if (Objects.isNull(context.getSignature())) {
            throw new IllegalArgumentException("Signature type is missing.");
        }
    }
}
