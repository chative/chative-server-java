package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetGroupAnnouncementResponse {

  public static class GroupAnnouncement {
    @JsonProperty
    private String id;

    @JsonProperty
    private long announcementExpiry;

    @JsonProperty
    private String content;

    @JsonProperty
    private long reviseTime;

    public GroupAnnouncement(String id, long announcementExpiry, String content,long reviseTime) {
      this.id = id;
      this.announcementExpiry = announcementExpiry;
      this.content = content;
      this.reviseTime=reviseTime;
    }
  }

  @JsonProperty
  private String gid;

  @JsonProperty
  private List<GetGroupAnnouncementResponse.GroupAnnouncement> groupAnnouncements;


  public GetGroupAnnouncementResponse(String gid, List<GetGroupAnnouncementResponse.GroupAnnouncement> groupAnnouncements) {
    this.gid=gid;
    this.groupAnnouncements = groupAnnouncements;
  }
}
