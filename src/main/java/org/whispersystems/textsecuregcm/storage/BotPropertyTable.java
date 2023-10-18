package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BotPropertyTable {
    private static final String AutoAnswer = "auto_answer";
    private static final String Space = "space";
    private static final String AnswerServerHost = "answer_server_host";

    @Mapper(BotPropertyRowMapper.class)
    @SqlQuery("select * from bot_properties where number = :number")
    public abstract BotPropertyRow get(@Bind("number") String number);

    public static class BotPropertyRowMapper implements ResultSetMapper<BotPropertyRow> {

        @Override
        public BotPropertyRow map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return  new BotPropertyRow(
                    r.getBoolean(AutoAnswer),
                    r.getString(Space),
                    r.getString(AnswerServerHost)
            );
        }
    }
}
