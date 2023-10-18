package org.whispersystems.textsecuregcm.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;


public class ApnMessage {

  // public static final String APN_PAYLOAD    = "{\"aps\":{\"sound\":\"default\",\"alert\":{\"loc-key\":\"APN_Message\"},\"category\":\"reply\",\"Content-available\":\"1\"}}";
  public static final String APN_PAYLOAD    = "{\"aps\":{\"sound\":\"default\",\"badge\":1,\"alert\":{\"loc-key\":\"APN_Message\"},\"Content-available\":\"1\"}}";
  public static final long   MAX_EXPIRATION = Integer.MAX_VALUE * 1000L;

  private final String apnId;
  private final String number;
  private final long   deviceId;
  private final boolean isVoip ;
  private final String collapseId;

  public void setBackground(boolean background) {
    this.background = background;
  }

  public boolean isBackground() {
    return background;
  }

  private boolean background = false;

  @JsonProperty
  @NotEmpty
  private String message;

  @JsonProperty
  private long expirationTime;

  public ApnMessage(String apnId, String number, long deviceId, boolean isVoip) {
    this.apnId          = apnId;
    this.number         = number;
    this.deviceId       = deviceId;
    this.isVoip         = isVoip;
    this.message        = "";
    this.collapseId=null;
  }

  public ApnMessage(String apnId, String number, long deviceId, String message, boolean voip) {
    this.apnId          = apnId;
    this.number         = number;
    this.deviceId       = deviceId;
    this.message        = message;
    this.isVoip         = voip;
    this.collapseId=null;
  }

  public ApnMessage(String apnId, String number, long deviceId, String message, boolean voip,String collapseId) {
    this.apnId          = apnId;
    this.number         = number;
    this.deviceId       = deviceId;
    this.message        = message;
    this.isVoip         = voip;
    this.collapseId=collapseId;
  }

  public boolean isVoip() {
    return isVoip;
  }

  public String getApnId() {
    return apnId;
  }

  public String getMessage() {
    if (message.length() < 5) {
      return APN_PAYLOAD;
    }
    else {
      return message;
    }
  }

  public long getExpirationTime() {
    return MAX_EXPIRATION;
  }

  public String getNumber() {
    return number;
  }

  public long getDeviceId() {
    return deviceId;
  }

  public String getCollapseId() {
    return collapseId;
  }
}
