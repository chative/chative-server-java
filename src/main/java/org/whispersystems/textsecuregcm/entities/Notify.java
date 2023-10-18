package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notify {

  @JsonProperty
  private int notifyType;

  @JsonProperty
  private long notifyTime;

  @JsonProperty
  private String content;

  @JsonProperty
  private Notify.NodifyData data;

  @JsonProperty
  private int display;

  public Notify(int notifyType, long notifyTime, String content,Notify.NodifyData data, int display){
    this.notifyType=notifyType;
    this.notifyTime=notifyTime;
    this.content=content;
    this.data=data;
    this.display=display;
  }

  public enum Display {
    NO,
    YES
  }

  public enum NotifyType {
    GROUP(0),
    DIRECTORY(1),
    TASK(2),
    VOTE(3),
    CONVERSATION(4);

    int code;

    NotifyType(int code){
      this.code=code;
    }

    public int getCode() {
      return code;
    }
  }

  public static class NodifyData {
  }

  public NodifyData getData() {
    return data;
  }

  public void setData(NodifyData data) {
    this.data = data;
  }

  public int getNotifyType() {
    return notifyType;
  }

  public long getNotifyTime() {
    return notifyTime;
  }

  public String getContent() {
    return content;
  }

  public int getDisplay() {
    return display;
  }
}
