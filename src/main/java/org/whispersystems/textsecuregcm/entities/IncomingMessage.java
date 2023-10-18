/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.List;

public class IncomingMessage implements IncomingMessageBase{

  @JsonProperty
  private int    type;

  @JsonProperty
  private String destination;

  @JsonProperty
  private long   destinationDeviceId = 1;

  @JsonProperty
  private int destinationRegistrationId;

  @JsonProperty
  private String body; // Legacy

  @JsonProperty
  private String content;

  @JsonProperty
  private String relay;

  @JsonProperty
  private long   timestamp; // deprecated

  @JsonProperty
  private Notification notification;
  @JsonProperty

  private boolean readReceipt;

  @JsonProperty
  private Long sequenceId;

  @JsonProperty
  private long systemShowTimestamp;

  @JsonProperty
  private Long notifySequenceId;

  @JsonProperty
  private int msgType;

  @JsonProperty
  private Conversation conversation;

  @JsonProperty
  private List<ReadPosition> readPositions;

  @JsonProperty
  private RealSource realSource;
  @JsonProperty
  private int detailMessageType;

  public String getDestination() {
    return destination;
  }

  public String getBody() {
    return body;
  }

  public int getType() {
    return type;
  }

  public String getRelay() {
    return relay;
  }

  public long getDestinationDeviceId() {
    return destinationDeviceId;
  }

  public int getDestinationRegistrationId() {
    return destinationRegistrationId;
  }

  public String getContent() {
    return content;
  }

  public Notification getNotification() {
    return notification;
  }

  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  public boolean isReadReceipt() {
    return readReceipt;
  }

  public void setReadReceipt(boolean readReceipt) {
    this.readReceipt = readReceipt;
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

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getNotifySequenceId() {
    return notifySequenceId;
  }

  public void setNotifySequenceId(Long notifySequenceId) {
    this.notifySequenceId = notifySequenceId;
  }

  public int getMsgType() {
    return msgType;
  }

  public void setMsgType(int msgType) {
    this.msgType = msgType;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public void setConversation(Conversation conversation) {
    this.conversation = conversation;
  }

  public List<ReadPosition> getReadPositions() {
    return readPositions;
  }

  public void setReadPositions(List<ReadPosition> readPositions) {
    this.readPositions = readPositions;
  }

  public IncomingMessage.RealSource getRealSource() {
    return realSource;
  }

  public void setRealSource(IncomingMessage.RealSource realSource) {
    this.realSource = realSource;
  }

  public int getDetailMessageType() {
    return detailMessageType;
  }

  public void setDetailMessageType(int detailMessageType) {
    this.detailMessageType = detailMessageType;
  }

  public enum DetailMessageType{
    FORWARD(1),
    CONTACT(2),
    RECALL(3),
    TASK(4),
    VOTE(5),
    REACTION(6),
    CARD(7),
    TOOK_SCREENSHOT(8);

    private int code;
    DetailMessageType(int code){
      this.code=code;
    }

    public int getCode() {
      return code;
    }
  }

  public static class Conversation{
    private String number;
    private String gid;

    public Conversation(String number, String gid) {
      this.number = number;
      this.gid = gid;
    }

    public Conversation() {
    }

    public enum Type{
      UNKNOWN,
      PRIVATE,
      GROUP;
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
    public Type getType(){
      if(!StringUtil.isEmpty(number)){
        return Type.PRIVATE;
      }
      if(!StringUtil.isEmpty(gid)){
        return Type.GROUP;
      }
      return Type.UNKNOWN;
    }

    public String getId(){
      if(getType()==Type.PRIVATE){
        return number;
      }
      if(getType()==Type.GROUP){
        return gid;
      }
      return null;
    }
  }

  public static class ReadPosition{
    private String groupId;
    private long maxServerTime;
    private long maxNotifySequenceId;
    private long readAt;

    public String getGroupId() {
      return groupId;
    }

    public void setGroupId(String groupId) {
      this.groupId = groupId;
    }

    public long getMaxServerTime() {
      return maxServerTime;
    }

    public void setMaxServerTime(long maxServerTime) {
      this.maxServerTime = maxServerTime;
    }

    public long getMaxNotifySequenceId() {
      return maxNotifySequenceId;
    }

    public void setMaxNotifySequenceId(long maxNotifySequenceId) {
      this.maxNotifySequenceId = maxNotifySequenceId;
    }

    public long getReadAt() {
      return readAt;
    }

    public void setReadAt(long readAt) {
      this.readAt = readAt;
    }
  }

  public static class RealSource{
    private String source;
    private Long sourceDevice;
    private Long timestamp;
    private long serverTimestamp;
    private long sequenceId;
    private long notifySequenceId;

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public Long getSourceDevice() {
      return sourceDevice;
    }

    public void setSourceDevice(Long sourceDevice) {
      this.sourceDevice = sourceDevice;
    }

    public Long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
    }

    public long getServerTimestamp() {
      return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
      this.serverTimestamp = serverTimestamp;
    }

    public long getSequenceId() {
      return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
      this.sequenceId = sequenceId;
    }

    public long getNotifySequenceId() {
      return notifySequenceId;
    }

    public void setNotifySequenceId(long notifySequenceId) {
      this.notifySequenceId = notifySequenceId;
    }
  }
}
