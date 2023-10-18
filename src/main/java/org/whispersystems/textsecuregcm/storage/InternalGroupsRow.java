package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalGroupsRow {

    @JsonProperty
    private String name;

    @JsonProperty
    private int id;

    @JsonProperty
    private String appid;

    @JsonProperty
    private String pid;

    public InternalGroupsRow() {
    }

    public InternalGroupsRow(String name) {
        this.name = name;
    }

    public InternalGroupsRow(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public InternalGroupsRow(int id, String name, String appid, String pid) {
        this.name = name;
        this.id = id;
        this.appid = appid;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
