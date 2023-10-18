package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetMyGroupsResponse {

  public static class Group {
    @JsonProperty
    private String gid;

    @JsonProperty
    private String name;

    @JsonProperty
    private long messageExpiry;

    @JsonProperty
    private String avatar;

    @JsonProperty
    private int status;

    @JsonProperty
    private int invitationRule;

    @JsonProperty
    private int version;
    @JsonProperty
    private String remindCycle;

    @JsonProperty
    private boolean anyoneRemove;

    @JsonProperty
    private boolean rejoin;

    @JsonProperty
    private boolean ext;

    @JsonProperty
    private int publishRule;
    public Group(String gid, String name, long messageExpiry, String avatar,int status,int invitationRule,int version,String remindCycle,boolean anyoneRemove,boolean rejoin,boolean ext, int publishRule) {
      this.gid = gid;
      this.name = name;
      this.messageExpiry = messageExpiry;
      this.avatar = avatar;
      this.status = status;
      this.invitationRule=invitationRule;
      this.version=version;
      this.remindCycle=remindCycle;
      this.anyoneRemove=anyoneRemove;
      this.rejoin=rejoin;
      this.ext=ext;
      this.publishRule=publishRule;
    }
  }

  @JsonProperty
  private List<Group> groups;

  public GetMyGroupsResponse(List<Group> groups) {
    this.groups = groups;
  }
}
