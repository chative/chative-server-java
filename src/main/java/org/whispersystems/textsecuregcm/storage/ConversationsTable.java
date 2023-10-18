package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;
import org.whispersystems.textsecuregcm.entities.Notification;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class ConversationsTable {
    public enum STATUS {
        CLOSE(0),
        OPEN(1);
        private int code;
        STATUS(int code){
            this.code=code;
        }
        public int getCode(){
            return code;
        }

        public static STATUS fromCode(int n) {
            for(STATUS status: STATUS.values()){
                if(status.getCode()==n){
                    return status;
                }
            }
            return null;
        }
    }

    @SqlUpdate("INSERT INTO conversations (number,conversation,remark,last_update_time,mute_status,mute_begin_time,mute_end_time,block_status,version )" +
            " VALUES (:number,:conversation,:remark,:last_update_time,:mute_status,:mute_begin_time,:mute_end_time,:block_status,:version) ")
    @GetGeneratedKeys(columnName = "id")
    public abstract long insert(@ConversationBinder Conversation conversation);

    @SqlQuery("update conversations set last_update_time=:last_update_time ,remark=:remark,mute_status=:mute_status,mute_begin_time=:mute_begin_time,mute_end_time=:mute_end_time,block_status=:block_status,version=version+1" +
            " where id=:id returning version")
    public abstract int update(@ConversationBinder Conversation conversation);

    @Mapper(ConversationsMapper.class)
    @SqlQuery("select * from  conversations  where number=:number ")
    public abstract List<Conversation> queryByNumber(@Bind("number") String number);

    @Mapper(ConversationsMapper.class)
    @SqlQuery("select * from  conversations  where number=:number and conversation in (<conversations>) ")
    public abstract List<Conversation> queryByConversations(@Bind("number") String number,@BindIn("conversations") List<String> conversations);

    @BindingAnnotation(ConversationsTable.ConversationBinder.AccountBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface ConversationBinder {
        public static class AccountBinderFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<ConversationsTable.ConversationBinder ,Conversation>() {
                    @Override
                    public void bind(SQLStatement<?> sql,
                                     ConversationsTable.ConversationBinder conversationBinder,
                                     Conversation conversation)
                    {
                        sql.bind("number", conversation.getNumber());
                        sql.bind("conversation",conversation.getConversation());
                        sql.bind("remark",conversation.getRemark());
                        sql.bind("last_update_time",conversation.getLastUpdateTime());
                        sql.bind("mute_status", conversation.getMuteStatus());
                        sql.bind("mute_begin_time", conversation.getMuteBeginTime());
                        sql.bind("mute_end_time",conversation.getMuteEndTime());
                        sql.bind("block_status", conversation.getBlockStatus());
                        sql.bind("version", conversation.getVersion());
                        sql.bind("id", conversation.getId());
                    }
                };
            }
        }
    }

    public static class ConversationsMapper implements ResultSetMapper<Conversation> {
        @Override
        public Conversation map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new Conversation(
                    resultSet.getLong("id"),
                    resultSet.getString("number"),
                    resultSet.getString("conversation"),
                    resultSet.getString("remark"),
                    resultSet.getLong("last_update_time"),
                    resultSet.getInt("mute_status"),
                    resultSet.getLong("mute_begin_time"),
                    resultSet.getLong("mute_end_time"),
                    resultSet.getInt("block_status"),
                    resultSet.getInt("confidential_mode"),
                    resultSet.getInt("version"));
        }
    }
}
