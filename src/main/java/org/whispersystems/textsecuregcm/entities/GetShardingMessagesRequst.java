package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.PushConversationsTable;

import javax.validation.constraints.NotNull;
import java.util.List;

public class GetShardingMessagesRequst {
    @JsonProperty
    private String number;
    @JsonProperty
    private String gid;
    @JsonProperty
    private List<Long> sequenceIds;
    @JsonProperty
    private Long minSequenceId;
    @JsonProperty
    private Long maxSequenceId;

    public enum DESTINATION_TYPE {
        PRIVATE,
        GROUP;
        private static DESTINATION_TYPE[] allValues = values();

        public static DESTINATION_TYPE fromOrdinal(int n) {
            switch (n) {
                case 0: return PRIVATE;
                case 1: return GROUP;
                default: return null;
            }
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public List<Long> getSequenceIds() {
        return sequenceIds;
    }

    public void setSequenceIds(List<Long> sequenceIds) {
        this.sequenceIds = sequenceIds;
    }

    public Long getMinSequenceId() {
        return minSequenceId;
    }

    public void setMinSequenceId(Long minSequenceId) {
        this.minSequenceId = minSequenceId;
    }

    public Long getMaxSequenceId() {
        return maxSequenceId;
    }

    public void setMaxSequenceId(Long maxSequenceId) {
        this.maxSequenceId = maxSequenceId;
    }
}
