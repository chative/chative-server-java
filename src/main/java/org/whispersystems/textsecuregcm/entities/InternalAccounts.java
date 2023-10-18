package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.validation.constraints.NotNull;

public class InternalAccounts {
    @JsonProperty
    private long directoryVersion;
    @NotNull
    @JsonProperty
    private List<InternalAccount> accounts;

    public List<InternalAccount> getAccounts() {
        return accounts;
    }

    public InternalAccounts() {
    }

    public InternalAccounts(List<InternalAccount> accounts,long directoryVersion) {
        this.directoryVersion=directoryVersion;
        this.accounts = accounts;
    }
}
