package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class GetPushToken {

    @NotEmpty
    @JsonProperty
    private String type;

    @JsonProperty
    private String token;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GetPushToken() {
    }

    public GetPushToken(String type, String token) {
        this.type = type;
        this.token = token;
    }
}
