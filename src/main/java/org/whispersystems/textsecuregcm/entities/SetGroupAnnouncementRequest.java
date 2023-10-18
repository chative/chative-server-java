package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SetGroupAnnouncementRequest {

  @JsonProperty
  private Long announcementExpiry;

  @JsonProperty
  private String content;

  public Optional<String> getContent() {
    return Optional.ofNullable(content);
  }


  public Optional<Long> getAnnouncementExpiry() {
    return Optional.ofNullable(announcementExpiry);
  }

}
