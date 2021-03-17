package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ResultMajor",
    "ResultMinor",
    "ResultMessage"
})
public class Result {

    @JsonProperty("ResultMajor")
    private String resultMajor;
    @JsonProperty("ResultMinor")
    private String resultMinor;
    @JsonProperty("ResultMessage")
    private ResultMessage resultMessage;

    @JsonProperty("ResultMajor")
    public String getResultMajor() {
        return resultMajor;
    }

    @JsonProperty("ResultMajor")
    public void setResultMajor(String resultMajor) {
        this.resultMajor = resultMajor;
    }

    public Result withResultMajor(String resultMajor) {
        this.resultMajor = resultMajor;
        return this;
    }

    @JsonProperty("ResultMinor")
    public String getResultMinor() {
        return resultMinor;
    }

    @JsonProperty("ResultMinor")
    public void setResultMinor(String resultMinor) {
        this.resultMinor = resultMinor;
    }

    public Result withResultMinor(String resultMinor) {
        this.resultMinor = resultMinor;
        return this;
    }

    @JsonProperty("ResultMessage")
    public ResultMessage getResultMessage() {
        return resultMessage;
    }

    @JsonProperty("ResultMessage")
    public void setResultMessage(ResultMessage resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Result withResultMessage(ResultMessage resultMessage) {
        this.resultMessage = resultMessage;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Result.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("resultMajor");
        sb.append('=');
        sb.append(((this.resultMajor == null) ? "<null>" : this.resultMajor));
        sb.append(',');
        sb.append("resultMinor");
        sb.append('=');
        sb.append(((this.resultMinor == null) ? "<null>" : this.resultMinor));
        sb.append(',');
        sb.append("resultMessage");
        sb.append('=');
        sb.append(((this.resultMessage == null) ? "<null>" : this.resultMessage));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
