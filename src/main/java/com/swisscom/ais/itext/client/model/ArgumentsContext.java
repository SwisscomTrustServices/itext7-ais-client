package com.swisscom.ais.itext.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArgumentsContext {

    private List<String> inputFiles;
    private String outputFile;
    private String suffix;
    private String configFile;
    private SignatureMode signature;
    private VerboseLevel verboseLevel;

    private ArgumentsContext() {
    }

    public ArgumentsContext(List<String> inputFiles, String outputFile, String suffix, String configFile,
                            SignatureMode signature, VerboseLevel verboseLevel) {
        this.inputFiles = inputFiles;
        this.outputFile = outputFile;
        this.suffix = suffix;
        this.configFile = configFile;
        this.signature = signature;
        this.verboseLevel = verboseLevel;
    }

    public static ArgumentsContext.Builder builder() {
        return new Builder();
    }

    public List<String> getInputFiles() {
        return inputFiles;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getConfigFile() {
        return configFile;
    }

    public SignatureMode getSignature() {
        return signature;
    }

    public VerboseLevel getVerboseLevel() {
        return verboseLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArgumentsContext that = (ArgumentsContext) o;
        return Objects.equals(inputFiles, that.inputFiles) && Objects.equals(outputFile, that.outputFile) && Objects
            .equals(suffix, that.suffix) && Objects.equals(configFile, that.configFile) && signature == that.signature
               && verboseLevel == that.verboseLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputFiles, outputFile, suffix, configFile, signature, verboseLevel);
    }

    @Override
    public String toString() {
        return "ArgumentsContext{" +
               "inputFiles=" + inputFiles +
               ", outputFile='" + outputFile + '\'' +
               ", suffix='" + suffix + '\'' +
               ", configFile='" + configFile + '\'' +
               ", signature=" + signature +
               ", verboseLevel=" + verboseLevel +
               '}';
    }

    public static class Builder {
        private final List<String> inputFiles = new ArrayList<>();
        private String outputFile;
        private String suffix;
        private String configFile;
        private SignatureMode signature;
        private VerboseLevel verboseLevel = VerboseLevel.LOW;

        Builder() {
        }

        public Builder withInputFile(String inputFile) {
            inputFiles.add(inputFile);
            return this;
        }

        public Builder withOutputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder withConfigFile(String configFile) {
            this.configFile = configFile;
            return this;
        }

        public Builder withSignature(SignatureMode signature) {
            this.signature = signature;
            return this;
        }

        public Builder withVerboseLevel(VerboseLevel verboseLevel) {
            if (this.verboseLevel.getImportance() < verboseLevel.getImportance()) {
                this.verboseLevel = verboseLevel;
            }
            return this;
        }

        public List<String> getInputFiles() {
            return inputFiles;
        }

        public String getOutputFile() {
            return outputFile;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getConfigFile() {
            return configFile;
        }

        public SignatureMode getSignature() {
            return signature;
        }

        public VerboseLevel getVerboseLevel() {
            return verboseLevel;
        }

        public ArgumentsContext build() {
            return new ArgumentsContext(inputFiles, outputFile, suffix, configFile, signature, verboseLevel);
        }

        @Override
        public String toString() {
            return "ArgumentsContext.Builder{" +
                   "inputFiles=" + inputFiles +
                   ", outputFile='" + outputFile + '\'' +
                   ", suffix='" + suffix + '\'' +
                   ", configFile='" + configFile + '\'' +
                   ", signature=" + signature +
                   ", verboseLevel=" + verboseLevel +
                   '}';
        }
    }
}
