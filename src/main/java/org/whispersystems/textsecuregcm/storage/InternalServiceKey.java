package org.whispersystems.textsecuregcm.storage;

import com.github.difftim.security.signing.SignatureVerifier;

import java.util.List;

public class InternalServiceKey extends SignatureVerifier.Key {

  private String appid;

  public InternalServiceKey(){
    super(null, null, 0, null);
  };
  public InternalServiceKey(String appid,String algorithm, byte[] key, int signatureExpireTime, List<String> allowedIPList) {
    super(algorithm, key, signatureExpireTime, allowedIPList);
    this.appid=appid;
  }

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

}
