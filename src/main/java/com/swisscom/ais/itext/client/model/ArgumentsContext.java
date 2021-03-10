package com.swisscom.ais.itext.client.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ArgumentsContext {

    private final List<String> inputFiles = new LinkedList<>();
    private String outputFile;
    private String suffix;
    private String configFile;
    private String distinguishedName;
    private String stepUpMsisdn;
    private String stepUpMessage;
    private String stepUpLanguage;
    private String stepUpSerialNo;
    private SignatureType signatureType;
    private String signatureReason;
    private String signatureLocation;
    private String signatureContactInfo;
    private int certificationLevel;
    private VerboseLevel verboseLevel = VerboseLevel.LOW;

    public List<String> getInputFiles() {
        return inputFiles;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public SignatureType getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(SignatureType signatureType) {
        this.signatureType = signatureType;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getStepUpMsisdn() {
        return stepUpMsisdn;
    }

    public void setStepUpMsisdn(String stepUpMsisdn) {
        this.stepUpMsisdn = stepUpMsisdn;
    }

    public String getStepUpMessage() {
        return stepUpMessage;
    }

    public void setStepUpMessage(String stepUpMessage) {
        this.stepUpMessage = stepUpMessage;
    }

    public String getStepUpLanguage() {
        return stepUpLanguage;
    }

    public void setStepUpLanguage(String stepUpLanguage) {
        this.stepUpLanguage = stepUpLanguage;
    }

    public String getStepUpSerialNo() {
        return stepUpSerialNo;
    }

    public void setStepUpSerialNo(String stepUpSerialNo) {
        this.stepUpSerialNo = stepUpSerialNo;
    }

    public String getSignatureReason() {
        return signatureReason;
    }

    public void setSignatureReason(String signatureReason) {
        this.signatureReason = signatureReason;
    }

    public String getSignatureLocation() {
        return signatureLocation;
    }

    public void setSignatureLocation(String signatureLocation) {
        this.signatureLocation = signatureLocation;
    }

    public String getSignatureContactInfo() {
        return signatureContactInfo;
    }

    public void setSignatureContactInfo(String signatureContactInfo) {
        this.signatureContactInfo = signatureContactInfo;
    }

    public int getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public VerboseLevel getVerboseLevel() {
        return verboseLevel;
    }

    public void setVerboseLevel(VerboseLevel verboseLevel) {
        if (this.verboseLevel.getImportance() < verboseLevel.getImportance()) {
            this.verboseLevel = verboseLevel;
        }
    }

    public void addInputFile(String inputFile) {
        inputFiles.add(inputFile);
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
            .equals(suffix, that.suffix) && Objects.equals(configFile, that.configFile) && signatureType == that.signatureType
               && Objects.equals(distinguishedName, that.distinguishedName) && Objects.equals(stepUpMsisdn, that.stepUpMsisdn)
               && Objects.equals(stepUpMessage, that.stepUpMessage) && Objects.equals(stepUpLanguage, that.stepUpLanguage) && Objects
                   .equals(stepUpSerialNo, that.stepUpSerialNo) && Objects.equals(signatureReason, that.signatureReason) && Objects
                   .equals(signatureLocation, that.signatureLocation) && Objects.equals(signatureContactInfo, that.signatureContactInfo) && Objects
                   .equals(certificationLevel, that.certificationLevel) && verboseLevel == that.verboseLevel;
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(inputFiles, outputFile, suffix, configFile, signatureType, distinguishedName, stepUpMsisdn, stepUpMessage, stepUpLanguage,
                  stepUpSerialNo,
                  signatureReason, signatureLocation, signatureContactInfo, certificationLevel, verboseLevel);
    }

    @Override
    public String toString() {
        return "ArgumentsContext{" +
               "inputFiles=" + inputFiles +
               ", outputFile='" + outputFile + '\'' +
               ", suffix='" + suffix + '\'' +
               ", configFile='" + configFile + '\'' +
               ", signatureType=" + signatureType +
               ", distinguishedName='" + distinguishedName + '\'' +
               ", stepUpMsisdn='" + stepUpMsisdn + '\'' +
               ", stepUpMsg='" + stepUpMessage + '\'' +
               ", stepUpLang='" + stepUpLanguage + '\'' +
               ", stepUpSerialNo='" + stepUpSerialNo + '\'' +
               ", reason='" + signatureReason + '\'' +
               ", location='" + signatureLocation + '\'' +
               ", contact='" + signatureContactInfo + '\'' +
               ", certificationLevel='" + certificationLevel + '\'' +
               ", verbosityLevel=" + verboseLevel +
               '}';
    }
}
