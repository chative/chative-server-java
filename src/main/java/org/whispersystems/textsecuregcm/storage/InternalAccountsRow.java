package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalAccountsRow {

    @JsonProperty
    private String number;

    @JsonProperty
    private String name;

    @JsonProperty
    private String push_type;

    @JsonProperty
    private String push_token;

    @JsonProperty
    private int vcode;

    @JsonProperty
    private boolean registered;


    @JsonProperty
    private long joinedAt;

    @JsonProperty
    private int invitation_per_day;

    @JsonProperty
    private boolean disabled;

    @JsonProperty
    private boolean deleted;

    @JsonProperty
    private boolean inactive;

    @JsonProperty
    private String okta_id;

    @JsonProperty
    private String pid;

    @JsonProperty
    private String puid;

    @JsonProperty
    private String okta_org;

    @JsonProperty
    private long extId;


    private int inviteRule;
    @JsonProperty
    private boolean supportTransfer;

    public boolean isSupportTransfer() {
        return supportTransfer;
    }

    public InternalAccountsRow() {
    }

    public InternalAccountsRow(String number, String name, String push_type, String push_token, int vcode, boolean registered, int invitation_per_day, boolean disabled,
                                String okta_id,String pid,String puid, String okta_org, boolean inactive,long extId,
                               int inviteRule, boolean supportTransfer, boolean deleted,long joinedAt) {
        this.number = number;
        this.name = name;
        this.push_type = push_type;
        this.push_token = push_token;
        this.vcode = vcode;
        this.registered = registered;
        this.invitation_per_day = invitation_per_day;
        this.disabled = disabled;
        this.okta_id = okta_id;
        this.pid=pid;
        this.puid=puid;
        this.okta_org=okta_org;
        this.inactive=inactive;
        this.extId=extId;
        this.inviteRule=inviteRule;
        this.supportTransfer = supportTransfer;
        this.deleted = deleted;
        this.joinedAt = joinedAt;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPush_type() {
        return push_type;
    }

    public String getPush_token() {
        return push_token;
    }

    public int getVcode() {
        return vcode;
    }

    public boolean isRegistered() {
        return registered;
    }
    public long getJoinedAt() {
        return joinedAt;
    }
    public int getInvitation_per_day() {
        return invitation_per_day;
    }

    public boolean isDisabled() {
        return disabled;
    }


    public String getOktaId() {
        return okta_id;
    }

    public String getPid() {
        return pid;
    }

    public String getPuid() {
        return puid;
    }

    public String getOkta_org() {
        return okta_org;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public long getExtId() {
        return Account.ExtType.DEFAULT.getId();
        //return extId;
    }

    public void setExtId(long extId) {
        this.extId = extId;
    }


    public boolean isDeleted() {
        return deleted;
    }

    public int getInviteRule() {
        return inviteRule;
    }
}
