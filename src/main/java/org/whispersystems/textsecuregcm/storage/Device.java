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
package org.whispersystems.textsecuregcm.storage;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.auth.AuthenticationCredentials;
import org.whispersystems.textsecuregcm.entities.SignedPreKey;
import org.whispersystems.textsecuregcm.util.Util;

import java.util.concurrent.TimeUnit;

public class Device {

  public static final long MASTER_ID = 1;

  @JsonProperty
  private long    id;

  @JsonProperty
  private String  name;

  @JsonProperty
  private String  authToken;

  @JsonProperty
  private String  salt;

  @JsonProperty
  private String  signalingKey;

  @JsonProperty
  private String  gcmId;

  @JsonProperty
  private AndroidNotify androidNotify;

  @JsonProperty
  private String  apnId;

  @JsonProperty
  private String  voipApnId;

  @JsonProperty
  private long pushTimestamp;

  @JsonProperty
  private boolean fetchesMessages;

  @JsonProperty
  private int registrationId;

  @JsonProperty
  private SignedPreKey signedPreKey;

  @JsonProperty
  private long lastSeen;

  @JsonProperty
  private long created;

  @JsonProperty
  private boolean voice;

  @JsonProperty
  private boolean video;

  @JsonProperty
  private String userAgent;
  @JsonProperty
  private int receiveType;
  @JsonProperty
  private String receiveChannel;
  @JsonProperty
  private String appId;

  public Integer getMeetingVersion() {
    return meetingVersion == null || meetingVersion < 1 ? 1 : meetingVersion;
  }

  public void setMeetingVersion(Integer meetingVersion) {
    this.meetingVersion = meetingVersion == null || meetingVersion < 1 ? 1 : meetingVersion;
  }

  @JsonProperty
  private Integer meetingVersion = 1;

  @JsonProperty
  private Integer msgEncVersion = 1;

  public Device() {}

  public Device(long id, String name, String authToken, String salt,
                String signalingKey, String gcmId, String apnId,
                String voipApnId, boolean fetchesMessages,
                int registrationId, SignedPreKey signedPreKey,
                long lastSeen, long created, boolean voice, boolean video,
                String userAgent)
  {
    this.id              = id;
    this.name            = name;
    this.authToken       = authToken;
    this.salt            = salt;
    this.signalingKey    = signalingKey;
    this.gcmId           = gcmId;
    this.apnId           = apnId;
    this.voipApnId       = voipApnId;
    this.fetchesMessages = fetchesMessages;
    this.registrationId  = registrationId;
    this.signedPreKey    = signedPreKey;
    this.lastSeen        = lastSeen;
    this.created         = created;
    this.voice           = voice;
    this.video           = video;
    this.userAgent       = userAgent;
  }

  public String getApnId() {
    return apnId;
  }

  public boolean shouldPushNotification(){
    return getApnId() != null || getAndroidNotify() != null;
  }

  public void setApnId(String apnId) {
    this.apnId = apnId;

    if (apnId != null) {
      this.pushTimestamp = System.currentTimeMillis();
    }
  }

  public String getVoipApnId() {
    return voipApnId;
  }

  public void setVoipApnId(String voipApnId) {
    this.voipApnId = voipApnId;
  }

  public void setLastSeen(long lastSeen) {
    this.lastSeen = lastSeen;
  }

  public long getLastSeen() {
//    return System.currentTimeMillis();
    return lastSeen;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public long getCreated() {
    return this.created;
  }

  public String getGcmId() {
    return gcmId;
  }

  public void setGcmId(String gcmId) {
    this.gcmId = gcmId;

    if (gcmId != null) {
      this.pushTimestamp = System.currentTimeMillis();
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isVoiceSupported() {
    return voice;
  }

  public void setVoiceSupported(boolean voice) {
    this.voice = voice;
  }

  public boolean isVideoSupported() {
    return video;
  }

  public void setVideoSupported(boolean video) {
    this.video = video;
  }

  public void setAuthenticationCredentials(AuthenticationCredentials credentials) {
    this.authToken = credentials.getHashedAuthenticationToken();
    this.salt      = credentials.getSalt();
  }

  public AuthenticationCredentials getAuthenticationCredentials() {
    return new AuthenticationCredentials(authToken, salt);
  }

  public String getSignalingKey() {
    return signalingKey;
  }

  public void setSignalingKey(String signalingKey) {
    this.signalingKey = signalingKey;
  }

//  public boolean isActive() {
//    boolean hasChannel = fetchesMessages || !Util.isEmpty(getApnId()) || !Util.isEmpty(getGcmId());
//
//    return (id == MASTER_ID && hasChannel && signedPreKey != null) ||
//           (id != MASTER_ID && hasChannel && signedPreKey != null );
//  }

  public boolean getFetchesMessages() {
    return fetchesMessages;
  }

  public void setFetchesMessages(boolean fetchesMessages) {
    this.fetchesMessages = fetchesMessages;
  }

  public boolean isMaster() {
    return getId() == MASTER_ID;
  }

  public int getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(int registrationId) {
    this.registrationId = registrationId;
  }

  public SignedPreKey getSignedPreKey() {
    return signedPreKey;
  }

  public void setSignedPreKey(SignedPreKey signedPreKey) {
    this.signedPreKey = signedPreKey;
  }

  public long getPushTimestamp() {
    return pushTimestamp;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public int getReceiveType() {
    return receiveType;
  }

  public void setReceiveType(int receiveType) {
    this.receiveType = receiveType;
  }

  public String getReceiveChannel() {
    return receiveChannel;
  }

  public void setReceiveChannel(String receiveChannel) {
    this.receiveChannel = receiveChannel;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || !(other instanceof Device)) return false;

    Device that = (Device)other;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return (int)this.id;
  }

  public boolean isSameOS(String ua){
    if (userAgent == null){
      return false;
    }
    if (ua.contains("iPhone")){
      return userAgent.contains("iPhone");
    } else if ( ua.contains("Android")){
      return userAgent.contains("Android");
    }
    return false;
  }

  public AndroidNotify getAndroidNotify() {
    return androidNotify;
  }

  public void setAndroidNotify(AndroidNotify androidNotify) {
    this.androidNotify = androidNotify;
  }


  public Integer getMsgEncVersion() {
    return msgEncVersion == null || msgEncVersion < 1 ? 1 : msgEncVersion;
  }

  public void setMsgEncVersion(Integer msgEncVersion) {
    this.msgEncVersion = msgEncVersion == null || msgEncVersion < 1 ? 1 : msgEncVersion;
  }

  static public class AndroidNotify{
    public String getTpnID() {
      return tpnID;
    }

    public void setTpnID(String tpnID) {
      this.tpnID = tpnID;
    }

    @JsonProperty
    private String  tpnID;
    @JsonProperty
    private String  fcmID;

    public AndroidNotify(String tpnID, String fcmID) {
      this.tpnID = tpnID;
      this.fcmID = fcmID;
    }

    public AndroidNotify() { }
  }
}
