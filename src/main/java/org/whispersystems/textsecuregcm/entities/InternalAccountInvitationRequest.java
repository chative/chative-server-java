package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class InternalAccountInvitationRequest {

    @NotNull
    @JsonProperty
    private String name;

    @JsonProperty
    private String client;

    public InternalAccountInvitationRequest() {
    }

    public InternalAccountInvitationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getClient() {
        return client;
    }

}
