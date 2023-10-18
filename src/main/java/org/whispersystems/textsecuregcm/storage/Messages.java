package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@UseStringTemplate3StatementLocator
public abstract class Messages {

  public static final int RESULT_SET_CHUNK_SIZE = 100;

  private static final String ID                 = "id";
  private static final String TYPE               = "type";
  private static final String RELAY              = "relay";
  private static final String TIMESTAMP          = "timestamp";
  private static final String SOURCE             = "source";
  private static final String SOURCE_DEVICE      = "source_device";
  private static final String DESTINATION        = "destination";
  private static final String DESTINATION_DEVICE = "destination_device";
  private static final String MESSAGE            = "message";
  private static final String CONTENT            = "content";
  private static final String NOTIFY       = "notify";
  private static final String RECEIVE_TYPE       = "receive_type";
  private static final String PRIORITY = "priority";
  private static final String SYSTEM_SHOW_TIMESTAMP = "system_show_timestamp";
  private static final String SEQUENCE_ID = "sequence_id";
  private static final String NOTIFY_SEQUENCE_ID = "notify_sequence_id";
  private static final String CONVERSATION       = "conversation";
  private static final String MSG_TYPE       = "msg_type";
  private static final String NOTIFY_MSG_TYPE       = "notify_msg_type";

  private static final String SOURCE_IK       = "source_ik";

  private static final String PEER_CONTEXT       = "peer_context";

  public enum PRIORITY {
    PERSONAL(100),
    SYNC(200),
    READ_RECEIPT(700),
    NOTIFY(400),
    GROUP(500),
    BOT(600);

    private int  value=0;
    PRIORITY(int value)
    {
      this.value=value;
    }

    public int getValue()
    {
      return value;
    }

  }

  @SqlUpdate("INSERT INTO messages (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY +", " + PRIORITY +", " + SYSTEM_SHOW_TIMESTAMP+", " + SEQUENCE_ID+", " + NOTIFY_SEQUENCE_ID+", " + CONVERSATION +", " + MSG_TYPE+", " + NOTIFY_MSG_TYPE+", " + SOURCE_IK +", " + PEER_CONTEXT+ ") " +
           "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify, :priority, :system_show_timestamp,:sequence_id,:notify_sequence_id ,:conversation,:msg_type,:notify_msg_type, :source_ik, :peer_context) ")
  @GetGeneratedKeys(columnName="id")
  abstract long store(@MessageBinder Envelope message,
                     @Bind("destination") String destination,
                     @Bind("destination_device") long destinationDevice,
                     @Bind("notify") boolean notify,
                     @Bind("priority") int priority,
                     @Bind("conversation") String conversation,
                      @Bind("notify_msg_type") int notifyMsgType,
                      @Bind("source_ik") String sourceIK,
                      @Bind("peer_context") String peerContext);


  //@org.jdbi.v3.sqlobject.statement.SqlBatch("INSERT INTO messages (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY +", " + PRIORITY +", " + SYSTEM_SHOW_TIMESTAMP+", " + SEQUENCE_ID+", " + NOTIFY_SEQUENCE_ID+", " + CONVERSATION+", " + MSG_TYPE+ ") " +
  //        "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify, :priority, :system_show_timestamp,:sequence_id,:notify_sequence_id,:conversation,:msg_type) ")
  //@org.jdbi.v3.sqlobject.statement.GetGeneratedKeys("id")
  //abstract long[] storeBatch(@MessageBinder List<Envelope> message,
  //                    @Bind("destination") List<String> destination,
  //                    @Bind("destination_device") List<Long> destinationDevice,
  //                    @Bind("notify") List<Boolean> notify,
  //                    @Bind("priority") List<Integer> priority,
  //                    @Bind("conversation") List<String> conversation);

  @SqlUpdate("INSERT INTO messages (" + TYPE + ", " + RELAY + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE + ", " + DESTINATION + ", " + DESTINATION_DEVICE + ", " + MESSAGE + ", " + CONTENT + ", " + NOTIFY +", " + RECEIVE_TYPE +", " + SYSTEM_SHOW_TIMESTAMP+", " + SEQUENCE_ID+", " + NOTIFY_SEQUENCE_ID+", " + MSG_TYPE+  ") " +
          "VALUES (:type, :relay, :timestamp, :source, :source_device, :destination, :destination_device, :message, :content, :notify, :receive_type, :system_show_timestamp,:sequence_id,:notify_sequence_id,:msg_type) ")
  abstract void storeForReceiveType(@MessageBinder Envelope message,
                     @Bind("destination") String destination,
                     @Bind("destination_device") long destinationDevice,
                     @Bind("notify") boolean notify,
                     @Bind("receive_type") int receiveType);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages WHERE " + RECEIVE_TYPE + " = 1   ORDER BY " + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntity> loadForKafKa();

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device  ORDER BY "  + PRIORITY+"," + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntity> load(@Bind("destination")        String destination,
                                            @Bind("destination_device") long destinationDevice);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device  ORDER BY "  + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntity> loadOld(@Bind("destination")        String destination,
                                            @Bind("destination_device") long destinationDevice);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages WHERE " + DESTINATION + " = :destination AND "  + CONVERSATION + " = :conversation AND " + DESTINATION_DEVICE + " = :destination_device  ORDER BY "  + TIMESTAMP + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntity> loadByConversation(@Bind("destination")        String destination,
                                                          @Bind("destination_device") long destinationDevice,
                                                          @Bind("conversation") String conversation);
  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages WHERE " + ID + " in (<ids>)" )
  abstract List<OutgoingMessageEntity> loadByIds(@BindIn("ids") List<Long> ids);

  @Mapper(MessageMapperForRemind.class)
  @SqlQuery("SELECT SOURCE,\n" +
          "\tDESTINATION\n" +
          "FROM\n" +
          "\t(SELECT *,\n" +
          "\t\t\tROW_NUMBER() OVER (PARTITION BY DESTINATION) AS ROW_ID\n" +
          "\t\tFROM\n" +
          "\t\t\t(SELECT SOURCE,\n" +
          "\t\t\t\t\tDESTINATION\n" +
          "\t\t\t\tFROM MESSAGES\n" +
          "\t\t\t\tWHERE TYPE != 6\n" +
          "\t\t\t\t\tAND TIMESTAMP >= :begin \n" +
          "\t\t\t\t\tAND TIMESTAMP <= :end \n" +
          "\t\t\t\t\tAND SOURCE != DESTINATION\n" +
          "\t\t\t\t\tAND notify = true\n" +
          "\t\t\t\t\tand length(SOURCE)=12\n" +
          "\t\t\t\tGROUP BY SOURCE,\n" +
          "\t\t\t\t\tDESTINATION) TT) T\n" +
          "WHERE T.ROW_ID <= 5\n" +
          "LIMIT "+ RESULT_SET_CHUNK_SIZE +" offset :offset " )
  abstract List<OutgoingMessageEntityForRemind> loadByTimeRange( @Bind("begin") long begin, @Bind("end") long end, @Bind("offset") long offset);


  @SqlUpdate("delete from MESSAGES where id= any(array (\n" +
          "\t\tSELECT ID\n" +
          "\t\tFROM\n" +
          "\t\t\t(SELECT ID,\n" +
          "\t\t\t\tDESTINATION,\n" +
          "\t\t\t\tDESTINATION_DEVICE,\n" +
          "\t\t\t\tmax(id) OVER DEST maxid,\n" +
          "\t\t\t\tCOUNT(1) OVER DEST\n" +
          "\t\t\t\tFROM MESSAGES\n" +
          "\t\t\t\tWHERE NOTIFY_MSG_TYPE = 1 and msg_type= 6\n" +
          "\t\t\t\tWINDOW DEST AS (PARTITION BY DESTINATION,DESTINATION_DEVICE  )\n" +
          "\t\t\t) t \n" +
          "\t\twhere t.id != maxid and t.count>=:notifyCountThreshold \n" +
          "))")
  abstract long removeForNotifyMerge( @Bind("notifyCountThreshold") int notifyCountThreshold);

  @Mapper(MessageMapper.class)
  @SqlQuery("DELETE FROM messages WHERE " + ID + " IN (SELECT " + ID + " FROM messages WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device AND " + SOURCE + " = :source AND " + TIMESTAMP + " = :timestamp ORDER BY " + ID + " LIMIT 1) RETURNING *")
  abstract OutgoingMessageEntity remove(@Bind("destination")        String destination,
                                        @Bind("destination_device") long destinationDevice,
                                        @Bind("source")             String source,
                                        @Bind("timestamp")          long timestamp);

  @Mapper(DestinationMapper.class)
  @SqlQuery("SELECT DISTINCT ON (destination, destination_device) destination, destination_device FROM messages WHERE timestamp > :timestamp ORDER BY destination, destination_device OFFSET :offset LIMIT :limit")
  public abstract List<Pair<String, Integer>> getPendingDestinations(@Bind("timestamp") long sinceTimestamp, @Bind("offset") int offset, @Bind("limit") int limit);

  @Mapper(MessageMapper.class)
  @SqlUpdate("DELETE FROM messages WHERE " + ID + " = :id AND " + DESTINATION + " = :destination")
  abstract void remove(@Bind("destination") String destination, @Bind("id") long id);

  @SqlUpdate("DELETE FROM messages WHERE " + DESTINATION + " = :destination")
  abstract void clear(@Bind("destination") String destination);

  @SqlUpdate("DELETE FROM messages WHERE " + DESTINATION + " = :destination AND " + DESTINATION_DEVICE + " = :destination_device")
  abstract void clear(@Bind("destination") String destination, @Bind("destination_device") long destinationDevice);

  @SqlUpdate("DELETE FROM messages WHERE " + DESTINATION + " = :destination AND " +SOURCE +" =:source AND "+PRIORITY +" = :priority")
  abstract void clear(@Bind("destination") String destination,@Bind("source") String source,@Bind("priority") Integer priority);

  @SqlUpdate("DELETE FROM messages WHERE " + TIMESTAMP + " \\< :timestamp")
  public abstract long removeOld(@Bind("timestamp") long timestamp);

  @SqlUpdate("DELETE FROM messages WHERE " + TIMESTAMP + " \\< :timestamp and source ~ :sourceRegex")
  public abstract long removeOldBySource(@Bind("sourceRegex") String sourceRegex,@Bind("timestamp") long timestamp);

  @SqlUpdate("update messages set "+MSG_TYPE+"="+Envelope.MsgType.MSG_RECALLED_VALUE+" WHERE " + SOURCE + " = :source" + SOURCE_DEVICE + " = :source_device" + TIMESTAMP + " = :timestamp")
  abstract void updateForRecall(@Bind("source") String source, @Bind("source_device") long sourceDevice,@Bind("timestamp") long timestamp);

  @SqlUpdate("VACUUM messages")
  public abstract void vacuum();

  @SqlQuery("SELECT count(1) FROM messages")
  public abstract long count();

  @SqlUpdate("analyze messages")
  public abstract void analyze();

//  @SqlQuery("select n_live_tup as estimate_rows from pg_stat_all_tables where relname = 'messages'")
  @SqlQuery("select count_estimate('select 1 from messages')")
  public abstract long countByEstimate();

  public static class DestinationMapper implements ResultSetMapper<Pair<String, Integer>> {

    @Override
    public Pair<String, Integer> map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new Pair<>(resultSet.getString(DESTINATION), resultSet.getInt(DESTINATION_DEVICE));
    }
  }

  public static class MessageMapper implements ResultSetMapper<OutgoingMessageEntity> {
    @Override
    public OutgoingMessageEntity map(int i, ResultSet resultSet, StatementContext statementContext)
        throws SQLException
    {

      int    type          = resultSet.getInt(TYPE);
      byte[] legacyMessage = resultSet.getBytes(MESSAGE);
      long   destination_device       = resultSet.getLong(DESTINATION_DEVICE);
      long   receiveType       = resultSet.getLong(RECEIVE_TYPE);

      if (type == Envelope.Type.RECEIPT_VALUE && legacyMessage == null) {
        /// XXX - REMOVE AFTER 10/01/15
        legacyMessage = new byte[0];
      }
      if(receiveType==1){
        return new OutgoingMessageEntityForKafka(resultSet.getLong(ID),
                false,
                type,
                resultSet.getString(RELAY),
                resultSet.getLong(TIMESTAMP),
                resultSet.getString(SOURCE),
                resultSet.getInt(SOURCE_DEVICE),
                legacyMessage,
                resultSet.getBytes(CONTENT),
                resultSet.getBoolean(NOTIFY),
                resultSet.getString(DESTINATION),
                destination_device,
                resultSet.getLong(SYSTEM_SHOW_TIMESTAMP),
                resultSet.getLong(SEQUENCE_ID),
                resultSet.getLong(NOTIFY_SEQUENCE_ID),
                resultSet.getInt(MSG_TYPE),
                resultSet.getString(CONVERSATION)
                );
      }
      return new OutgoingMessageEntity(resultSet.getLong(ID),
                                       false,
                                       type,
                                       resultSet.getString(RELAY),
                                       resultSet.getLong(TIMESTAMP),
                                       resultSet.getString(SOURCE),
                                       resultSet.getInt(SOURCE_DEVICE),
                                       legacyMessage,
                                       resultSet.getBytes(CONTENT),
                                       resultSet.getBoolean(NOTIFY),
                                       resultSet.getLong(SYSTEM_SHOW_TIMESTAMP),
              resultSet.getLong(SEQUENCE_ID),
              resultSet.getLong(NOTIFY_SEQUENCE_ID),
              resultSet.getInt(MSG_TYPE),
              resultSet.getString(CONVERSATION),
              resultSet.getString(SOURCE_IK),
              resultSet.getString(PEER_CONTEXT));
    }
  }

  @BindingAnnotation(MessageBinder.AccountBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface MessageBinder {
    public static class AccountBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<MessageBinder, Envelope>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           MessageBinder accountBinder,
                           Envelope message)
          {
            sql.bind(TYPE, message.getType().getNumber());
            sql.bind(RELAY, message.getRelay());
            sql.bind(TIMESTAMP, message.getTimestamp());
            sql.bind(SOURCE, message.getSource());
            sql.bind(SOURCE_DEVICE, message.getSourceDevice());
            sql.bind(MESSAGE, message.hasLegacyMessage() ? message.getLegacyMessage().toByteArray() : null);
            sql.bind(CONTENT, message.hasContent() ? message.getContent().toByteArray() : null);
            sql.bind(SYSTEM_SHOW_TIMESTAMP, message.getSystemShowTimestamp());
            sql.bind(SEQUENCE_ID, message.getSequenceId());
            sql.bind(NOTIFY_SEQUENCE_ID, message.getNotifySequenceId());
            sql.bind(MSG_TYPE, message.getMsgType().getNumber());
          }
        };
      }
    }
  }

  public static class MessageMapperForRemind implements ResultSetMapper<OutgoingMessageEntityForRemind> {
    @Override
    public OutgoingMessageEntityForRemind map(int i, ResultSet resultSet, StatementContext statementContext)
            throws SQLException
    {
      return new OutgoingMessageEntityForRemind(
              resultSet.getString(SOURCE),
              resultSet.getString(DESTINATION));
    }
  }
}
