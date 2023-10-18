package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class EmailConfiguration {

    @JsonProperty
    @NotNull
    String server;

    @JsonProperty
    @NotNull
    int port;

    @JsonProperty
    @NotNull
    String username;

    @JsonProperty
    @NotNull
    String password;

    @JsonProperty
    @NotNull
    String from;

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
