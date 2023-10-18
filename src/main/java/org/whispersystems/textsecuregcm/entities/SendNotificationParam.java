package org.whispersystems.textsecuregcm.entities;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.util.Base64;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SendNotificationParam {
    public String getUid() {
        return uid;
    }

    public long getDid() {
        return did;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isNewOnly() {
        return newOnly;
    }

    public boolean isSilent() {
        return silent;
    }

    public String getMsgEnvelope() {
        return msgEnvelope;
    }

    public Notification getNotification() {
        return notification;
    }

    public IncomingMessage.Conversation getConversation() {
        return conversation;
    }


    // Account account;
    // Device device;

    @JsonProperty
    String uid;

    @JsonProperty
    long did;

    @JsonProperty
    int depth;

    @JsonProperty
    boolean newOnly;

    @JsonProperty
    boolean silent;

    @JsonProperty
    String msgEnvelope;

    //MessageProtos.Envelope outgoingMessage;

    @JsonProperty
    Notification notification;

    @JsonProperty
    IncomingMessage.Conversation conversation;

    public SendNotificationParam() {
    }

    public SendNotificationParam(String uid, long did, int depth, boolean newOnly, boolean silent,
                                 MessageProtos.Envelope outgoingMessage, Notification notification,
                                 IncomingMessage.Conversation conversation) {
        this.uid = uid;
        this.did = did;
        this.depth = depth;
        this.newOnly = newOnly;
        this.silent = silent;
        //this.msgEnvelope = msgEnvelope;
        msgEnvelope = Base64.getEncoder().encodeToString(outgoingMessage.toByteArray());
        //this.outgoingMessage = outgoingMessage;
        this.notification = notification;
        this.conversation = conversation;
    }

    public MessageProtos.Envelope getOutgoingMessage() throws InvalidProtocolBufferException {
        return MessageProtos.Envelope.parseFrom(Base64.getDecoder().decode(msgEnvelope));
    }

    public String toJson() throws JsonProcessingException {
        return  SystemMapper.getMapper().writeValueAsString(this);
    }

    static public SendNotificationParam fromJson(String json) throws JsonProcessingException {
        return SystemMapper.getMapper().readValue(json, SendNotificationParam.class);
    }
}
