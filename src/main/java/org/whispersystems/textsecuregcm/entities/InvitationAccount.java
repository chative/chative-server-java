package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

public class InvitationAccount {

    @NotEmpty
    @JsonProperty
    private String account;

    @JsonProperty
    private String vcode;

    @JsonProperty
    private String inviter;

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public InvitationAccount() {
    }

    public InvitationAccount(String account, String vcode) {
        this.account = account;
        this.vcode = vcode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }
}
