package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.whispersystems.textsecuregcm.internal.LogSqlFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@LogSqlFactory
public abstract class InternalAccountsInvitationTable {

    private static final String CODE = "code";
    private static final String INVITER = "inviter";
    private static final String TIMESTAMP = "timestamp";
    private static final String REGISTER_TIME = "register_time";
    private static final String ACCOUNT = "account";
    private static final String NAME = "name";
    private static final String ORGS = "orgs";
    private static final String EMAIL_HASH = "email_hash";
    private static final String PHONE_HASH = "phone_hash";
    private static final String OKTA_ID = "okta_id";
    private static final String OKTA_ORG = "okta_org";

    @SqlUpdate("INSERT INTO internal_accounts_invitation (" + CODE + ", " + INVITER + ", " + TIMESTAMP + ", " +
            REGISTER_TIME + ", " + ACCOUNT + ", " + NAME + ", " + ORGS + ", " + EMAIL_HASH + ", " + PHONE_HASH + ", " + OKTA_ID + ", " + OKTA_ORG+ ") " +
            "VALUES (:code, :inviter, :timestamp, :register_time, :account, :name, :orgs, :email_hash, :phone_hash, :okta_id, :okta_org)")
  public abstract void insert(@Bind("code") String code,
                              @Bind("inviter") String inviter,
                              @Bind("timestamp") long timestamp,
                              @Bind("register_time") long register_time,
                              @Bind("account") String account,
                              @Bind("name") String name,
                              @Bind("orgs") String orgs,
                              @Bind(EMAIL_HASH) String emailHash,
                              @Bind(PHONE_HASH) String phoneHash,
                              @Bind(OKTA_ID) String okta_id,
                              @Bind(OKTA_ORG) String okta_org
    );

    @Mapper(InternalAccountsInvitationTable.InternalAccountsInvitationMapper.class)
    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE " + CODE + " = :code")
    public abstract List<InternalAccountsInvitationRow> get(@Bind("code") String code);

    @Mapper(InternalAccountsInvitationTable.InternalAccountsInvitationMapper.class)
    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE " + ACCOUNT + " = :account  order by register_time desc limit 1")
    public abstract InternalAccountsInvitationRow getByAccount(@Bind(ACCOUNT) String account);

    @Mapper(InternalAccountsInvitationTable.InternalAccountsInvitationMapper.class)
    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE " + OKTA_ID + " = :okta_id and "+OKTA_ORG + " = :okta_org and "+" register_time=0 order by timestamp desc")
    public abstract InternalAccountsInvitationRow getByOktaId(@Bind(OKTA_ID) String oktaId,@Bind(OKTA_ORG) String okta_org);

    @Mapper(InternalAccountsInvitationTable.InternalAccountsInvitationMapper.class)
    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE " + INVITER + " = :" + INVITER)
    public abstract List<InternalAccountsInvitationRow> getByInviter(@Bind(INVITER) String inviter);

    @Mapper(InternalAccountsInvitationTable.InternalAccountsInvitationMapper.class)
    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE to_timestamp(timestamp / 1000) > timestamp 'today' and " + INVITER + " = :" + INVITER)
    public abstract List<InternalAccountsInvitationRow> getByInviterToday(@Bind(INVITER) String inviter);

    @SqlUpdate("DELETE FROM internal_accounts_invitation WHERE " + CODE + " = :code")
    public abstract void remove(@Bind("code") String code);

    @SqlQuery("SELECT * FROM internal_accounts_invitation WHERE COALESCE (email,'') LIKE CONCAT('%',COALESCE(:email_hash, COALESCE(email, '')),'%') AND COALESCE (name,'') LIKE CONCAT('%',COALESCE(:name, COALESCE(name, '')),'%') AND COALESCE (account,'') LIKE CONCAT('%',COALESCE(:account, COALESCE(account, '')),'%') AND COALESCE (code,'') LIKE CONCAT('%',COALESCE(:code, COALESCE(code, '')),'%') AND COALESCE (inviter,'') LIKE CONCAT('%',COALESCE(:inviter, COALESCE(inviter, '')),'%') ORDER BY timestamp DESC OFFSET :offset LIMIT :limit")
    @Mapper(InternalAccountsInvitationMapper.class)
    public abstract List<InternalAccountsInvitationRow> getInvitationList(@Bind("offset") int offset, @Bind("limit") int limit, @Bind("email") String email, @Bind("name") String name, @Bind("account") String account, @Bind("code") String code, @Bind(INVITER) String inviter);

    @SqlQuery("SELECT COUNT(*) from internal_accounts_invitation WHERE COALESCE (email,'') LIKE CONCAT('%',COALESCE(:email_hash, COALESCE(email, '')),'%') AND COALESCE (name,'') LIKE CONCAT('%',COALESCE(:name, COALESCE(name, '')),'%') AND COALESCE (account,'') LIKE CONCAT('%',COALESCE(:account, COALESCE(account, '')),'%') AND COALESCE (code,'') LIKE CONCAT('%',COALESCE(:code, COALESCE(code, '')),'%') AND COALESCE (inviter,'') LIKE CONCAT('%',COALESCE(:inviter, COALESCE(inviter, '')),'%')")
    public abstract long getInvitationTotal(@Bind("email") String email, @Bind("name") String name, @Bind("account") String account, @Bind("code") String code, @Bind(INVITER) String inviter);

    @SqlUpdate("UPDATE internal_accounts_invitation SET orgs = :orgs , inviter =:inviter WHERE code = :code")
    public abstract void update(@Bind("code") String code, @Bind("orgs") String orgs, @Bind(INVITER) String inviter);

    @SqlUpdate("UPDATE internal_accounts_invitation SET timestamp = :timestamp WHERE email = :email_hash AND code = :code")
    public abstract void updateByEmail(@Bind("timestamp") long timestamp, @Bind("email") String email, @Bind("code") String code);

    @SqlUpdate("UPDATE internal_accounts_invitation SET " + CODE + "=:new_code, " + INVITER + "=:inviter, " +
            TIMESTAMP + "=:timestamp, " + REGISTER_TIME + "=:register_time, " + ACCOUNT + "=:account, " + NAME + "=:name, " + ORGS + "=:orgs, "  + OKTA_ID + "=:okta_id, "+ OKTA_ORG + "=:okta_org" + " WHERE " + CODE + "=:code ")
    public abstract void update(
            @Bind("code") String code,
            @Bind("new_code") String new_code,
            @Bind("inviter") String inviter,
            @Bind("timestamp") long timestamp,
            @Bind("register_time") long register_time,
            @Bind("account") String account,
            @Bind("name") String name,
            @Bind("orgs") String orgs,
            @Bind(OKTA_ID) String okta_id,
            @Bind(OKTA_ORG) String okta_org
    );

    public static class InternalAccountsInvitationMapper implements ResultSetMapper<InternalAccountsInvitationRow> {
        @Override
        public InternalAccountsInvitationRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new InternalAccountsInvitationRow(
                    resultSet.getString(CODE),
                    resultSet.getString(INVITER),
                    resultSet.getLong(TIMESTAMP),
                    resultSet.getLong(REGISTER_TIME),
                    resultSet.getString(ACCOUNT),
                    resultSet.getString(NAME),
                    resultSet.getString(ORGS),
                    resultSet.getString(EMAIL_HASH),
                    resultSet.getString(PHONE_HASH),
                    resultSet.getString(OKTA_ID),
                    resultSet.getString(OKTA_ORG)
            );
        }
    }
}
