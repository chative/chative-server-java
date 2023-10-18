/*
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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import org.whispersystems.textsecuregcm.util.LangMatcher;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account extends AccountExtend implements Cloneable{
//  private final Logger logger         = LoggerFactory.getLogger(Account.class);

  public static final int MEMCACHE_VERION = 5;

  public Device setMeetingVersion(int meetingVersion, Device authenticatedDevice, int accMaxMeetingVersion) {
    if (authenticatedDevice != null) {
      Device device = getDevice(authenticatedDevice.getId()).orElse(null);
      if (device == null) device = authenticatedDevice;
      device.setMeetingVersion(meetingVersion);
      for (Device d : devices) {
        if (d.getMeetingVersion() == null) { // 有一个没有设置版本号，就设置为1
          setPublicConfig(FieldName.MeetingVersion, 1);
          return device;
        }
        if (d.getMeetingVersion() < meetingVersion) {
          meetingVersion = d.getMeetingVersion();
        }
      }
      if (meetingVersion > accMaxMeetingVersion) {
        meetingVersion = accMaxMeetingVersion;
      }
      setPublicConfig(FieldName.MeetingVersion, meetingVersion);
      return device;
    }
    return null;
  }

  public int getRealMeetingVersion(){
    final Set<Device> devices1 = getDevices();
    if (devices1.isEmpty())return 1;
    if (devices1.size() == 1) {
      final Optional<Device> masterDevice = getMasterDevice();
      if (masterDevice.isPresent())
        return masterDevice.get().getMeetingVersion();
      return 1;
    }
    int minVersion = 255;
    for (Device device : devices1) {
      if (minVersion > device.getMeetingVersion())
        minVersion = device.getMeetingVersion();
    }
    return  minVersion;
  }
  public Device setMsgEncVersion(int msgEncVersion, Device authenticatedDevice){
    if (authenticatedDevice != null) {
      Device device = getDevice(authenticatedDevice.getId()).orElse(null);
      if (device == null) device = authenticatedDevice;
      device.setMsgEncVersion(msgEncVersion);
      for (Device d : devices) {
        if (d.getMsgEncVersion() == null) { // 有一个没有设置版本号，就设置为1
          setPublicConfig(FieldName.MsgEncVersion, 1);
          return device;
        }
        if (d.getMsgEncVersion() < msgEncVersion) {
          msgEncVersion = d.getMsgEncVersion();
        }
      }
      setPublicConfig(FieldName.MsgEncVersion, msgEncVersion);
      return device;
    }
    return null;
  }

  public enum GENDER {
    NONE,
    MALE,
    FEMALE,
    OTHER
  }

  public enum MsgHandleType {
    NORMAL,
    ONLY_SEND,
    ONLY_RECEIVE,
    NOTHING
  }

  public enum ExtType {
    DEFAULT(2),
    B(1),
    P(2),
    BOT(3);
    long id;
    ExtType(long id){
      this.id=id;
    }

    public static ExtType fromName(String name) {
      for(ExtType extType: ExtType.values()){
        if(extType.name().equals(name.toUpperCase())){
          return extType;
        }
      }
      return DEFAULT;
    }
    public long getId() {
      return id;
    }
  }

  @JsonProperty
  private String number;

  public Long getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(Long joinedAt) {
    this.joinedAt = joinedAt;
  }

  @JsonProperty
  private Long joinedAt;

  public void setDevices(Set<Device> devices) {
    this.devices = devices;
  }

  @JsonProperty
  private Set<Device> devices = new HashSet<>();

  @JsonProperty
  private String identityKey;

  @JsonProperty
  private String name;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private String avatarDigest;

  @JsonProperty
  private String pin;

  @JsonIgnore
  private Device authenticatedDevice;

  @JsonProperty
  private String plainName;

  @JsonProperty
  private String pushType;

  @JsonProperty
  private String pushToken;

  @JsonProperty
  private int vcode;

  @JsonProperty
  private boolean registered; // logout时设置为false

  @JsonProperty
  private int invitationPerDay;

  @JsonProperty
  private boolean disabled;



  @JsonProperty
  private boolean deleted;


  //@JsonProperty
  //private String email;



  //@JsonProperty
  //private String phone;

  @JsonProperty
  private String oktaId;

  @JsonProperty
  private String timeZone;

  @JsonProperty
  private String avatar2;

  @JsonProperty
  private String avatar2Key;

  @JsonProperty
  private String signature;

  @JsonProperty
  private String department;

  @JsonProperty
  private String superior;

  @JsonProperty
  private Integer gender;

  @JsonProperty
  private String address;

  @JsonProperty
  private Integer flag;

  @JsonProperty
  private String pid;

  @JsonProperty
  private String puid;

  @JsonProperty
  private Integer accountMsgHandleType;

  @JsonProperty
  private String oktaOrg;

//  @JsonProperty
//  private String invitationCode;

  @JsonProperty
  private boolean inactive;

  @JsonProperty
  private long extId;

  @JsonProperty
  private boolean supportTransfer;

  public boolean isSupportTransfer() {
    return supportTransfer;
  }

  public void setSupportTransfer(boolean supportTransfer) {
    this.supportTransfer = supportTransfer;
  }

  public Account() {}

  @VisibleForTesting
  public Account(String number, Set<Device> devices) {
    this.number  = number;
    this.devices = devices;
  }

  public Optional<Device> getAuthenticatedDevice() {
    return Optional.ofNullable(authenticatedDevice);
  }

  public void setAuthenticatedDevice(Device device) {
    this.authenticatedDevice = device;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getNumber() {
    return number;
  }

  public void addDevice(Device device) {
    this.devices.remove(device);
    this.devices.add(device);
  }

  public void removeDevice(long deviceId) {
    this.devices.remove(new Device(deviceId, null, null, null, null, null, null, null, false, 0, null, 0, 0, false, false, "NA"));
  }

  public Set<Device> getDevices() {
    if (devices == null) {
      devices = new HashSet<>();
    }
    return devices;
  }

  public Optional<Device> getMasterDevice() {
    return getDevice(Device.MASTER_ID);
  }

  public Optional<Device> getDevice(long deviceId) {
    if (devices == null || devices.isEmpty()) {
      return Optional.empty();
    }
    for (Device device : devices) {
      if (device.getId() == deviceId) {
        return Optional.of(device);
      }
    }

    return Optional.empty();
  }

//  public boolean isVoiceSupported() {
//    for (Device device : devices) {
//      if (device.isActive() && device.isVoiceSupported()) {
//        return true;
//      }
//    }
//
//    return false;
//  }
//
//  public boolean isVideoSupported() {
//    for (Device device : devices) {
//      if (device.isActive() && device.isVideoSupported()) {
//        return true;
//      }
//    }
//
//    return false;
//  }
//
//  public boolean isActive() {
//    return
//        getMasterDevice().isPresent() &&
//        getMasterDevice().get().isActive() ;
//  }
//
//  public long getNextDeviceId() {
//    long highestDevice = Device.MASTER_ID;
//
//    for (Device device : devices) {
//      if (!device.isActive()) {
//        return device.getId();
//      } else if (device.getId() > highestDevice) {
//        highestDevice = device.getId();
//      }
//    }
//
//    return highestDevice + 1;
//  }
//
//  public int getActiveDeviceCount() {
//    int count = 0;
//
//    for (Device device : devices) {
//      if (device.isActive()) count++;
//    }
//
//    return count;
//  }

  public boolean isRateLimited() {
    return true;
  }

  public Optional<String> getRelay() {
    return Optional.empty();
  }

  public void setIdentityKey(String identityKey) {
    this.identityKey = identityKey;
  }

  public String getIdentityKey() {
    return identityKey;
  }

  public long getLastSeen() {
    long lastSeen = 0;

    for (Device device : devices) {
      if (device.getLastSeen() > lastSeen) {
        lastSeen = device.getLastSeen();
      }
    }

    return lastSeen;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getAvatarDigest() {
    return avatarDigest;
  }

  public void setAvatarDigest(String avatarDigest) {
    this.avatarDigest = avatarDigest;
  }

  public Optional<String> getPin() {
    return Optional.ofNullable(pin);
  }

  public void setPin(String pin) {
    this.pin = pin;
  }

  public String getPlainName() {
    if (plainName == null || plainName.equals("")) {
      System.out.println(      "getPlainName failed,number:"+getNumber()+"getPlainName empty");
      return " "; // 临时调试
    }
    return plainName;
  }

  public void setPlainName(String plainName) {
    this.plainName = plainName;
    this.setPublicName(plainName);
  }
  //todo 前端支持设置publicName时，需完善成仅注册场景调用该方法
  public void setPublicName(String plainName){
    if(plainName!=null) {
      String publicName=getPublicName(plainName);
      this.setPublicConfig(FieldName.PUBLIC_NAME, publicName);
    }else{
      this.setPublicConfig(FieldName.PUBLIC_NAME, number);
    }
  }

  public String getPublicName(String plainName){
    if(!isInternalAccount()){
      return plainName;
    }
    String patternStr="([^ |\\(|\\.|（|-|_|-]+)";
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(plainName);
    if(matcher.find()) {
      return matcher.group(1);
    }
    return number;
  }
  public String getPushType() {
    return pushType;
  }

  public void setPushType(String pushType) {
    this.pushType = pushType;
  }

  public String getPushToken() {
    return pushToken;
  }

  public void setPushToken(String pushToken) {
    this.pushToken = pushToken;
  }

  public int getVcode() {
    return vcode;
  }

  public void setVcode(int vcode) {
    this.vcode = vcode;
  }

  public boolean isRegistered() {
    return registered;
  }

  public void setRegistered(boolean registered) {
    this.registered = registered;
  }

  public int getInvitationPerDay() {
    return invitationPerDay;
  }

  public void setInvitationPerDay(int invitationPerDay) {
    this.invitationPerDay = invitationPerDay;
  }

  public boolean isinValid() {
    return disabled || inactive || deleted;
  }

  public boolean isDisabled(){
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }


  public String getOktaId() {
    return oktaId;
  }

  public void setOktaId(String oktaId) {
    this.oktaId = oktaId;
  }

  public int renew() {
    int vcode = 100000 + (new SecureRandom()).nextInt(999999 - 100000);
    setVcode(vcode);
    setRegistered(false);
    return vcode;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getAvatar2() {
    return avatar2;
  }

  public void setAvatar2(String avatar2) {
    this.avatar2 = avatar2;
  }

  public String getAvatar2Key() {
    return avatar2Key;
  }

  public void setAvatar2Key(String avatar2Key) {
    this.avatar2Key = avatar2Key;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getSuperior() {
    return superior;
  }

  public void setSuperior(String superior) {
    this.superior = superior;
  }

  public Integer getGender() {
    return gender;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getPuid() {
    return puid;
  }

  public void setPuid(String puid) {
    this.puid = puid;
  }

  public Integer getAccountMsgHandleType() {
    return accountMsgHandleType;
  }

  public void setAccountMsgHandleType(Integer accountMsgHandleType) {
    this.accountMsgHandleType = accountMsgHandleType;
  }

  public String getOktaOrg() {
    return oktaOrg;
  }

  public void setOktaOrg(String oktaOrg) {
    this.oktaOrg = oktaOrg;
  }

//  public String getInvitationCode() {
//    return invitationCode;
//  }
//
//  public void setInvitationCode(String invitationCode) {
//    this.invitationCode = invitationCode;
//  }

  public boolean isInactive() {
    return inactive;
  }

  public void setInactive(boolean inactive) {
    this.inactive = inactive;
  }

  public long getExtId() {
    return ExtType.DEFAULT.getId();
    //return extId;
  }

  public void setExtId(long extId) {
    this.extId = ExtType.DEFAULT.getId();
  }

  public int getMainDeviceRegistrationId() {
    for (Device device : getDevices()) {
      if(device.getId() == Device.MASTER_ID) return device.getRegistrationId();
    }
    return 0;
  }

  @Override
  public boolean equals(Object object){
    if(!(object instanceof Account)){
      return false;
    }
    if(object==this){
      return true;
    }
    return this.number.equals(((Account)object).getNumber());
  }
  @Override
  public int hashCode(){
    return this.number.hashCode();
  }

  @Override
  public Object clone()  {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      return null;
    }
  }

  public boolean isInternalAccount() {
    return !StringUtil.isEmpty(oktaId);
  }

  public Account(String number, boolean deleted) {
    this.number = number;
    this.deleted = deleted;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String formatJoinedDate(String lang) {
    if (joinedAt == null)return "";
    final String target = LangMatcher.Parse(lang);
    if (target.equals("en")) {
     return new SimpleDateFormat("MMM yyyy", Locale.ENGLISH).
              format(new Date(joinedAt));
    } else {
      return new SimpleDateFormat("yyyy.MM", Locale.ENGLISH).
              format(new Date(joinedAt));
    }
  }
}
