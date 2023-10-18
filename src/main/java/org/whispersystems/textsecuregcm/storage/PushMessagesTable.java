package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@UseStringTemplate3StatementLocator
public abstract class PushMessagesTable {
  private static final String ID     = "id";
  private static final String PUSH_CONVERSATION_ID = "push_conversation_id";
  private static final String MSG_ID        = "msg_id";
  private static final String SEQUENCE_ID       = "sequence_id";
  private static final String PUSH_TYPE       = "push_type";
  private static final String PRIORITY       = "priority";

  @SqlUpdate("INSERT INTO push_messages (" + PUSH_CONVERSATION_ID + ", " + MSG_ID + ", "+ PUSH_TYPE + ", "+ PRIORITY+ ", "+ SEQUENCE_ID+ ") VALUES (:push_conversation_id, :msg_id, :push_type, :priority ,:sequence_id)")
  @GetGeneratedKeys(columnName="id")
  public abstract long insert(@PushMessageBinder PushMessage pushMessage);

  @SqlBatch("INSERT INTO push_messages (" + PUSH_CONVERSATION_ID + ", " + MSG_ID + ", "+ PUSH_TYPE + ", "+ PRIORITY+ ", "+ SEQUENCE_ID+ ") VALUES (:push_conversation_id, :msg_id, :push_type, :priority,:sequence_id)")
  public abstract void insertBatch(@PushMessageBinder List<PushMessage> pushMessage);


  @Mapper(PushMessageMapper.class)
  @SqlQuery("SELECT * FROM push_messages WHERE push_conversation_id=:push_conversation_id order by sequence_id limit 200")
  public abstract List<PushMessage> getByConversation(@Bind(PUSH_CONVERSATION_ID) String pushConversationId);

  @SqlQuery("SELECT count(1) FROM push_messages WHERE push_conversation_id=:push_conversation_id ")
  public abstract long getCountByConversation(@Bind(PUSH_CONVERSATION_ID) String pushConversationId);

  @SqlQuery("SELECT count(1) FROM push_messages WHERE push_conversation_id=:push_conversation_id and msg_id not in  (<msgIds>)")
  public abstract long getCountByConversationForLoad(@Bind(PUSH_CONVERSATION_ID) String pushConversationId,@BindIn("msgIds") List<Long> msgIds);

  @SqlUpdate("DELETE FROM push_messages where id=:id")
  public abstract void delete( @Bind(ID) long id);

  @SqlUpdate("DELETE FROM push_messages where push_conversation_id=:push_conversation_id and msg_id=:msg_id")
  public abstract void deleteByPushConversationIdAndMsgId( @Bind(PUSH_CONVERSATION_ID) String pushConversationId,@Bind(MSG_ID) long msgId);

  @SqlUpdate("DELETE FROM push_messages where push_conversation_id=:push_conversation_id and msg_id in  (<msgIds>)")
  public abstract void deleteByPushConversationIdAndMsgIds( @Bind(PUSH_CONVERSATION_ID) String pushConversationId,@BindIn("msgIds") List<Long> msgIds);


  public static class PushMessageMapper implements ResultSetMapper<PushMessage> {
    @Override
    public PushMessage map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new PushMessage(
              resultSet.getLong(ID),
              resultSet.getString(PUSH_CONVERSATION_ID),
              resultSet.getLong(MSG_ID),
              resultSet.getInt(PUSH_TYPE),
              resultSet.getInt(PRIORITY),
              resultSet.getLong(SEQUENCE_ID)
      );
    }
  }

  @BindingAnnotation(PushMessageBinder.PushConversationFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface PushMessageBinder {
    public static class PushConversationFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<PushMessageBinder, PushMessage>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           PushMessageBinder pushMessageBinder,
                           PushMessage pushMessage)
          {
            sql.bind(PUSH_CONVERSATION_ID, pushMessage.getPushConversationId());
            sql.bind(MSG_ID, pushMessage.getMsgId());
            sql.bind(PUSH_TYPE,pushMessage.getPushType());
            sql.bind(PRIORITY,pushMessage.getPriority());
            sql.bind(SEQUENCE_ID,pushMessage.getSequenceId());
          }
        };
      }
    }
  }
}
