package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class JssConfiguration  {

    @NotEmpty
    @JsonProperty
    private String accessKey;

    @NotEmpty
    @JsonProperty
    private String secretKey;

    @NotEmpty
    @JsonProperty
    private String attachmentBucket;

    @NotEmpty
    @JsonProperty
    private String avatarBucket;

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAttachmentBucket() {
        return attachmentBucket;
    }

    public String getAvatarBucket() {
        return avatarBucket;
    }
}
