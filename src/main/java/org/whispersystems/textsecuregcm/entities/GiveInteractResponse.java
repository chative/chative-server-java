package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.InteractMem;

import java.util.List;

public class GiveInteractResponse {
  @JsonProperty
  long thumbsUpCount;
  @JsonProperty
  List<InteractMem.InteractMemSource> lastSource;
  public GiveInteractResponse(long thumbsUpCount,List<InteractMem.InteractMemSource> lastSource){
    this.thumbsUpCount=thumbsUpCount;
    this.lastSource=lastSource;
  }

  public long getThumbsUpCount() {
    return thumbsUpCount;
  }

  public void setThumbsUpCount(long thumbsUpCount) {
    this.thumbsUpCount = thumbsUpCount;
  }

  public List<InteractMem.InteractMemSource> getLastSource() {
    return lastSource;
  }

  public void setLastSource(List<InteractMem.InteractMemSource> lastSource) {
    this.lastSource = lastSource;
  }
}
