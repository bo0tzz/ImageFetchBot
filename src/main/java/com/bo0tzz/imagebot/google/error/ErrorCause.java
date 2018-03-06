package com.bo0tzz.imagebot.google.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorCause {

    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("locationType")
    @Expose
    private String locationType;
    @SerializedName("location")
    @Expose
    private String location;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ErrorCause [" +
                "\"domain\": " + domain + ", " +
                "\"reason\": " + reason + ", " +
                "\"message\": " + message + ", " +
                "\"locationType\": " + locationType + ", " +
                "\"location\": " + location + "]";
    }
}