package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupMessageEncInfoRes {


    static  public  class GroupMessageEncInfo {
        @JsonProperty
        private  String  uid;
        @JsonProperty
        private  String  identityKey;

        @JsonProperty
        private  int registrationId;

        public  GroupMessageEncInfo( String  uid,  String  identityKey, int registrationId){
            this .uid = uid;
            this .identityKey = identityKey;
            this .registrationId = registrationId;
        }
    }

    public GroupMessageEncInfoRes(List<GroupMessageEncInfo> keys, int msgEncVersion) {
        this.keys = keys;
        this.msgEncVersion = msgEncVersion;
    }

    @JsonProperty
    private List<GroupMessageEncInfo> keys;
    @JsonProperty
    private  int msgEncVersion;

}
