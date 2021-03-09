package com.swisscom.ais.itext.client.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ArgumentsContext {

    private final List<String> inputFiles = new LinkedList<>();
    private String outputFile;
    private String suffix;
    private String configFile;
    private SignatureType signatureType;
    private String distinguishedName;
    private String stepUpMsisdn;
    private String stepUpMsg;
    private String stepUpLang;
    private String stepUpSerialNo;
    private String reason;
    private String location;
    private String contact;
    private String certificationLevel;
    private VerbosityLevel verbosityLevel = VerbosityLevel.LOW;

    public void addInputFile(String inputFile) {
        inputFiles.add(inputFile);
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setSignatureType(SignatureType signatureType) {
        this.signatureType = signatureType;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public void setStepUpMsisdn(String stepUpMsisdn) {
        this.stepUpMsisdn = stepUpMsisdn;
    }

    public void setStepUpMsg(String stepUpMsg) {
        this.stepUpMsg = stepUpMsg;
    }

    public void setStepUpLang(String stepUpLang) {
        this.stepUpLang = stepUpLang;
    }

    public void setStepUpSerialNo(String stepUpSerialNo) {
        this.stepUpSerialNo = stepUpSerialNo;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCertificationLevel(String certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public void setVerbosityLevel(VerbosityLevel verbosityLevel) {
        if (this.verbosityLevel.getImportance() < verbosityLevel.getImportance()) {
            this.verbosityLevel = verbosityLevel;
        }
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
               && Objects.equals(stepUpMsg, that.stepUpMsg) && Objects.equals(stepUpLang, that.stepUpLang) && Objects
                   .equals(stepUpSerialNo, that.stepUpSerialNo) && Objects.equals(reason, that.reason) && Objects
                   .equals(location, that.location) && Objects.equals(contact, that.contact) && Objects
                   .equals(certificationLevel, that.certificationLevel) && verbosityLevel == that.verbosityLevel;
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(inputFiles, outputFile, suffix, configFile, signatureType, distinguishedName, stepUpMsisdn, stepUpMsg, stepUpLang, stepUpSerialNo,
                  reason, location, contact, certificationLevel, verbosityLevel);
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
               ", stepUpMsg='" + stepUpMsg + '\'' +
               ", stepUpLang='" + stepUpLang + '\'' +
               ", stepUpSerialNo='" + stepUpSerialNo + '\'' +
               ", reason='" + reason + '\'' +
               ", location='" + location + '\'' +
               ", contact='" + contact + '\'' +
               ", certificationLevel='" + certificationLevel + '\'' +
               ", verbosityLevel=" + verbosityLevel +
               '}';
    }
}
