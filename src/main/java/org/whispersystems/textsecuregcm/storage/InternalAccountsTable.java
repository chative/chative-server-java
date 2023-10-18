package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class InternalAccountsTable {

    private static final String TABLE_NAME = "internal_accounts";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String PUSH_TYPE = "push_type";
    private static final String PUSH_TOKEN = "push_token";
    private static final String VCODE = "vcode";
    private static final String REGISTERED = "registered";
    private static final String INVITATION_PER_DAY = "invitation_per_day";
    private static final String DISABLED = "disabled";
    private static final String OKTA_ID = "okta_id";
    private static final String TEAM_ID = "team_id";
    private static final String TEAM_NAME = "team_name";
    private static final String PID= "pid";
    private static final String PUID = "puid";
    private static final String OKTA_ORG = "okta_org";
    private static final String INACTIVE = "inactive";
    private static final String EXT_ID = "ext_id";
    private static final String INVITE_RULE = "invite_rule";
    private static final String SUPPORT_TRANSFER = "support_transfer";

    private static final String INVITE_CODE = "invite_code";
    private static final String DELETED = "deleted";

    private static final String JOINED_AT = "joined_at";

    @SqlUpdate("INSERT INTO " + TABLE_NAME + " (" + NUMBER + ", " + NAME + ", " + PUSH_TYPE + ", " +
            PUSH_TOKEN + ", " + VCODE + ", " + REGISTERED + ", " + INVITATION_PER_DAY  + ", " + DISABLED + ", " + OKTA_ID + "," + OKTA_ORG+ "," + EXT_ID + "," + JOINED_AT + ") " +
            "VALUES (:" + NUMBER + ", :" + NAME + ", :" + PUSH_TYPE + ", :" + PUSH_TOKEN + ", :" +
            VCODE + ", :" + REGISTERED + ", :" + INVITATION_PER_DAY + ", :" + DISABLED + ", :" + OKTA_ID + ", :" + OKTA_ORG+ ", :" + EXT_ID + ", extract(epoch from now()) * 1000)")
    public abstract void insert(@Bind(NUMBER) String number,
                                @Bind(NAME) String name,
                                @Bind(PUSH_TYPE) String push_type,
                                @Bind(PUSH_TOKEN) String push_token,
                                @Bind(VCODE) int vcode,
                                @Bind(REGISTERED) boolean registered,
                                @Bind(INVITATION_PER_DAY) int invitation_per_day,
                                @Bind(DISABLED) boolean disabled,
                                @Bind(OKTA_ID) String okta_id,
                                @Bind(OKTA_ORG) String okta_org,
                                @Bind(EXT_ID) long extId
    );
    @SqlUpdate("INSERT INTO " + TABLE_NAME + " (" + NUMBER + ", " + NAME + ", " + PUSH_TYPE + ", " +
            PUSH_TOKEN + ", " + VCODE + ", " + REGISTERED + ", " + INVITATION_PER_DAY  + ", " + DISABLED +  ", " + OKTA_ID + ", " + PID + ", " + PUID + "," + OKTA_ORG+ "," + EXT_ID + ") " +
            "VALUES (:" + NUMBER + ", :" + NAME + ", :" + PUSH_TYPE + ", :" + PUSH_TOKEN + ", :" +
            VCODE + ", :" + REGISTERED + ", :" + INVITATION_PER_DAY + ", :" + DISABLED + ", :" + OKTA_ID + ", :" + PID + ", :" + PUID + ", :" + OKTA_ORG+ ", :" + EXT_ID+ ")")
    public abstract void insertForOpenApi(@Bind(NUMBER) String number,
                                @Bind(NAME) String name,
                                @Bind(PUSH_TYPE) String push_type,
                                @Bind(PUSH_TOKEN) String push_token,
                                @Bind(VCODE) int vcode,
                                @Bind(REGISTERED) boolean registered,
                                @Bind(INVITATION_PER_DAY) int invitation_per_day,
                                @Bind(DISABLED) boolean disabled,
                                @Bind(OKTA_ID) String okta_id,
                                @Bind(PID) String pid,
                                @Bind(PUID) String puid,
                                @Bind(OKTA_ORG) String okta_org,
                                @Bind(EXT_ID) long extId
    );

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + INVITE_CODE + " = :" + INVITE_CODE)
    public abstract InternalAccountsRow getByInviteCode(@Bind(INVITE_CODE) String code);

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + NUMBER + " = :" + NUMBER)
    public abstract InternalAccountsRow get(@Bind(NUMBER) String number);

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PUID + " = :" + PUID +" AND "+PID +" = :"+PID)
    public abstract InternalAccountsRow getByPuid(@Bind(PID) String pid,@Bind(PUID) String puid);

    //@Mapper(InternalAccountMapper.class)
    //@SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE LOWER(" + EMAIL + ") = LOWER(:" + EMAIL + ")")
    //public abstract List<InternalAccountsRow> getByEmail(@Bind(EMAIL) String email);

    //@Mapper(InternalAccountMapper.class)
    //@SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE LOWER(" + PHONE + ") = LOWER(:" + PHONE + ")")
    //public abstract List<InternalAccountsRow> getByPhone(@Bind(PHONE) String phone);

    //@Mapper(InternalAccountMapper.class)
    //@SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + OKTA_ORG + " = :" + OKTA_ORG+" and  LOWER(" + EMAIL + ") = LOWER(:" + EMAIL + ")")
    //public abstract InternalAccountsRow getByEmail(@Bind(OKTA_ORG) String okta_org,@Bind(EMAIL) String email);

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + OKTA_ORG + " = :" + OKTA_ORG+" and "+ OKTA_ID + " = :" + OKTA_ID)
    public abstract InternalAccountsRow getByOktaId(@Bind(OKTA_ORG) String okta_org,@Bind(OKTA_ID) String okta_id);

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + NAME + "=:" + NAME + ", " +
            PUSH_TYPE + "=:" + PUSH_TYPE + ", " + PUSH_TOKEN + "=:" + PUSH_TOKEN + ", " + VCODE + "=:" + VCODE + ", " +
            REGISTERED + "=:" + REGISTERED + ", " + INVITATION_PER_DAY + "=:" + INVITATION_PER_DAY + ", " +
            DISABLED + "=:" + DISABLED +  ", " + OKTA_ID + "=:" + OKTA_ID + ", " + OKTA_ORG + "=:" + OKTA_ORG + ", " + INACTIVE + "=:" + INACTIVE +", " + EXT_ID + "=:" + EXT_ID+
            ", " + SUPPORT_TRANSFER + "=:" + SUPPORT_TRANSFER+
        " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void update(
            @Bind(NUMBER) String number,
            @Bind(NAME) String name,
            @Bind(PUSH_TYPE) String push_type,
            @Bind(PUSH_TOKEN) String push_token,
            @Bind(VCODE) int vcode,
            @Bind(REGISTERED) boolean registered,
            @Bind(INVITATION_PER_DAY) int invitaiton_per_day,
            @Bind(DISABLED) boolean disabled,
            @Bind(OKTA_ID) String okta_id,
            @Bind(OKTA_ORG) String okta_org,
            @Bind(INACTIVE) boolean inactive,
            @Bind(EXT_ID) long extId,
            @Bind(SUPPORT_TRANSFER) boolean supportTransfer
    );

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME + " where  disabled <> true and registered = true and inactive <> true and (deleted is null or deleted <> true) ")
    public abstract List<InternalAccountsRow> getAllActive();

    @Mapper(InternalAccountMapper.class)
    @SqlQuery("SELECT * FROM " + TABLE_NAME)
    public abstract List<InternalAccountsRow> getAll();

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + VCODE + "=:" + VCODE + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setVcode(
            @Bind(NUMBER) String number,
            @Bind(VCODE) int vcode
    );

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + REGISTERED + "=:" + REGISTERED + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setRegistered(
            @Bind(NUMBER) String number,
            @Bind(REGISTERED) boolean registered
    );

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + NAME + "=:" + NAME + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setName(
            @Bind(NUMBER) String number,
            @Bind(NAME) String name
    );

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + PUSH_TYPE + "=:" + PUSH_TYPE + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setPushType(
            @Bind(NUMBER) String number,
            @Bind(PUSH_TYPE) String push_type
    );

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + PUSH_TOKEN + "=:" + PUSH_TOKEN + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setPushToken(
            @Bind(NUMBER) String number,
            @Bind(PUSH_TOKEN) String push_token
    );

    @SqlUpdate("UPDATE " + TABLE_NAME + " SET " + DISABLED + "=:" + DISABLED + " WHERE " + NUMBER + "=:" + NUMBER)
    public abstract void setDisabled(
            @Bind(NUMBER) String number,
            @Bind(DISABLED) boolean disabled
    );

    // 查询有name，email，number合并到一个字段，用name代替三个字段
    @Mapper(InternalAccountsTeamMapper.class)
    @SqlQuery("SELECT iacc.*,  string_agg(CAST(tm.team_id AS TEXT), ',') as team_id, string_agg(tm.team_name, ',') as team_name FROM internal_accounts iacc LEFT JOIN teams_members tm ON iacc.number = tm.account_number WHERE (COALESCE (iacc.email,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.email, '')),'%') OR COALESCE (iacc.name,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.name, '')),'%') OR COALESCE (iacc.number,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.number, '')),'%')) AND (:teamsId IS NULL OR :teamsId = 0 OR tm.team_id = :teamsId) AND iacc.disabled = ANY (:list) GROUP BY iacc.number OFFSET :offset LIMIT :limit")
    public abstract List<InternalAccountsTeamRow> getAccountsList(@Bind("offset") int offset, @Bind("limit") int length, @Bind("email") String email, @Bind("name") String name,
                                                              @Bind("number") String number, @Bind("teamsId") long teamsId, @BindBooleanList("list") List<Boolean> list);

    @SqlQuery("SELECT COUNT(DISTINCT iacc.number) FROM internal_accounts iacc LEFT JOIN teams_members tm ON iacc.number = tm.account_number WHERE (COALESCE (iacc.email,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.email, '')),'%') OR COALESCE (iacc.name,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.name, '')),'%') OR COALESCE (iacc.number,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.number, '')),'%')) AND (:teamsId IS NULL OR :teamsId = 0 OR tm.team_id = :teamsId) AND iacc.disabled = ANY (:list)")
    public abstract long getAllCount(@Bind("email") String email, @Bind("name") String name, @Bind("number") String number, @Bind("teamsId") long teamsId, @BindBooleanList("list") List<Boolean> list);


    // 解决多team查询临时用
    @Mapper(InternalAccountsTeamMapper.class)
    @SqlQuery("SELECT iacc.*,  string_agg(CAST(tm.team_id AS TEXT), ',') as team_id, string_agg(tm.team_name, ',') as team_name FROM internal_accounts iacc LEFT JOIN teams_members tm ON iacc.number = tm.account_number WHERE (COALESCE (iacc.email,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.email, '')),'%') OR COALESCE (iacc.name,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.name, '')),'%') OR COALESCE (iacc.number,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.number, '')),'%')) AND tm.team_id = ANY (:teamsId) AND iacc.disabled = ANY (:list) GROUP BY iacc.number OFFSET :offset LIMIT :limit")
    public abstract List<InternalAccountsTeamRow> getAccountsListByTeam(@Bind("offset") int offset, @Bind("limit") int length, @Bind("email") String email, @Bind("name") String name,
                                                                  @Bind("number") String number, @BindLongList("teamsId") List<String> teamsId, @BindBooleanList("list") List<Boolean> list);

    @SqlQuery("SELECT COUNT(DISTINCT iacc.number) FROM internal_accounts iacc LEFT JOIN teams_members tm ON iacc.number = tm.account_number WHERE (COALESCE (iacc.email,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.email, '')),'%') OR COALESCE (iacc.name,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.name, '')),'%') OR COALESCE (iacc.number,'') ilike CONCAT('%',COALESCE(:name, COALESCE(iacc.number, '')),'%')) AND tm.team_id = ANY(:teamsId) AND iacc.disabled = ANY (:list)")
    public abstract long getAllCountByTeam(@Bind("email") String email, @Bind("name") String name, @Bind("number") String number, @BindLongList("teamsId") List<String> teamsId, @BindBooleanList("list") List<Boolean> list);

    @Transaction
    public boolean create(Optional<String> number, String name, String push_type, String push_token, int vcode, boolean registered, int invitation_per_day, boolean disabled, String email, String okta_id,String okta_org,long extId) {
        if (null != get(number.get())) {
            return false;
        }

        insert(number.get(), name, push_type, push_token, vcode, registered, invitation_per_day, disabled,  okta_id,okta_org,extId);

        return true;
    }

    public static class InternalAccountMapper implements ResultSetMapper<InternalAccountsRow> {
        @Override
        public InternalAccountsRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new InternalAccountsRow(
                    resultSet.getString(NUMBER),
                    resultSet.getString(NAME),
                    resultSet.getString(PUSH_TYPE),
                    resultSet.getString(PUSH_TOKEN),
                    resultSet.getInt(VCODE),
                    resultSet.getBoolean(REGISTERED),
                    resultSet.getInt(INVITATION_PER_DAY),
                    resultSet.getBoolean(DISABLED),
                    resultSet.getString(OKTA_ID),
                    resultSet.getString(PID),
                    resultSet.getString(PUID),
                    resultSet.getString(OKTA_ORG),
                    resultSet.getBoolean(INACTIVE),
                    resultSet.getLong(EXT_ID),
                    resultSet.getInt(INVITE_RULE),
                    resultSet.getBoolean(SUPPORT_TRANSFER),
                    resultSet.getBoolean(DELETED),
                    resultSet.getLong(JOINED_AT)
            );
        }
    }

    public static class InternalAccountsTeamMapper implements ResultSetMapper<InternalAccountsTeamRow> {
        @Override
        public InternalAccountsTeamRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new InternalAccountsTeamRow(
                    resultSet.getString(NUMBER),
                    resultSet.getString(NAME),
                    resultSet.getString(PUSH_TYPE),
                    resultSet.getString(PUSH_TOKEN),
                    resultSet.getInt(VCODE),
                    resultSet.getBoolean(REGISTERED),
                    resultSet.getInt(INVITATION_PER_DAY),
                    resultSet.getBoolean(DISABLED),
                    resultSet.getString(OKTA_ID),
                    resultSet.getString(TEAM_ID),
                    resultSet.getString(TEAM_NAME),
                    resultSet.getString(PID),
                    resultSet.getString(PUID),
                    resultSet.getString(OKTA_ORG),
                    resultSet.getBoolean(INACTIVE),
                    resultSet.getLong(EXT_ID),
                    resultSet.getBoolean(SUPPORT_TRANSFER),
                    resultSet.getBoolean(DELETED),
                    resultSet.getLong(JOINED_AT)
            );
        }
    }

    @BindingAnnotation(BindBooleanList.BindFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindBooleanList {
        String value() default "it";

        class BindFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<BindBooleanList, Collection<Boolean>>() {
                    @Override
                    public void bind(SQLStatement<?> q, BindBooleanList bind, Collection<Boolean> arg) {
                        try {
                            Array array = q.getContext().getConnection().createArrayOf("boolean", arg.toArray());
                            q.bindBySqlType(bind.value(), array, Types.ARRAY);
                        } catch (SQLException e) {
                            // handle error
                        }
                    }
                };
            }
        }
    }

    @BindingAnnotation(BindLongList.BindFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindLongList {
        String value() default "it";

        class BindFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<BindLongList, Collection<Long>>() {
                    @Override
                    public void bind(SQLStatement<?> q, BindLongList bind, Collection<Long> arg) {
                        try {
                            Array array = q.getContext().getConnection().createArrayOf("long", arg.toArray());
                            q.bindBySqlType(bind.value(), array, Types.ARRAY);
                        } catch (SQLException e) {
                            // handle error
                        }
                    }
                };
            }
        }
    }

    // @BindingAnnotation(InternalAccountBinder.InternalAccountBinderFactory.class)
    // @Retention(RetentionPolicy.RUNTIME)
    // @Target({ElementType.PARAMETER})
    // public @interface InternalAccountBinder {
    //     public static class InternalAccountBinderFactory implements BinderFactory {
    //         @Override
    //         public Binder build(Annotation annotation) {
    //             return new Binder<Messages.MessageBinder, MessageProtos.Envelope>() {
    //                 @Override
    //                 public void bind(SQLStatement<?> sql,
    //                                  Messages.MessageBinder accountBinder,
    //                                  MessageProtos.Envelope message)
    //                 {
    //                     sql.bind(, message.getType().getNumber());
    //                     sql.bind(RELAY, message.getRelay());
    //                     sql.bind(TIMESTAMP, message.getTimestamp());
    //                     sql.bind(SOURCE, message.getSource());
    //                     sql.bind(SOURCE_DEVICE, message.getSourceDevice());
    //                     sql.bind(MESSAGE, message.hasLegacyMessage() ? message.getLegacyMessage().toByteArray() : null);
    //                     sql.bind(CONTENT, message.hasContent() ? message.getContent().toByteArray() : null);
    //                 }
    //             };
    //         }
    //     }
    // }
}
