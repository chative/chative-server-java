package org.whispersystems.textsecuregcm.entities;

public class UserProfile {
    String uid, emailHash, phoneHash;

    public UserProfile(String uid, String emailHash, String phoneHash) {
        this.uid = uid;
        this.emailHash = emailHash;
        this.phoneHash = phoneHash;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }
}
