package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetGroupPinResponse {

  public static class GroupPin {
    @JsonProperty
    private String id;

    @JsonProperty
    private String conversationId;

    @JsonProperty
    private String content;

    @JsonProperty
    private String creator;

    @JsonProperty
    private long createTime;

    public GroupPin(String id, String creator, long createTime, String content, String conversationId) {
      this.id = id;
      this.creator = creator;
      this.createTime = createTime;
      this.content = content;
      this.conversationId = conversationId;
    }
  }

  @JsonProperty
  private String gid;

  @JsonProperty
  private List<GetGroupPinResponse.GroupPin> groupPins;


  public GetGroupPinResponse(String gid, List<GetGroupPinResponse.GroupPin> groupPins) {
    this.gid=gid;
    this.groupPins = groupPins;
  }
}
