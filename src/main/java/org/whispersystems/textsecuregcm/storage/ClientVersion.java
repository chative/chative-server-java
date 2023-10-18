package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class ClientVersion {

    @Mapper(ClientVersionMapper.class)
    @SqlQuery("select dft_version,count(*) as count from client_versions where os =:os and dft_version > '2' group by dft_version order by count desc limit 10")
    public abstract List<ClientVersionRow> aggVersionCnt(@Bind("os") String os);

    @Mapper(ClientVersionMapper.class)
    @SqlQuery("select dft_version,count(*) as count from client_versions where os =:os and dft_version >= :ver group by dft_version order by count desc limit 10")
    public abstract List<ClientVersionRow> aggVersionCnt(@Bind("os") String os,@Bind("ver") String ver);


    @Mapper(ClientInfoMapper.class)
    @SqlQuery("select dft_version,os,ua from client_versions where number =:number and device=:device order by last_login desc limit 1")
    public abstract ClientInfoRow getClientInfo(@Bind("number") String number,@Bind("device") String device);

    public static class ClientInfoMapper implements ResultSetMapper<ClientInfoRow> {
        @Override
        public ClientInfoRow map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
            return new ClientInfoRow(
                    resultSet.getString("dft_version"),
                    resultSet.getString("os"),
                    resultSet.getString("ua")
            );
        }
    }

    public static class ClientVersionMapper implements ResultSetMapper<ClientVersionRow> {
        @Override
        public ClientVersionRow map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
            return new ClientVersionRow(
                    resultSet.getString("dft_version"),
                    resultSet.getInt("count")
            );
        }
    }

}
