package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TpnConfiguration {


    @JsonProperty
    String secretKey;

    @JsonProperty
    String appId;

    @JsonProperty
    String domainUrl;

    @JsonProperty
    String title;

    @JsonProperty
    String defaultContent;

    @JsonProperty
    String callContent;


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCallContent() {
        return callContent;
    }

    public void setCallContent(String callContent) {
        this.callContent = callContent;
    }

    public String getDefaultContent() {
        return defaultContent;
    }

    public void setDefaultContent(String defaultContent) {
        this.defaultContent = defaultContent;
    }
}
