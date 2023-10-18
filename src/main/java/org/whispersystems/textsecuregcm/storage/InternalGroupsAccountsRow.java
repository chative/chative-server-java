package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalGroupsAccountsRow {

    @JsonProperty
    private String group_name;

    @JsonProperty
    private String account_number;

    public InternalGroupsAccountsRow() {
    }

    public InternalGroupsAccountsRow(String group_name, String account_number) {
        this.group_name = group_name;
        this.account_number = account_number;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getAccount_number() {
        return account_number;
    }
}

