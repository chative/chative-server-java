package org.whispersystems.textsecuregcm.storage;

public class PlatformApp {
  private String pid;
  private String appid;
  private String note;

  public PlatformApp(){
  }

  public PlatformApp(String pid, String appid, String note){
    this.pid=pid;
    this.appid=appid;
    this.note=note;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
