package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetShardingMessagesResponse {
    @JsonProperty
    List<String> messages;
    @JsonProperty
    long surplus;

    public GetShardingMessagesResponse( List<String> messages,long surplus){
        this.messages=messages;
        this.surplus=surplus;
    }
    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public long getSurplus() {
        return surplus;
    }

    public void setSurplus(long surplus) {
        this.surplus = surplus;
    }
}
