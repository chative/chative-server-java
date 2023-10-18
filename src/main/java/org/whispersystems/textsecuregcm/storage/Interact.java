package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Interact {
    public static final int MEMCACHE_VERION = 1;
    @JsonProperty
    private long id;
    @JsonProperty
    private String number;
    @JsonProperty
    private String source;
    @JsonProperty
    private long lastUpdateTime;
    @JsonProperty
    private Integer type;
    @JsonProperty
    private String comment;


    public Interact() {
    }

    public Interact(long id, String number, String source, long lastUpdateTime, Integer type, String comment) {
        this.id=id;
        this.number=number;
        this.source=source;
        this.lastUpdateTime=lastUpdateTime;
        this.type=type;
        this.comment=comment;
    }
    public Interact(String number, String source, long lastUpdateTime, Integer type, String comment) {
        this.number=number;
        this.source=source;
        this.lastUpdateTime=lastUpdateTime;
        this.type=type;
        this.comment=comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
