package org.whispersystems.textsecuregcm.storage;

public class BotPropertyRow {
    Boolean autoAnswer;
    String space;
    String answerServerHost;

    public BotPropertyRow(Boolean autoAnswer, String space, String answerServerHost) {
        this.autoAnswer = autoAnswer;
        this.space = space;
        this.answerServerHost = answerServerHost;
    }
}
