package org.whispersystems.textsecuregcm.storage;

public class Platform {
  public static final int MEMCACHE_VERION = 1;
  private String pid;
  private String owner;
  private String note;

  public Platform(){
  }

  public Platform(String pid, String owner, String note){
    this.pid=pid;
    this.owner=owner;
    this.note=note;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
