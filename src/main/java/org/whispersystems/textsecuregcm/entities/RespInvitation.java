package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class RespInvitation {

    @JsonProperty
    private String code;

    @JsonProperty
    private int total;

    @JsonProperty
    private int remaining;

    public RespInvitation() {
    }

    public RespInvitation(String code, int total, int remaining) {
        this.code = code;
        this.total = total;
        this.remaining = remaining;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
