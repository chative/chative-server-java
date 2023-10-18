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

public abstract class ConversationMsgsTable {
  public static final int RESULT_SET_CHUNK_SIZE = 100;

  private static final String ID     = "id";
  private static final String CONVERSATION       = "conversation";
  private static final String LAST_MSG_ID       = "last_msg_id";
  private static final String LAST_VISIBLE_MSG_ID       = "last_visible_msg_id";
  private static final String LAST_VISIBLE_MSG_SID     = "last_visible_msg_sid";
  private static final String LAST_VISIBLE_MSG_NID   = "last_visible_msg_nid";
  private static final String PUSH_TYPE       = "push_type";
  private static final String PRIORITY       = "priority";
  private static final String LAST_MSG_ST       = "last_msg_server_time";
  private static final String LAST_VISIBLE_MSG_ST       = "last_visible_msg_server_time";
  private static final String LAST_MSG_SID       = "last_msg_sid";


  @SqlUpdate("INSERT INTO conversation_msgs ("  +  CONVERSATION + ", " + LAST_MSG_ID + ", "+ LAST_VISIBLE_MSG_ID + ", "+ LAST_VISIBLE_MSG_SID+ ", "+ LAST_VISIBLE_MSG_NID+ ", "+ LAST_MSG_ST+ ", "+ LAST_VISIBLE_MSG_ST+ ", "+ PUSH_TYPE+ ", "+ PRIORITY+ ", "+ LAST_MSG_SID+
          ") VALUES ( :conversation, :last_msg_id, :last_visible_msg_id, :last_visible_msg_sid, :last_visible_msg_nid, :last_msg_server_time, :last_visible_msg_server_time, :push_type, :priority, :last_msg_sid)")
  @GetGeneratedKeys(columnName="id")
  public abstract long insert(@ConversationMsgBinder ConversationMsg conversationMsg);

  @SqlUpdate("update conversation_msgs set "+ LAST_MSG_ID+  " = :last_msg_id, "+ LAST_VISIBLE_MSG_ID+  " = :last_visible_msg_id, "+ LAST_VISIBLE_MSG_SID+  " = :last_visible_msg_sid, "+ LAST_VISIBLE_MSG_NID+  " = :last_visible_msg_nid, "
          + LAST_MSG_ST+  " = :last_msg_server_time, "+ LAST_VISIBLE_MSG_ST+  " = :last_visible_msg_server_time, "+ PUSH_TYPE+  " = :push_type, "+ PRIORITY+  " = :priority,"+ LAST_MSG_SID+  " = :last_msg_sid"+"  where conversation=:conversation")
  public abstract void update(@ConversationMsgBinder ConversationMsg conversationMsg);


  @Mapper(ConversationMsgMapper.class)
  @SqlQuery("SELECT * FROM conversation_msgs WHERE conversation=:conversation")
  public abstract ConversationMsg getByConversation(@Bind(CONVERSATION) String conversation);


  @Mapper(ConversationMsgMapper.class)
//  @SqlQuery("SELECT *\n" +
//          "FROM CONVERSATION_MSGS\n" +
//          "WHERE (CONVERSATION like CONCAT('%',:number,'%'))\n" +
//          "\tOR CONVERSATION in\n" +
//          "\t\t(SELECT GID\n" +
//          "\t\t\tFROM (select * from dblink(:link,'select gid,uid,create_time from public.group_members') as t( gid text,uid text,create_time bigint)) a\n" +
//          "\t\t\tWHERE UID = :number\n" +
//          "\t\t\t\tAND CREATE_TIME <= LAST_MSG_SERVER_TIME)\n" +
//          "\tAND LAST_MSG_SERVER_TIME >= :startTime \n" +
//          "ORDER BY LAST_MSG_SERVER_TIME DESC  ")

  @SqlQuery("select * from (\n" +
          "\tselect c.*\n" +
          "\tfrom conversation_msgs c,(select gid,create_time\n" +
          "\t\t\t\tfrom (select * from dblink(:link,'select gid,uid,create_time from public.group_members') as t( gid text,uid text,create_time bigint)) a\n" +
          "\t\t\t\twhere uid = :number) g\n" +
          "\twhere c.conversation =g.gid and g.create_time <= c.last_msg_server_time\n" +
          "\t\tand c.last_msg_server_time >= :startTime\n" +
          "\tunion all \n" +
          "\tselect * from conversation_msgs where  conversation like  concat('%',:number,'%') \n" +
          "\tand last_msg_server_time >= :startTime \n" +
          ") a\n" +
          "order by a.last_msg_server_time desc")
  public abstract List<ConversationMsg> getByNumberForHot(@Bind("number") String number, @Bind("startTime") long startTime, @Bind("link") String link);

  public static class ConversationMsgMapper implements ResultSetMapper<ConversationMsg> {
    @Override
    public ConversationMsg map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {

        return new ConversationMsg(
                resultSet.getLong(ID),
                resultSet.getString(CONVERSATION),
                resultSet.getLong(LAST_MSG_ID),
                resultSet.getLong(LAST_VISIBLE_MSG_ID),
                resultSet.getLong(LAST_VISIBLE_MSG_SID),
                resultSet.getLong(LAST_VISIBLE_MSG_NID),
                resultSet.getLong(LAST_MSG_ST),
                resultSet.getLong(LAST_VISIBLE_MSG_ST),
                resultSet.getInt(PUSH_TYPE),
                resultSet.getInt(PRIORITY),
                resultSet.getLong(LAST_MSG_SID)

      );
    }
  }

  @BindingAnnotation(ConversationMsgBinder.ConversationMsgFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface ConversationMsgBinder {
    public static class ConversationMsgFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<ConversationMsgBinder, ConversationMsg>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           ConversationMsgBinder conversationMsgBinder,
                           ConversationMsg conversationMsg)
          {
            sql.bind(ID, conversationMsg.getId());
            sql.bind(CONVERSATION, conversationMsg.getConversation());
            sql.bind(LAST_MSG_ID,conversationMsg.getLastMsgId());
            sql.bind(LAST_VISIBLE_MSG_ID,conversationMsg.getLastVisibleMsgId());
            sql.bind(LAST_VISIBLE_MSG_SID,conversationMsg.getLastVisibleMsgSequenceId());
            sql.bind(LAST_VISIBLE_MSG_NID,conversationMsg.getLastVisibleMsgNotifySequenceId());
            sql.bind(LAST_MSG_ST,conversationMsg.getLastMsgServerTime());
            sql.bind(LAST_VISIBLE_MSG_ST,conversationMsg.getLastVisibleMsgServerTime());
            sql.bind(PUSH_TYPE,conversationMsg.getPushType());
            sql.bind(PRIORITY,conversationMsg.getPriority());
            sql.bind(LAST_MSG_SID,conversationMsg.getLastMsgSequenceId());
          }
        };
      }
    }
  }
}
