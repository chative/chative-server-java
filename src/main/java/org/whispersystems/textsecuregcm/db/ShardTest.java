package org.whispersystems.textsecuregcm.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.MessageProtos;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntity;
import org.whispersystems.textsecuregcm.storage.MessagesTest;

import java.util.List;
import java.util.UUID;

public class ShardTest {
    private final Logger logger = LoggerFactory.getLogger(ShardTest.class);

    MessagesTest messagesTest;
    public ShardTest(MessagesTest messagesTest){
        this.messagesTest=messagesTest;
        init();
    }
    public void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageProtos.Envelope.Builder messageBuilder = MessageProtos.Envelope.newBuilder();
                messageBuilder.setType(MessageProtos.Envelope.Type.valueOf(1))
                        .setSource("source.getNumber()")
                        .setTimestamp(System.currentTimeMillis() )
                        .setSourceDevice(1)
                        .setSequenceId(1)
                        .setRelay("")
                        .setSystemShowTimestamp(System.currentTimeMillis());
                String querydes=null;
                for(int i=0;i<100;i++) {
                    String des= UUID.randomUUID().toString();
                    long id=messagesTest.store(messageBuilder.build(),des,1,false );
                    List<OutgoingMessageEntity> outgoingMessageEntityList= messagesTest.load(des,1);
                    logger.info("{},id:{},outgoingMessageEntityList.size========{}",des,id,outgoingMessageEntityList.size());
                }

            }
        }).start();

    }
}
