package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingAuthToken {

  @JsonProperty
  private int ver;

  @JsonProperty
  private int iat;

  @JsonProperty
  private String uid;

  public MeetingAuthToken(int ver, int iat, String uid) {
    this.ver = ver;
    this.iat = iat;
    this.uid = uid;
  }

  public int getVer() {
    return ver;
  }

  public int getIat() {
    return iat;
  }

  public String getUid() {
    return uid;
  }
}
