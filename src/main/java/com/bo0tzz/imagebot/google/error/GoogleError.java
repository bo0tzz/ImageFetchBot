package com.bo0tzz.imagebot.google.error;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleError {

    @SerializedName("errors")
    @Expose
    private List<ErrorCause> errors = null;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("message")
    @Expose
    private String message;

    public List<ErrorCause> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorCause> errors) {
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {

        return "GoogleError [" +
                "\"errors\": [" + errors.stream().map(ErrorCause::toString).collect(Collectors.joining(", ")) + "], " +
                "\"code\": " + code + ", " +
                "\"message\": " + message + "]";

    }
}