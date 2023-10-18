package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class TDTokenRequest {
    public String getToken() {
        return token;
    }

    @JsonProperty
    @NotEmpty
    String token;
}
