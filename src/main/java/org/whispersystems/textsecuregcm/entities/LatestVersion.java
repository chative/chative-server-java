package org.whispersystems.textsecuregcm.entities;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

public class LatestVersion {

    @NotNull
    @JsonProperty
    private String latest;

    @NotNull
    @JsonProperty
    private String description;

    @NotNull
    @JsonProperty
    private String apkurl;

    @NotNull
    @JsonProperty
    private String sha256sum;

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSha256sum() {
        return sha256sum;
    }

    public void setSha256sum(String sha256sum) {
        this.sha256sum = sha256sum;
    }

    public LatestVersion(String latest, String description, String apkurl, String sha256sum) {
        this.latest = latest;
        this.description = description;
        this.apkurl = apkurl;
        this.sha256sum = sha256sum;
    }
}
