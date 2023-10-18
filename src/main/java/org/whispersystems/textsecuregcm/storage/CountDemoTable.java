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

public abstract class CountDemoTable {


    private static final String COUNT = "count";


    @SqlUpdate("INSERT INTO demo_counts (id, " + COUNT + ") VALUES (1, 0) ON CONFLICT ON CONSTRAINT pk_demo_counts DO UPDATE SET count = 0;")
    public abstract void init();

    @Mapper(CountDemoRowMapper.class)
    @SqlQuery("SELECT * FROM demo_counts ;")
    public abstract List<CountDemoRow> get();

    @SqlUpdate("UPDATE demo_counts set count=:count;")
    public abstract void update(@Bind("count") int count);


    public static class CountDemoRowMapper implements ResultSetMapper<CountDemoRow> {
        @Override
        public CountDemoRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new CountDemoRow(
                    resultSet.getInt(COUNT));
        }
    }
}
