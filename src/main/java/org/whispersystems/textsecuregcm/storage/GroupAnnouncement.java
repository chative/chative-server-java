package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupAnnouncement {

  public static final int MEMCACHE_VERION = 1;

  @JsonProperty
  private String id;

  @JsonProperty
  private String gid;

  @JsonProperty
  private String creator;

  @JsonProperty
  private long createTime;

  @JsonProperty
  private String reviser;

  @JsonProperty
  private long reviseTime;

  @JsonProperty
  private int status;

  @JsonProperty
  private long announcementExpiry;

  @JsonProperty
  private String content;

  public GroupAnnouncement() {
  }

  public GroupAnnouncement(String gid,String creator, int status, long announcementExpiry, String content) {
    this(null,gid,creator, System.currentTimeMillis(), creator, System.currentTimeMillis(), status, announcementExpiry, content);
  }

  public GroupAnnouncement(String id,  String gid, String creator, long createTime, String reviser, long reviseTime, int status, long announcementExpiry, String content) {
    this.id = id;
    this.gid=gid;
    this.creator = creator;
    this.createTime = createTime;
    this.reviser=reviser;
    this.reviseTime=reviseTime;
    this.status = status;
    this.announcementExpiry = announcementExpiry;
    this.content = content;
  }

  public String getId() {
    return id;
  }


  public String getCreator() {
    return creator;
  }

  public long getCreateTime() {
    return createTime;
  }

  public int getStatus() {
    return status;
  }

  public long getAnnouncementExpiry() {
    return announcementExpiry;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setAnnouncementExpiry(long announcementExpiry) {
    this.announcementExpiry = announcementExpiry;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public String getReviser() {
    return reviser;
  }

  public void setReviser(String reviser) {
    this.reviser = reviser;
  }

  public long getReviseTime() {
    return reviseTime;
  }

  public void setReviseTime(long reviseTime) {
    this.reviseTime = reviseTime;
  }
}
