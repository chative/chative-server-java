package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class PushConversationsTable {
  private static final String ID     = "id";
  private static final String DESTINATION        = "destination";
  private static final String DESTINATION_DEVICE = "destination_device";
  private static final String CONVERSATION       = "conversation";
  private static final String PUSH_TYPE       = "push_type";
  private static final String PRIORITY       = "priority";
  private static final String MAX_READ_NID       = "max_read_nid";
  private static final String LAST_SELF_NID       = "last_self_nid";
  private static final String IS_SEND       = "is_send";
  private static final String LAST_MSG_ID       = "last_msg_id";
  private static final String LAST_VISIBLE_MSG_ID       = "last_visible_msg_id";
  private static final String LAST_VISIBLE_MSG_SID     = "last_visible_msg_sid";
  private static final String LAST_VISIBLE_MSG_NID   = "last_visible_msg_nid";
  private static final String LAST_SHARD_MSG_ID       = "last_shard_msg_id";
  private static final String LAST_SHARD_MSG_ST       = "last_shard_msg_server_time";

  public static final int RESULT_SET_CHUNK_SIZE = 100;

  public enum PUSH_TYPE {
    ALL,
    LATEST;
    private static PUSH_TYPE[] allValues = values();

    public static PUSH_TYPE fromOrdinal(int n) {
      switch (n) {
        case 0: return ALL;
        case 1: return LATEST;
        default: return null;
      }
    }
  }

  public enum PRIORITY {
    HIGH,
    NORMAL,
    LOW;

    private static PRIORITY[] allValues = values();
    public static PRIORITY fromOrdinal(int n) {
      switch (n) {
        case 0: return HIGH;
        case 1: return NORMAL;
        case 2: return LOW;
        default: return null;
      }
    }
  }

  @SqlUpdate("INSERT INTO push_conversations (" + ID + ", " +  CONVERSATION + ", " + DESTINATION + ", "+ DESTINATION_DEVICE + ", "+ PUSH_TYPE+ ", "+ PRIORITY+ ", "+ MAX_READ_NID+ ", "+ LAST_SELF_NID+ ", "+ IS_SEND+  ", " + LAST_MSG_ID + ", "+ LAST_VISIBLE_MSG_ID + ", "+ LAST_VISIBLE_MSG_SID+ ", "+ LAST_VISIBLE_MSG_NID+ ", "+ LAST_SHARD_MSG_ID+ ", "+ LAST_SHARD_MSG_ST+
          ") VALUES ( :id , :conversation, :destination, :destination_device, :push_type, :priority, :max_read_nid, :last_self_nid, :is_send,:last_msg_id, :last_visible_msg_id, :last_visible_msg_sid, :last_visible_msg_nid, :last_shard_msg_id, :last_shard_msg_server_time)")
  public abstract void insert(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ MAX_READ_NID+ " = :max_read_nid, "+ LAST_SELF_NID+ " = :last_self_nid, "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_VISIBLE_MSG_ID+  " = :last_visible_msg_id, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid, "+ IS_SEND+  " = :is_send  where id=:id")
  public abstract void update(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ LAST_SELF_NID+ " = :last_self_nid, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid, "+ LAST_SHARD_MSG_ID+  " = :last_shard_msg_id, "+ LAST_SHARD_MSG_ST+  " = :last_shard_msg_server_time, "+ IS_SEND+  " = :is_send  where id=:id")
  public abstract void updateForSource(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ MAX_READ_NID+ " = :max_read_nid, "+ IS_SEND+  " = :is_send  where id=:id")
  public abstract void updateForRead(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_VISIBLE_MSG_ID+  " = :last_visible_msg_id, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid, "+ LAST_SHARD_MSG_ID+  " = :last_shard_msg_id, "+ LAST_SHARD_MSG_ST+  " = :last_shard_msg_server_time, "+ IS_SEND+  " = :is_send  where id=:id")
  public abstract void updateForOther(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_VISIBLE_MSG_ID+  " = :last_visible_msg_id, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid "+ " where id=:id")
  public abstract void updateForOtherSelf(@PushConversationBinder PushConversation pushConversation);

  @SqlUpdate("update push_conversations set "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_SHARD_MSG_ID+  " = :last_shard_msg_id, "+ LAST_SHARD_MSG_ST+  " = :last_shard_msg_server_time "+ " where id=:id")
  public abstract void updateForOtherForSilent(@PushConversationBinder PushConversation pushConversation);
  @SqlUpdate("update push_conversations set "+ IS_SEND+  " = :is_send  where id=:id")
  public abstract void updateForSend(@PushConversationBinder PushConversation pushConversation);

  @SqlBatch("INSERT INTO push_conversations (" + ID + ", " + CONVERSATION + ", " + DESTINATION + ", "+ DESTINATION_DEVICE + ", "+ PUSH_TYPE+ ", "+ PRIORITY+ ", "+ MAX_READ_NID+ ", "+ LAST_SELF_NID+ ", "+ IS_SEND+  ", " + LAST_MSG_ID + ", "+ LAST_VISIBLE_MSG_ID + ", "+ LAST_VISIBLE_MSG_SID+ ", "+ LAST_VISIBLE_MSG_NID+
          ") VALUES ( :id , :conversation, :destination, :destination_device, :push_type, :priority, :max_read_nid, :last_self_nid, :is_send,:last_msg_id, :last_visible_msg_id, :last_visible_msg_sid, :last_visible_msg_nid)")
  public abstract void insertBatch(@PushConversationBinder List<PushConversation> pushConversations);

  @SqlBatch("update push_conversations set "+ MAX_READ_NID+ " = :max_read_nid, "+ LAST_SELF_NID+ " = :last_self_nid, "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_VISIBLE_MSG_ID+  " = :last_visible_msg_id, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid, "+ IS_SEND+ "= :is_send  where id=:id" )
  public abstract void updateBatch(@PushConversationBinder List<PushConversation> pushConversations);

  @Mapper(PushConversationMapper.class)
  @SqlQuery("SELECT * FROM push_conversations WHERE destination=:destination and destination_device=:destination_device and is_send=true ORDER BY "  + LAST_VISIBLE_MSG_ID + " ASC LIMIT " + RESULT_SET_CHUNK_SIZE)
  public abstract List<PushConversation> getByDestination(@Bind(DESTINATION) String destination,@Bind(DESTINATION_DEVICE) long destination_device);

  @Mapper(PushConversationMapper.class)
  @SqlQuery("SELECT * FROM push_conversations WHERE destination=:destination and destination_device=:destination_device and conversation=:conversation ")
  public abstract PushConversation getByDestinationAndConversation(@Bind(DESTINATION) String destination,@Bind(DESTINATION_DEVICE) long destination_device,@Bind(CONVERSATION) String conversation);

  @Mapper(PushConversationMapper.class)
  @SqlQuery("SELECT * FROM push_conversations WHERE destination=:destination and destination_device=:destination_device and priority:=priority")
  public abstract List<PushConversation> getByDestinationAndPriority(@Bind(DESTINATION) String destination,@Bind(DESTINATION_DEVICE) long destination_device,@Bind(PRIORITY) int priority);

  @Mapper(PushConversationMapper.class)
  @SqlQuery("SELECT * FROM push_conversations WHERE id=:id")
  public abstract List<PushConversation> getById(@Bind(ID) String id);

  @SqlUpdate("DELETE FROM push_conversations where id=:id")
  public abstract void delete( @Bind(ID) String id);

  public static class PushConversationMapper implements ResultSetMapper<PushConversation> {
    @Override
    public PushConversation map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new PushConversation(
              resultSet.getString(ID),
              resultSet.getString(CONVERSATION),
              resultSet.getString(DESTINATION),
              resultSet.getLong(DESTINATION_DEVICE),
              resultSet.getInt(PUSH_TYPE),
              resultSet.getInt(PRIORITY),
              resultSet.getLong(MAX_READ_NID),
              resultSet.getLong(LAST_SELF_NID),
              resultSet.getBoolean(IS_SEND),
              resultSet.getLong(LAST_MSG_ID),
              resultSet.getLong(LAST_VISIBLE_MSG_ID),
              resultSet.getLong(LAST_VISIBLE_MSG_SID),
              resultSet.getLong(LAST_VISIBLE_MSG_NID),
              resultSet.getLong(LAST_SHARD_MSG_ID),
              resultSet.getLong(LAST_SHARD_MSG_ST)
      );
    }
  }

  @BindingAnnotation(PushConversationBinder.PushConversationFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface PushConversationBinder {
    public static class PushConversationFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<PushConversationBinder, PushConversation>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           PushConversationBinder pushConversationBinder,
                           PushConversation pushConversation)
          {
            sql.bind(ID, pushConversation.getId());
            sql.bind(CONVERSATION, pushConversation.getConversation());
            sql.bind(DESTINATION, pushConversation.getDestination());
            sql.bind(DESTINATION_DEVICE,pushConversation.getDestinationDevice());
            sql.bind(PUSH_TYPE,pushConversation.getPushType());
            sql.bind(PRIORITY,pushConversation.getPriority());
            sql.bind(MAX_READ_NID,pushConversation.getMaxReadNotifySequenceId());
            sql.bind(LAST_SELF_NID,pushConversation.getLastSelfNotifySequenceId());
            sql.bind(IS_SEND,pushConversation.isSend());
            sql.bind(LAST_MSG_ID,pushConversation.getLastMsgId());
            sql.bind(LAST_VISIBLE_MSG_ID,pushConversation.getLastVisibleMsgId());
            sql.bind(LAST_VISIBLE_MSG_SID,pushConversation.getLastVisibleMsgSequenceId());
            sql.bind(LAST_VISIBLE_MSG_NID,pushConversation.getLastVisibleMsgNotifySequenceId());
            sql.bind(LAST_SHARD_MSG_ID,pushConversation.getLastShardMsgId());
            sql.bind(LAST_SHARD_MSG_ST,pushConversation.getLastShardMsgServerTime());
          }
        };
      }
    }
  }
}
