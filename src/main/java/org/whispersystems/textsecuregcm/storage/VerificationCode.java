package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 账号绑定邮箱，邮箱登录验证码
 */
public class VerificationCode implements Serializable {

    @JsonProperty
    private String email;

    @JsonProperty
    private String verificationCode;

    @JsonProperty
    private long timestamp;

    public VerificationCode(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public VerificationCode(String email, String verificationCode, long timestamp){
        this.email = email;
        this.verificationCode = verificationCode;
        this.timestamp = timestamp;
    }
}
