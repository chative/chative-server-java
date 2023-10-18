package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountExtend;
import org.whispersystems.textsecuregcm.util.ParameterValidator;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.ws.rs.core.Response;

public class Profile2 extends AccountExtend {

  @JsonProperty
  private String name;

  @JsonProperty
  private String signature;

  @JsonProperty
  private String timeZone;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private Integer gender;

  @JsonProperty
  private String address;

  @JsonProperty
  private Integer supportTransfer;

  public Boolean getSupportTransfer() {
    return supportTransfer != null ? supportTransfer != 0 : null;
  }


  public Integer getMeetingVersion() {
    return meetingVersion;
  }

  public void setMeetingVersion(Integer meetingVersion) {
    this.meetingVersion = meetingVersion;
  }

  @JsonProperty
  private Integer meetingVersion;


  @JsonProperty
  private Integer msgEncVersion;

  public Profile2() {
  }

  public Profile2(String name, String signature, String timeZone, String avatar, Integer gender, String address) {
    this.name = name;
    this.signature = signature;
    this.timeZone = timeZone;
    this.avatar = avatar;
    this.gender = gender;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public String getSignature() {
    return signature;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public String getAvatar() {
    return avatar;
  }

  public Integer getGender() {
    return gender;
  }

  public String getAddress() {
    return address;
  }


  public Integer getMsgEncVersion() {
    return msgEncVersion;
  }

  public void setMsgEncVersion(Integer msgEncVersion) {
    this.msgEncVersion = msgEncVersion;
  }

  public void validate(Account account,boolean isControlTeam,Logger logger) {
    // validate
    if (null != name) {
      if (!ParameterValidator.validateName(name)) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid name: " + name, logger);
      }
      name = ParameterValidator.cutdown(name, 60);
    }
    if (null != signature && !ParameterValidator.validateSignature(signature)) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid signature: " + signature, logger);
    if (null != timeZone && !ParameterValidator.validateTimeZone(timeZone)) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid time zone: " + timeZone, logger);
    if (null != avatar && !ParameterValidator.validateAvatar(avatar)) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid avatar: " + avatar, logger);
    if (null != gender && !ParameterValidator.validateGender(gender)) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid gender: " + gender, logger);
    if (null != address && !ParameterValidator.validateAddress(address)) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid address: " + address, logger);
    if (null != getPrivateConfigs() &&!ParameterValidator.validatePrivateConfigs(getPrivateConfigs())) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid privateConfigs: " +getPrivateConfigs() , logger);
    if (null != getProtectedConfigs() &&!ParameterValidator.validateProtectedConfigs(getProtectedConfigs())) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid protectedConfigs: " +getProtectedConfigs() , logger);
    if (null != getPublicConfigs() &&!ParameterValidator.validatePublicConfigs(getPublicConfigs())) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid publicConfigs: " +getPublicConfigs() , logger);
//    if (null!=getGlobalNotification()&& !ParameterValidator.validateGlobalNotification(getGlobalNotification())) BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid globalNotification: " +getGlobalNotification() , logger);


  }
}
