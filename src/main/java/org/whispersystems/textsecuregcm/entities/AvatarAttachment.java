package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvatarAttachment {

    @JsonProperty
    private long attachmentId;

    public AvatarAttachment() {
    }

    public AvatarAttachment(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
