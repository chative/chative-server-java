package org.whispersystems.textsecuregcm.util;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.KafkaConfiguration;

import java.util.Properties;

public class ProducerKafka<K,V> {
    private static final Logger log = LoggerFactory.getLogger(ProducerKafka.class);
    public Producer<K, V> procuder;
    KafkaConfiguration kafkaConfiguration;
    public ProducerKafka(KafkaConfiguration kafkaConfiguration){
        this.kafkaConfiguration=kafkaConfiguration;
    }

    /**
     * 向kafka发送消息
     * @param message
     * @return
     */
    public void sendMessage(ProducerRecord message) {
        if(procuder==null){
            initProcuder();
        }
        procuder.send(message, new Callback() {
             @Override
             public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                 if (null != e) {
                     log.error("sendMessage to kafka error! case:{} msg:{}",e.getMessage(),message.value());
                 }
             }
         });
        procuder.flush();
    }

    /**
     *  向kafka发送消息
     * @param topic 主题
     * @param value 值
     * @throws Exception
     */
    public void sendMessage(String topic,K key, V value, Callback callback) {
        if(procuder==null){
            initProcuder();
        }
        procuder.send(new ProducerRecord<K,V>(topic, key, value), callback);
    }


    /**
     *  向kafka发送消息
     * @param topic 主题
     * @param value 值
     * @throws Exception
     */
    public void sendMessage(String topic, V value){
        sendMessage(new ProducerRecord<K, V>(topic, value));
    }

    /**
     *  向kafka发送消息
     * @param topic 主题
     * @param value 值
     * @throws Exception
     */
    public void sendMessage(String topic,K key, V value) {
        sendMessage(new ProducerRecord<K, V>(topic, key, value));
    }
    /**
     * 刷新缓存
     */
    public void flush()  {
        if(procuder!=null) {
            procuder.flush();
        }
    }


    /**
     * 关闭连接
     */
    public void close() {
        if(procuder!=null) {
            procuder.close();
        }
    }

    public void initProcuder(){
        if (procuder == null) {
            synchronized (ProducerKafka.class) {
                if (procuder == null) {
                    Properties props = new Properties();
                    props.put("bootstrap.servers", kafkaConfiguration.getServers());
                    props.put("acks", kafkaConfiguration.getAcks());
                    props.put("retries", kafkaConfiguration.getRetries());
                    props.put("batch.size", kafkaConfiguration.getBatchSize());
                    props.put("linger.ms",kafkaConfiguration.getLingerMs());
                    props.put("buffer.memory", kafkaConfiguration.getBufferMemory());
                    props.put("key.serializer", kafkaConfiguration.getKeySerializer());
                    props.put("value.serializer", kafkaConfiguration.getValueSerializer());
                    props.put("security.protocol", kafkaConfiguration.getSecurityProtocol());
                    procuder = new KafkaProducer(props);
                }
            }
        }
    }
}