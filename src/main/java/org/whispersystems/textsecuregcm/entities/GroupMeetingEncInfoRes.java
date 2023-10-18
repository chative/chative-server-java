package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupMeetingEncInfoRes {
    public GroupMeetingEncInfoRes(List<GroupMeetingEncInfo> keys, int meetingVersion) {
        this.keys = keys;
        this.meetingVersion = meetingVersion;
    }

    static  public  class GroupMeetingEncInfo {
        @JsonProperty
        private  String  uid;
        @JsonProperty
        private  String  identityKey;

        public  GroupMeetingEncInfo( String  uid,  String  identityKey) {
            this .uid = uid;
            this .identityKey = identityKey;
        }
    }
    @JsonProperty
    private List<GroupMeetingEncInfo> keys;
    @JsonProperty
    private  int meetingVersion;

}
