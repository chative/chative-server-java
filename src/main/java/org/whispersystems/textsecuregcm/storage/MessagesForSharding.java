package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityForSharding;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@UseStringTemplate3StatementLocator
public abstract class MessagesForSharding {

  public static final int RESULT_SET_CHUNK_SIZE = 100;

  private static final String ID                 = "id";
  private static final String TYPE               = "type";
  private static final String TIMESTAMP          = "timestamp";
  private static final String SOURCE             = "source";
  private static final String SOURCE_DEVICE      = "source_device";
  private static final String CONTENT            = "content";
  private static final String NOTIFY       = "notify";
  private static final String MSG_TYPE       = "msg_type";
  private static final String RECEIVE_TYPE       = "receive_type";
  private static final String CONVERSATION       = "conversation";
  private static final String SEQUENCE_ID       = "sequence_id";
  private static final String PUSH_TYPE       = "push_type";
  private static final String NOTIFY_SEQUENCE_ID="notify_sequence_id";
  private static final String SYSTEM_SHOW_TIMESTAMP = "system_show_timestamp";
  private static final String NOTIFY_MSG_TYPE       = "notify_msg_type";



  @SqlUpdate("INSERT INTO messages_shard (" + TYPE + ", " + TIMESTAMP + ", " + SOURCE + ", " + SOURCE_DEVICE +  ", " + CONTENT + ", " + NOTIFY + ", " + MSG_TYPE + ", " + CONVERSATION + ", " + SEQUENCE_ID + ", " + PUSH_TYPE+ ", " + NOTIFY_SEQUENCE_ID + ", " + SYSTEM_SHOW_TIMESTAMP +", " + NOTIFY_MSG_TYPE+") " +
           "VALUES (:type, :timestamp, :source, :source_device, :content, :notify ,:msg_type, :conversation, :sequence_id, :push_type , :notify_sequence_id,:system_show_timestamp,:notify_msg_type) ")
  @GetGeneratedKeys(columnName="id")
  abstract long store(@MessageBinder Envelope message,
                     @Bind("notify") boolean notify,@Bind("conversation") String conversation,@Bind("push_type") int push_type,@Bind("notify_msg_type") int notifyMsgType);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation and " + SYSTEM_SHOW_TIMESTAMP + " >= :beginTime order by sequence_id desc limit " +RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntityForSharding> load(@Bind("conversation")        String conversation, @Bind("beginTime") long beginTime);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + ID + " in (<ids>) order by sequence_id ")
  abstract List<OutgoingMessageEntityForSharding> loadByIds(@Bind("conversation")        String conversation,
                                            @BindIn("ids") List<Long> ids);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + ID + " =:id order by sequence_id ")
  abstract OutgoingMessageEntityForSharding loadById(@Bind("conversation")        String conversation,
                                                            @Bind("id") long id);

  @SqlQuery("SELECT min(sequence_id) FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + NOTIFY_SEQUENCE_ID + " >= :notify_sequence_id  ")
  abstract long loadByNsId(@Bind("conversation")        String conversation,
                                                     @Bind("notify_sequence_id") long notify_sequence_id);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SYSTEM_SHOW_TIMESTAMP + " >= :startTime and "+SYSTEM_SHOW_TIMESTAMP+" \\<= :endTime order by sequence_id limit 1")
  abstract OutgoingMessageEntityForSharding loadOldest(@Bind("conversation")        String conversation,
                                                     @Bind("startTime") long startTime,@Bind("endTime") long endTime);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SYSTEM_SHOW_TIMESTAMP + " >= :startTime and "+SYSTEM_SHOW_TIMESTAMP+" \\<= :endTime order by sequence_id desc limit 1")
  abstract OutgoingMessageEntityForSharding loadLastest(@Bind("conversation")        String conversation,
                                                       @Bind("startTime") long startTime,@Bind("endTime") long endTime);

  @Mapper(MessageMapper.class)
//  @SqlQuery("SELECT *\n" +
//          "FROM MESSAGES_SHARD\n" +
//          "WHERE ID in\n" +
//          "\t\t(SELECT\n" +
//          "\t\t\t\tUNNEST(Array[MIN(ID),MAX(ID)]) as id\n" +
//          "\t\t\tFROM MESSAGES_SHARD\n" +
//          "\t\t\tWHERE CONVERSATION = :conversation\n" +
//          "\t\t\t\tAND SYSTEM_SHOW_TIMESTAMP >= :startTime\n" +
//          "\t\t\t\tAND SYSTEM_SHOW_TIMESTAMP \\<= :endTime)\n" +
//          "ORDER BY ID ;")
  @SqlQuery("select t.*\n" +
          "from messages_shard t ,(select\n" +
          "\t\t\t\tmin(id) as id\n" +
          "\t\t\tfrom messages_shard \n" +
          "\t\t\twhere conversation = :conversation\n" +
          "\t\t\t\tand system_show_timestamp >= :startTime\n" +
          "\t\t\t\tand system_show_timestamp \\<= :endTime union all select\n" +
          "\t\t\t\tmax(id) as id\n" +
          "\t\t\tfrom messages_shard \n" +
          "\t\t\twhere conversation = :conversation\n" +
          "\t\t\t\tand system_show_timestamp >= :startTime\n" +
          "\t\t\t\tand system_show_timestamp \\<= :endTime) b\n" +
          "where t.id=b.id and conversation=:conversation \n" +
          "order by t.id ;")
  abstract List<OutgoingMessageEntityForSharding> loadOldestAndLastest(@Bind("conversation")        String conversation,
                                                        @Bind("startTime") long startTime,@Bind("endTime") long endTime);


  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SEQUENCE_ID + " \\<= :maxSequenceId and " + SYSTEM_SHOW_TIMESTAMP + " >= :beginTime order by sequence_id desc limit "+RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntityForSharding> loadByMaxSequenceId(@Bind("conversation")        String conversation,
                                            @Bind("maxSequenceId") Long maxSequenceId, @Bind("beginTime") long beginTime);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SEQUENCE_ID + " >= :minSequenceId and " + SYSTEM_SHOW_TIMESTAMP + " >= :beginTime order by sequence_id limit "+RESULT_SET_CHUNK_SIZE)
  abstract List<OutgoingMessageEntityForSharding> loadByMinSequenceId(@Bind("conversation")        String conversation,
                                                                      @Bind("minSequenceId") Long minSequenceId, @Bind("beginTime") long beginTime);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SEQUENCE_ID + " in  (<sequenceIds>)  and " + SYSTEM_SHOW_TIMESTAMP + " >= :beginTime order by sequence_id ")
  abstract List<OutgoingMessageEntityForSharding> loadBySequenceIds(@Bind("conversation")        String conversation,
                                            @BindIn("sequenceIds") List<Long> sequenceIds, @Bind("beginTime") long beginTime);

  @Mapper(MessageMapper.class)
  @SqlQuery("SELECT * FROM messages_shard WHERE " + CONVERSATION + " = :conversation AND " + SEQUENCE_ID + " >= :minSequenceId and  "+SEQUENCE_ID+" \\<= :maxSequenceId and " + SYSTEM_SHOW_TIMESTAMP + " >= :beginTime order by sequence_id ")
  abstract List<OutgoingMessageEntityForSharding> loadBySequenceRange(@Bind("conversation")        String conversation,
                                                         @Bind("minSequenceId")Long minSequenceId,@Bind("maxSequenceId")Long maxSequenceId, @Bind("beginTime") long beginTime);
  @SqlQuery("SELECT max(sequence_id) FROM messages_shard WHERE " + CONVERSATION + " = :conversation ")
  abstract long getMaxSequenceId(@Bind("conversation")    String conversation);

  @SqlQuery("SELECT max(notify_sequence_id) FROM messages_shard WHERE " + CONVERSATION + " = :conversation ")
  abstract long getMaxNotifySequenceId(@Bind("conversation")    String conversation);

  @Mapper(MessageMapper.class)
  @SqlUpdate("DELETE FROM messages WHERE "  + CONVERSATION + " = :conversation AND " + ID + " = :id ")
  abstract void remove( @Bind("conversation")        String conversation, @Bind("id") long id);

  @SqlUpdate("DELETE FROM messages_shard WHERE " + TIMESTAMP + " \\< :timestamp")
  public abstract void removeOld(@Bind("timestamp") long timestamp);


  public static class MessageMapper implements ResultSetMapper<OutgoingMessageEntityForSharding> {
    @Override
    public OutgoingMessageEntityForSharding map(int i, ResultSet resultSet, StatementContext statementContext)
        throws SQLException
    {

      int    type          = resultSet.getInt(TYPE);
       return new OutgoingMessageEntityForSharding(resultSet.getLong(ID),
                                       false,
                                       type,
                                       resultSet.getLong(TIMESTAMP),
                                       resultSet.getString(SOURCE),
                                       resultSet.getInt(SOURCE_DEVICE),
                                       resultSet.getBytes(CONTENT),
                                       resultSet.getBoolean(NOTIFY),
                                       resultSet.getInt(MSG_TYPE),
              resultSet.getInt(RECEIVE_TYPE),
              resultSet.getString(CONVERSATION),
              resultSet.getLong(SEQUENCE_ID),
              resultSet.getInt(PUSH_TYPE),
               resultSet.getLong(NOTIFY_SEQUENCE_ID),
               resultSet.getLong(SYSTEM_SHOW_TIMESTAMP));
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
            sql.bind(MSG_TYPE, message.getMsgType().getNumber());
            sql.bind(TIMESTAMP, message.getTimestamp());
            sql.bind(SOURCE, message.getSource());
            sql.bind(SOURCE_DEVICE, message.getSourceDevice());
            sql.bind(CONTENT, message.hasContent() ? message.getContent().toByteArray() : null);
            sql.bind(SEQUENCE_ID, message.getSequenceId());
            sql.bind(NOTIFY_SEQUENCE_ID, message.getNotifySequenceId());
            sql.bind(SYSTEM_SHOW_TIMESTAMP, message.getSystemShowTimestamp());
          }
        };
      }
    }
  }

}
