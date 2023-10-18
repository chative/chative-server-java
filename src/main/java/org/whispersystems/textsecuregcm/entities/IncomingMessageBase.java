package org.whispersystems.textsecuregcm.entities;

import java.util.List;

public interface IncomingMessageBase {
    public int getType();
    public String getContent();
    public String getRelay();
    public long getTimestamp();
    public Notification getNotification();
    public boolean isReadReceipt();
    public Long getSequenceId();
    public long getSystemShowTimestamp();
    public Long getNotifySequenceId();
    public int getMsgType();
    public IncomingMessage.Conversation getConversation();
    public List<IncomingMessage.ReadPosition> getReadPositions();
    public IncomingMessage.RealSource getRealSource();
}
