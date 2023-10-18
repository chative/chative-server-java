package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class AndroidNotifyRegistrationId {

    @JsonProperty
    @NotEmpty
    private String tpnID;

    @JsonProperty
    private String  fcmID;

    public String getTpnID() {
        return tpnID;
    }

    public void setTpnID(String tpnID) {
        this.tpnID = tpnID;
    }

    public String getFcmID() {
        return fcmID;
    }

    public void setFcmID(String fcmID) {
        this.fcmID = fcmID;
    }
}
