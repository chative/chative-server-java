package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetConversationMsgResponse {
    @JsonProperty
    List<String> conversationMsgInfos;
    @JsonProperty
    boolean hasMore;


    public GetConversationMsgResponse(List<String> conversationMsgInfos,boolean hasMore){
       this.conversationMsgInfos=conversationMsgInfos;
       this.hasMore=hasMore;
    }

    public List<String> getConversationMsgInfos() {
        return conversationMsgInfos;
    }

    public void setConversationMsgInfos(List<String> conversationMsgInfos) {
        this.conversationMsgInfos = conversationMsgInfos;
    }
}
