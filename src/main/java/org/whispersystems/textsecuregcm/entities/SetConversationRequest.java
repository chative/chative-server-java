package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.configuration.ConversationConfiguration;
import org.whispersystems.textsecuregcm.storage.ConversationsTable;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.regex.Pattern;

public class SetConversationRequest {

    @JsonProperty
    String conversation;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonProperty
    String remark;

    @JsonProperty
    Integer muteStatus;

    @JsonProperty
    Integer blockStatus;

    @JsonProperty
    Integer confidentialMode;

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public Integer getMuteStatus() {
        return muteStatus;
    }

    public void setMuteStatus(Integer muteStatus) {
        this.muteStatus = muteStatus;
    }

    public Integer getConfidentialMode() {
        return confidentialMode;
    }


    public Integer getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(Integer blockStatus) {
        this.blockStatus = blockStatus;
    }


    public boolean isValid(final ConversationConfiguration conversationConfiguration) {
        if (StringUtil.isEmpty(getConversation())) return false;
        if (getBlockStatus() == null && getMuteStatus() == null && getRemark() == null && getConfidentialMode() == null)
            return false;
        if (getBlockStatus() != null && ConversationsTable.STATUS.fromCode(getBlockStatus()) == null) return false;
        if (getMuteStatus() != null && ConversationsTable.STATUS.fromCode(getMuteStatus()) == null) return false;

        return true;
        //return getBlockStatus() == null || (conversationConfiguration != null &&
        //        !StringUtil.isEmpty(conversationConfiguration.getBlockRegex()) &&
        //        Pattern.matches(conversationConfiguration.getBlockRegex(), getConversation()));
    }
}
