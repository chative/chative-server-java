package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ESLogConfig {
    public String getEndPoint() {
        return endPoint;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getMyServerIP() {
        return myServerIP;
    }

    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty
    @NotNull
    String endPoint;

    @JsonProperty
    @NotNull
    String userName;

    @JsonProperty
    @NotNull
    String password;

    @JsonProperty
    String myServerIP;

    @JsonProperty
    String serviceName;

    public String getDefaultIndexName() {
        return defaultIndexName;
    }

    @JsonProperty
    String defaultIndexName;
}
