package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SendMessageResponseV2 {

    @JsonProperty
    private boolean needsSync;

    @JsonProperty
    private Long sequenceId;

    @JsonProperty
    private long systemShowTimestamp;

    @JsonProperty
    private Long notifySequenceId;

    public void setUnavailableUsers(Object unavailableUsers) {
        this.unavailableUsers = unavailableUsers;
    }

    @JsonProperty
    private Object unavailableUsers;

    public SendMessageResponseV2(boolean needsSync, Long sequenceId, long systemShowTimestamp, Long notifySequenceId, List<ExceptionRecipient> missing, List<ExceptionRecipient> extra, List<ExceptionRecipient> stale) {
        this.needsSync = needsSync;
        this.sequenceId = sequenceId;
        this.systemShowTimestamp = systemShowTimestamp;
        this.notifySequenceId = notifySequenceId;
        this.missing = missing;
        this.extra = extra;
        this.stale = stale;
    }

    public void setMissing(List<ExceptionRecipient> missing) {
        this.missing = missing;
    }

    public void setExtra(List<ExceptionRecipient> extra) {
        this.extra = extra;
    }

    public void setStale(List<ExceptionRecipient> stale) {
        this.stale = stale;
    }

    @JsonProperty
    private List<ExceptionRecipient> missing;

    @JsonProperty
    private List<ExceptionRecipient> extra;

    @JsonProperty
    private List<ExceptionRecipient> stale;

    public static class ExceptionRecipient{
        @JsonProperty
        private String uid;

        @JsonProperty
        private String identityKey;

        @JsonProperty
        private int registrationId;

        public ExceptionRecipient(String uid, String identityKey, int registrationId) {
            this.uid = uid;
            this.identityKey = identityKey;
            this.registrationId = registrationId;
        }
    }

    public SendMessageResponseV2() {}

    public SendMessageResponseV2(boolean needsSync) {
        this.needsSync = needsSync;
    }

    public SendMessageResponseV2(boolean needsSync, Long sequenceId) {
        this.needsSync = needsSync;
        this.sequenceId = sequenceId;
    }

    public SendMessageResponseV2(boolean needsSync, Long sequenceId, long systemShowTimestamp) {
        this.needsSync = needsSync;
        this.sequenceId = sequenceId;
        this.systemShowTimestamp = systemShowTimestamp;
    }

    public SendMessageResponseV2(boolean needsSync, Long sequenceId, long systemShowTimestamp,long notifySequenceId) {
        this.needsSync = needsSync;
        this.sequenceId = sequenceId;
        this.systemShowTimestamp = systemShowTimestamp;
        this.notifySequenceId=notifySequenceId;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getSystemShowTimestamp() {
        return systemShowTimestamp;
    }

    public void setSystemShowTimestamp(long systemShowTimestamp) {
        this.systemShowTimestamp = systemShowTimestamp;
    }

}
