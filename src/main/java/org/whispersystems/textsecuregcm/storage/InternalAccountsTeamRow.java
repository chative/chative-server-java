package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalAccountsTeamRow extends InternalAccountsRow{



    @JsonProperty
    private String team_id;

    @JsonProperty
    private String team_name;

    public InternalAccountsTeamRow() {
    }

    public InternalAccountsTeamRow(String number, String name, String push_type, String push_token, int vcode, boolean registered, int invitation_per_day, boolean disabled,
                                    String okta_id, String team_id, String team_name,String pid,String puid,String okta_org,
                                   boolean inactive,long extId,boolean  supportTransfer,boolean deleted,long joinedAt) {
        super(number, name, push_type, push_token, vcode, registered, invitation_per_day,disabled,  okta_id,pid,puid,
                okta_org,inactive,extId,0,supportTransfer,deleted,joinedAt);
        this.team_id = team_id;
        this.team_name = team_name;
    }

    public String getTeamId() {
        return team_id;
    }

    public String getTeamName() {
        return team_name;
    }
}
