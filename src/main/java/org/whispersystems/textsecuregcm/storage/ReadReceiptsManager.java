package org.whispersystems.textsecuregcm.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class ReadReceiptsManager {
  private final Logger logger = LoggerFactory.getLogger(ReadReceiptsManager.class);

  private final ReadReceiptsTable readReceiptsTable;
  private MemCache memCache = null;
  private MessagesManager messagesManager;

  public ReadReceiptsManager(ReadReceiptsTable readReceiptsTable, MemCache memCache) {
    this.readReceiptsTable=readReceiptsTable;
    this.memCache=memCache;
  }

  public void insert(ReadReceipt readReceipt) {
    readReceiptsTable.insert(readReceipt);
    setMaxReadReceiptMemCache(readReceipt);
    messagesManager.updatePushConversationForRead(readReceipt.getSource(),readReceipt.getConversation(),readReceipt.getMaxNotifySequenceId(),readReceipt.getSourceDevice());
  }
  public void insertBatch(List<ReadReceipt> readReceipts) {
    readReceiptsTable.insertBatch(readReceipts);
    setMaxReadReceiptMemCache(readReceipts.get(0));
    if(readReceipts.get(0).getMaxNotifySequenceId()>0) {
      messagesManager.updatePushConversationForRead(readReceipts.get(0).getSource(), readReceipts.get(0).getConversation(), readReceipts.get(0).getMaxNotifySequenceId(),readReceipts.get(0).getSourceDevice());
    }
  }

  private void setMaxReadReceiptMemCache(ReadReceipt readReceipt){
    String maxReadReceiptKey=getMaxReadReceiptKey(readReceipt.getSource());
    ReadReceipt readReceiptTemp= (ReadReceipt) memCache.hget(maxReadReceiptKey,readReceipt.getConversation(),ReadReceipt.class);
    if(readReceiptTemp==null||readReceiptTemp.getMaxServerTimestamp()<readReceipt.getMaxServerTimestamp()){
      memCache.hset(maxReadReceiptKey,readReceipt.getConversation(),readReceipt);
    }
  }

  public ReadReceipt getMaxReadReceipt(String number,String conversation){
    String maxReadReceiptKey=getMaxReadReceiptKey(number);
    ReadReceipt readReceipt= (ReadReceipt) memCache.hget(maxReadReceiptKey,conversation,ReadReceipt.class);
    if(readReceipt==null){
      readReceipt =readReceiptsTable.getMaxBySourceAndConversation(number,conversation);
      if(readReceipt==null){
          readReceipt=new ReadReceipt(conversation,number,-1,0L,0L,0L,0L);
      }
      memCache.hset(maxReadReceiptKey,conversation,readReceipt);
    }
    return readReceipt;
  }
  public List<ReadReceipt> getBySource(String destination, long destinationDevice) {
    return readReceiptsTable.getBySource(destination,destinationDevice);
  }

  public List<ReadReceipt> getBySourceAndConversation(String destination, long destinationDevice,String conversation) {
    return readReceiptsTable.getBySourceAndConversation(destination,destinationDevice,conversation);
  }

  private String getReadReceiptKey(ReadReceipt readReceipt){
    return String.join("_",ReadReceiptsManager.class.getSimpleName(), "ReadReceipt", String.valueOf(ReadReceipt.MEMCACHE_VERION), readReceipt.getSource(),readReceipt.getSourceDevice()+"");
  }

  private String getMaxReadReceiptKey(String number){
    return String.join("_",ReadReceiptsManager.class.getSimpleName(), "MaxReadReceipt", String.valueOf(ReadReceipt.MEMCACHE_VERION), number);
  }

  public void setMessagesManager(MessagesManager messagesManager) {
    this.messagesManager = messagesManager;
  }
}
