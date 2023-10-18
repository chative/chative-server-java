package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

public abstract class InternalTeamsDB {

    private static final String NAME = "name";

    private static final String ID = "id";

    private static final String PARENT_ID = "parent_id";

    private static final String ANCESTORS = "ancestors";

    private static final String STATUS = "status";

    private static final String ORDER_NUM = "order_num";

    private static final String CREATE_TIME = "create_time";

    private static final String APP_ID = "appid";

    private static final String PID = "pid";

    private static final String REMARK = "remark";

    @SqlUpdate("INSERT INTO teams (" + NAME + ") VALUES (:name)")
    public abstract void insert(@Bind("name") String name);

    @SqlUpdate("INSERT INTO teams (" +NAME+", "+PARENT_ID+", "+ANCESTORS+", "+STATUS+", "+ORDER_NUM+", "+APP_ID+", "+ PID+", "+REMARK+") VALUES (:name, :parentId, :ancestors, :status, :orderNum, :appid, :pid, :remark)")
    public abstract void insertNew(@Bind("name") String name, @Bind("parentId") long parentId, @Bind("ancestors") String ancestors,
                                   @Bind("status") boolean status, @Bind("orderNum") int orderNum, @Bind("appid") String appid, @Bind("pid") String pid, @Bind("remark") String remark);

    @Mapper(InternalGroupMapper.class)
    @SqlQuery("SELECT * FROM teams WHERE " + NAME + "=:name")
    public abstract InternalGroupsRow get(@Bind("name") String name);


    @Mapper(InternalGroupMapper.class)
    @SqlQuery("select * from teams where :name like CONCAT('%',replace("+NAME+", '_autojoin_domain_', ''))")
    public abstract  List<InternalGroupsRow> getForEndWith(@Bind("name") String name);


    @Mapper(InternalGroupMapper.class)
    @SqlQuery("SELECT * FROM teams WHERE " + ID + "=:id")
    public abstract InternalGroupsRow getById(@Bind("id") int id);

    @Mapper(InternalGroupMapper.class)
    @SqlQuery("SELECT * FROM teams WHERE pid LIKE CONCAT('%',:pid,'%') OFFSET :offset LIMIT :limit")
    public abstract List<InternalGroupsRow> getAll(@Bind("offset") int offset, @Bind("limit") int length, @Bind("pid") String pid);

    @SqlUpdate("DELETE FROM teams WHERE " + NAME + "=:name")
    public abstract void remove(@Bind("name") String name);

    @SqlUpdate("UPDATE teams SET " + NAME + "=:new_name WHERE " + NAME + "=:name")
    public abstract void update(@Bind("name") String name, @Bind("new_name") String new_name);

    @SqlUpdate("UPDATE teams SET "+ NAME + "=:name, "+ PARENT_ID +"=:parentId, "+ ANCESTORS +"=:ancestors, "+ STATUS +"=:status, "+ ORDER_NUM +"=:orderNum ,"+ REMARK +"=:remark WHERE " + ID + "=:id")
    public abstract void updateNew(@Bind("id") long id, @Bind("name") String name, @Bind("parentId") long parentId, @Bind("ancestors") String ancestors,
                                   @Bind("status") boolean status, @Bind("orderNum") int orderNum, @Bind("remark") String remark);

    @Mapper(InternalTeamMapper.class)
    @SqlQuery("SELECT * FROM teams WHERE COALESCE (NAME,'') LIKE CONCAT('%',COALESCE(:name, COALESCE(NAME, '')),'%') AND status = ANY (:list)")
    public abstract List<Team> getTree(@Bind("name") String name, @BindBooleanList("list") List<Boolean> list);

    public static class InternalGroupMapper implements ResultSetMapper<InternalGroupsRow> {
        @Override
        public InternalGroupsRow map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new InternalGroupsRow(
                    resultSet.getInt(ID),
                    resultSet.getString(NAME),
                    resultSet.getString(APP_ID),
                    resultSet.getString(PID)
            );
        }
    }

    public static class InternalTeamMapper implements ResultSetMapper<Team> {
        @Override
        public Team map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            Team team = new Team();
            team.setId(resultSet.getInt(ID));
            team.setName(resultSet.getString(NAME));
            team.setParentId(resultSet.getInt(PARENT_ID));
            team.setAncestors(resultSet.getString(ANCESTORS));
            team.setOrderNum(resultSet.getInt(ORDER_NUM));
            team.setCreateTime(resultSet.getLong(CREATE_TIME));
            team.setStatus(resultSet.getBoolean(STATUS));
            team.setRemark(resultSet.getString(REMARK));
            team.setPid(resultSet.getString(PID));
            team.setAppid(resultSet.getString(APP_ID));
            return team;
        }
    }

    @BindingAnnotation(InternalTeamsDB.BindBooleanList.BindFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindBooleanList {
        String value() default "it";

        class BindFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<InternalTeamsDB.BindBooleanList, Collection<Boolean>>() {
                    @Override
                    public void bind(SQLStatement<?> q, InternalTeamsDB.BindBooleanList bind, Collection<Boolean> arg) {
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
}
