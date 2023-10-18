package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class InternalTeamsAccountsTable {

    public static final int MEMCACHE_VERION = 1;

    private static final String TEAM_NAME = "team_name";

    private static final String ACCOUNT_NUMBER = "account_number";

    private static final String TEAM_ID = "team_id";

    @SqlUpdate("INSERT INTO teams_members (" + TEAM_NAME + ", " + ACCOUNT_NUMBER + ", "+TEAM_ID+")  SELECT :team_name, :account_number, ID FROM teams WHERE teams.name = :team_name")
    public abstract void insert(@Bind("team_name") String team_name, @Bind("account_number") String account_number);

    @Mapper(InternalGroupsAccountsRowMapper.class)
    @SqlQuery("SELECT * FROM teams_members WHERE " + TEAM_NAME + "=:team_name")
    public abstract List<InternalGroupsAccountsRow> get(@Bind("team_name") String team_name);

    @SqlUpdate("DELETE FROM teams_members WHERE " + TEAM_NAME + "=:team_name")
    public abstract void remove(@Bind("team_name") String team_name);

    @SqlUpdate("DELETE FROM teams_members WHERE " + TEAM_NAME + "=:team_name and " +ACCOUNT_NUMBER+" =:account_number")
    public abstract void delete(@Bind("team_name") String team_name, @Bind("account_number") String account_number);

    @Mapper(InternalGroupsAccountsRowMapper.class)
    @SqlQuery("SELECT * FROM teams_members WHERE " + ACCOUNT_NUMBER + "=:account_number")
    public abstract List<InternalGroupsAccountsRow> getAccountGroups(@Bind("account_number") String account_number);

    @Mapper(InternalGroupsAccountsRowMapper.class)
    @SqlQuery("SELECT * FROM teams_members WHERE " + TEAM_NAME + "=:team_name")
    public abstract List<InternalGroupsAccountsRow> getGroupAccounts(@Bind("team_name") String team_name);

    public static class InternalGroupsAccountsRowMapper implements ResultSetMapper<InternalGroupsAccountsRow> {
        @Override
        public InternalGroupsAccountsRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new InternalGroupsAccountsRow(
                    resultSet.getString(TEAM_NAME),
                    resultSet.getString(ACCOUNT_NUMBER)
            );
        }
    }
}
