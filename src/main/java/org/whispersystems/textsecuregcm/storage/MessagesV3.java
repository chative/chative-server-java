package org.whispersystems.textsecuregcm.storage;

import org.jdbi.v3.sqlobject.customizer.*;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

public interface MessagesV3 {

    String ID                 = "id";
    String TYPE               = "type";
    String RELAY              = "relay";
    String TIMESTAMP          = "timestamp";
    String SOURCE             = "source";
    String SOURCE_DEVICE      = "source_device";
    String DESTINATION        = "destination";
    String DESTINATION_DEVICE = "destination_device";
    String MESSAGE            = "message";
    String CONTENT            = "content";
    String NOTIFY       = "notify";
    String RECEIVE_TYPE       = "receive_type";
    String PRIORITY = "priority";
    String SYSTEM_SHOW_TIMESTAMP = "system_show_timestamp";
    String SEQUENCE_ID = "sequence_id";
    String NOTIFY_SEQUENCE_ID = "notify_sequence_id";
    String CONVERSATION       = "conversation";
    String MSG_TYPE       = "msg_type";
    String NOTIFY_MSG_TYPE       = "notify_msg_type";


  @org.jdbi.v3.sqlobject.statement.SqlBatch("INSERT INTO messages (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY +", " + PRIORITY +", " + SYSTEM_SHOW_TIMESTAMP+", " + SEQUENCE_ID+", " + NOTIFY_SEQUENCE_ID+", " + CONVERSATION+", " + MSG_TYPE+", " + NOTIFY_MSG_TYPE+ ") " +
          "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify, :priority, :system_show_timestamp,:sequence_id,:notify_sequence_id,:conversation,:msg_type,:notify_msg_type) ")
  @org.jdbi.v3.sqlobject.statement.GetGeneratedKeys("id")
  long[] storeBatch(@MessageBinder List<Envelope> message,
                      @Bind("destination") List<String> destination,
                      @Bind("destination_device") List<Long> destinationDevice,
                      @Bind("notify") List<Boolean> notify,
                      @Bind("priority") List<Integer> priority,
                      @Bind("conversation") List<String> conversation,
                      @Bind("notify_msg_type") List<Integer> notifyMsgType);

  @SqlStatementCustomizingAnnotation(MessageBinder.AccountBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  @interface MessageBinder {
    class AccountBinderFactory implements SqlStatementCustomizerFactory {
        public AccountBinderFactory() {
        }

        public SqlStatementParameterCustomizer createForParameter(Annotation annotation, Class<?> sqlObjectType, Method method, Parameter param, int index, Type type) {
            return (stmt, arg) -> {
                Envelope message=(Envelope)arg;
                stmt.bind(TYPE, message.getType().getNumber());
                stmt.bind(RELAY, message.getRelay());
                stmt.bind(TIMESTAMP, message.getTimestamp());
                stmt.bind(SOURCE, message.getSource());
                stmt.bind(SOURCE_DEVICE, message.getSourceDevice());
                stmt.bind(MESSAGE, message.hasLegacyMessage() ? message.getLegacyMessage().toByteArray() : null);
                stmt.bind(CONTENT, message.hasContent() ? message.getContent().toByteArray() : null);
                stmt.bind(SYSTEM_SHOW_TIMESTAMP, message.getSystemShowTimestamp());
                stmt.bind(SEQUENCE_ID, message.getSequenceId());
                stmt.bind(NOTIFY_SEQUENCE_ID, message.getNotifySequenceId());
                stmt.bind(MSG_TYPE, message.getMsgType().getNumber());
            };
        }
    }
  }

}
