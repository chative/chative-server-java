package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalAccountsInvitationRow {

    @JsonProperty
    private String code;

    @JsonProperty
    private String inviter;

    @JsonProperty
    private long timestamp;

    @JsonProperty
    private long register_time;

    @JsonProperty
    private String account;

    @JsonProperty
    private String name;

    @JsonProperty
    private String orgs;

    @JsonProperty
    private String emailHash;

    @JsonProperty
    private String phoneHash;

    @JsonProperty
    private String okta_id;

    @JsonProperty
    private String okta_org;

    public InternalAccountsInvitationRow() {
    }

    public InternalAccountsInvitationRow(String code, String inviter, long timestamp, long register_time, String account, String name, String orgs, String emailHash, String phoneHash, String okta_id, String okta_org) {
        this.code = code;
        this.inviter = inviter;
        this.timestamp = timestamp;
        this.register_time = register_time;
        this.account = account;
        this.name = name;
        this.orgs = orgs;
        this.emailHash = emailHash;
        this.phoneHash = phoneHash;
        this.okta_id = okta_id;
        this.okta_org=okta_org;
    }

    public String getCode() {
        return code;
    }

    public String getInviter() {
        return inviter;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getRegister_time() {
        return register_time;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getOrgs() {
        return orgs;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public String getOkta_id() {
        return okta_id;
    }

    public String getOkta_org() {
        return okta_org;
    }
}
