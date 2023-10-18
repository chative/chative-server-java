package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Team {

    public static final int MEMCACHE_VERION = 1;

    @JsonProperty
    private String name;

    @JsonProperty
    private int id;

    @JsonProperty
    private int parentId;

    @JsonProperty
    private Boolean status;

    @JsonProperty
    private int orderNum;

    @JsonProperty
    private String ancestors;

    @JsonProperty
    private long createTime;

    @JsonProperty
    private String appid;

    @JsonProperty
    private String pid;

    @JsonProperty
    private String remark;

    public Team(){}

    public Team(String name) {
        this.name = name;
    }

    public Team(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Team(int id, String name, String appid, String pid){
        this.id = id;
        this.name = name;
        this.appid = appid;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
