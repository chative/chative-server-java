package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.validation.constraints.NotNull;

public class RedisSentinelConfiguration {

    @JsonProperty
    @NotNull
    private String password;

    @JsonProperty
    @NotNull
    private List<String> sentinels;

    public String getPassword() { return password; }

    public List<String> getSentinels() {
        return sentinels;
    }
}
