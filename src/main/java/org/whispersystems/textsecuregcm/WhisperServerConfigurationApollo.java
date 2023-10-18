package org.whispersystems.textsecuregcm;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.Account;

import java.util.HashMap;
import java.util.Map;

public class WhisperServerConfigurationApollo{
  private final Logger logger = LoggerFactory.getLogger(WhisperServerConfigurationApollo.class);
  private static String EMAIL_DOMAIN_KEY="emailDomain";
  private static String EXT_CONFIG_KEY="extConfig";
  private static String VERIFICATION_CODE_FAILURE = "verificationCodeFailure";
  private static String VERIFICATION_CODE_EXPIRED = "verificationCodeExpired";
  private static String VERIFICATION_CODE_SUBJECT = "verificationCodeSubject";
  private static String VERIFICATION_CODE_TEMPLATE = "verificationCodeTemplate";
  private static String VERIFICATION_CODE_SEND_INTERVAL = "verificationCodeSendInterval";
  private static String CHATIVE_INVITATION_PER_DAY = "chativeInvitationPerDay";

  private static String ACC_MAX_MSG_ENC_VERSION = "accMaxMsgEncVersion";
  private static String V3_MESSAGE_SUPPORT = "v3MessageSupport";

  private JsonArray emailDomains;
  private JsonObject extConfig;
  private long verificationCodeFailureCount;
  private String verificationCodeExpired;
  private String verificationCodeSubject;
  private String verificationCodeTemplate;
  private long verificationCodeSendInterval;
  private String chativeInvitationPerDay;

  public Integer getMaxMsgEncVersion() {
    return maxMsgEncVersion;
  }

  private Integer maxMsgEncVersion = 255;
  private Map<String, Boolean> v3MessageSupport;

  WhisperServerConfigurationApollo(){
    Config config = ConfigService.getAppConfig();
    String emailDomainStr = config.getProperty(EMAIL_DOMAIN_KEY, "");
    emailDomains = new Gson().fromJson(emailDomainStr, JsonArray.class);
    String extConfigStr = config.getProperty(EXT_CONFIG_KEY, "");
    extConfig = new Gson().fromJson(extConfigStr, JsonObject.class);
    verificationCodeFailureCount = Long.parseLong(config.getProperty(VERIFICATION_CODE_FAILURE, "5"));
    verificationCodeExpired = config.getProperty(VERIFICATION_CODE_EXPIRED, "3600");
    verificationCodeSubject = config.getProperty(VERIFICATION_CODE_SUBJECT, "[Chative] Verification Code");
    verificationCodeTemplate = config.getProperty(VERIFICATION_CODE_TEMPLATE, "<div dir=\"ltr\"><span id=\"gmail-docs-internal-guid-a4077f-7fff-cb83-f4ba-cefc10b092f3\"><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><span style=\"background-color:transparent;color:rgb(0,0,0);font-family:Arial;font-size:11pt;white-space:pre-wrap\">Dear user,</span><br><br/></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><span style=\"font-size:11pt;font-family:Arial;color:rgb(0,0,0);background-color:transparent;font-weight:400;font-style:normal;font-variant:normal;text-decoration:none;vertical-align:baseline;white-space:pre-wrap\">Your verification code is:</span></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><b style=\"font-weight:normal\"><br></b></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><span style=\"font-size:11pt;font-family:Arial;color:rgb(0,0,0);background-color:transparent;font-weight:400;font-style:normal;font-variant:normal;text-decoration:none;vertical-align:baseline;white-space:pre-wrap\"><h3>#verificationCode#</h3></span></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><b style=\"font-weight:normal\"><br></b></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><span style=\"font-size:11pt;font-family:Arial;color:rgb(0,0,0);background-color:transparent;font-weight:400;font-style:normal;font-variant:normal;text-decoration:none;vertical-align:baseline;white-space:pre-wrap\">The verification code expires in 60 minutes. Please do not share it with anyone.</span></p><p dir=\"ltr\" style=\"line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><b style=\"font-weight:normal\"><br></b></p></span></div>");
    verificationCodeSendInterval = Long.parseLong(config.getProperty(VERIFICATION_CODE_SEND_INTERVAL, "50"));
    chativeInvitationPerDay = config.getProperty(CHATIVE_INVITATION_PER_DAY, "10");
    maxMsgEncVersion = config.getIntProperty(ACC_MAX_MSG_ENC_VERSION, 255);
    v3MessageSupport = parseString2map(config.getProperty(V3_MESSAGE_SUPPORT, "[\"all\"]"));

    config.addChangeListener(new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        logger.info("Changes for namespace " + changeEvent.getNamespace());
        for (String key : changeEvent.changedKeys()) {
          ConfigChange change = changeEvent.getChange(key);
          logger.info(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
          if (change.getPropertyName().equals(EMAIL_DOMAIN_KEY)) {
            emailDomains = new Gson().fromJson(change.getNewValue(), JsonArray.class);
          }
          if(change.getPropertyName().equals(EXT_CONFIG_KEY)){
            extConfig=new Gson().fromJson(change.getNewValue(), JsonObject.class);
          }
          if(change.getPropertyName().equals(VERIFICATION_CODE_FAILURE)){
            verificationCodeFailureCount = Long.parseLong(change.getNewValue());
          }
          if(change.getPropertyName().equals(VERIFICATION_CODE_EXPIRED)){
            verificationCodeExpired = change.getNewValue();
          }
          if(change.getPropertyName().equals(VERIFICATION_CODE_SUBJECT)){
            verificationCodeSubject = change.getNewValue();
          }
          if(change.getPropertyName().equals(VERIFICATION_CODE_TEMPLATE)){
            verificationCodeTemplate = change.getNewValue();
          }
          if(change.getPropertyName().equals(VERIFICATION_CODE_SEND_INTERVAL)){
            verificationCodeSendInterval = Long.parseLong(change.getNewValue());
          }
          if(change.getPropertyName().equals(CHATIVE_INVITATION_PER_DAY)){
            chativeInvitationPerDay = change.getNewValue();
          }
          if (change.getPropertyName().equals(ACC_MAX_MSG_ENC_VERSION)) {
            maxMsgEncVersion = change.getNewValue() == null ? 255 : Integer.parseInt(change.getNewValue());
          }
            if (change.getPropertyName().equals(V3_MESSAGE_SUPPORT)) {
                v3MessageSupport = parseString2map(change.getNewValue());
            }
        }
      }
    });
  }

  public JsonArray getEmailDomains() {
    return emailDomains;
  }

  public void setEmailDomains(JsonArray emailDomains) {
    this.emailDomains = emailDomains;
  }

  public JsonObject getExtConfig() {
    return extConfig;
  }

  public void setExtConfig(JsonObject extConfig) {
    this.extConfig = extConfig;
  }

  public long getVerificationCodeFailureCount() {
    return verificationCodeFailureCount;
  }


  public String getVerificationCodeExpired() {
    return verificationCodeExpired;
  }

  public void setVerificationCodeExpired(String verificationCodeExpired) {
    this.verificationCodeExpired = verificationCodeExpired;
  }

  public String getVerificationCodeSubject() {
    return verificationCodeSubject;
  }

  public void setVerificationCodeSubject(String verificationCodeSubject) {
    this.verificationCodeSubject = verificationCodeSubject;
  }

  public String getVerificationCodeTemplate() {
    return verificationCodeTemplate;
  }

  public void setVerificationCodeTemplate(String verificationCodeTemplate) {
    this.verificationCodeTemplate = verificationCodeTemplate;
  }

  public long getVerificationCodeSendInterval() {
    return verificationCodeSendInterval;
  }


  public String getChativeInvitationPerDay() {
    return chativeInvitationPerDay;
  }

  public void setChativeInvitationPerDay(String chativeInvitationPerDay) {
    this.chativeInvitationPerDay = chativeInvitationPerDay;
  }

  public Map<String, Boolean> getV3MessageSupport() {
    return v3MessageSupport;
  }

  public Map<String, Boolean> parseString2map(String str)  {
    final JsonArray arr = new Gson().fromJson(str, JsonArray.class);
    Map<String, Boolean>  map = new HashMap<>(arr.size());
    for ( JsonElement e : arr) {
      map.put(e.getAsString(), Boolean.TRUE);
    }
    return map;
  }

  public static void main(String[] args) {
    System.out.println("hello world");
  }
}
