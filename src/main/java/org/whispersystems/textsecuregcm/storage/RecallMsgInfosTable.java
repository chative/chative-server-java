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

public abstract class RecallMsgInfosTable {
  private static final String ID     = "id";
  private static final String CONVERSATION       = "conversation";
  private static final String MSG_SID       = "msg_sequence_id";
  private static final String MSG_NID       = "msg_notify_sequence_id";
  private static final String SOURCE_SID     = "source_sequence_id";
  private static final String SOURCE_NID   = "source_notify_sequence_id";



  @SqlUpdate("INSERT INTO recall_msg_infos (" +  CONVERSATION + ", " + MSG_SID + ", "+ MSG_NID + ", "+ SOURCE_SID+ ", "+ SOURCE_NID+
          ") VALUES (:conversation, :msg_sequence_id, :msg_notify_sequence_id, :source_sequence_id, :source_notify_sequence_id)")
  public abstract long insert(@RecallMsgInfoBinder RecallMsgInfo recallMsgInfo);

  @SqlQuery("SELECT AA.A - BB.B\n" +
          "FROM\n" +
          "\t(SELECT COUNT(1) * 2 AS A\n" +
          "\t\tFROM RECALL_MSG_INFOS\n" +
          "\t\tWHERE MSG_NOTIFY_SEQUENCE_ID > :minNid\n" +
          "\t\t\tAND MSG_NOTIFY_SEQUENCE_ID <= :maxNid\n" +
          "\t\t\tAND CONVERSATION = :conversation ) AA,\n" +
          "\t(SELECT COUNT(1) AS B\n" +
          "\t\tFROM RECALL_MSG_INFOS\n" +
          "\t\tWHERE SOURCE_SEQUENCE_ID < :minNid\n" +
          "\t\t\tAND MSG_NOTIFY_SEQUENCE_ID > :minNid\n" +
          "\t\t\tAND MSG_NOTIFY_SEQUENCE_ID <= :maxNid\n" +
          "\t\t\tAND CONVERSATION = :conversation) BB")
  public abstract int getUnreadCorrection(@Bind(CONVERSATION) String conversation,@Bind("minNid") long minNid,@Bind("maxNid") long maxNid);



  public static class RecallMsgInfoMapper implements ResultSetMapper<RecallMsgInfo> {
    @Override
    public RecallMsgInfo map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new RecallMsgInfo(
              resultSet.getLong(ID),
              resultSet.getString(CONVERSATION),
              resultSet.getLong(MSG_SID),
              resultSet.getLong(MSG_NID),
              resultSet.getLong(SOURCE_SID),
              resultSet.getLong(SOURCE_NID)
      );
    }
  }

  @BindingAnnotation(RecallMsgInfoBinder.RecallMsgInfoFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface RecallMsgInfoBinder {
    public static class RecallMsgInfoFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<RecallMsgInfoBinder, RecallMsgInfo>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           RecallMsgInfoBinder recallMsgInfoBinder,
                           RecallMsgInfo recallMsgInfo)
          {
            sql.bind(CONVERSATION, recallMsgInfo.getConversation());
            sql.bind(MSG_SID,recallMsgInfo.getMsgSequenceId());
            sql.bind(MSG_NID,recallMsgInfo.getMsgNotifySequenceId());
            sql.bind(SOURCE_SID,recallMsgInfo.getSourceSequenceId());
            sql.bind(SOURCE_NID,recallMsgInfo.getSourceNotifySequenceId());
          }
        };
      }
    }
  }
}
