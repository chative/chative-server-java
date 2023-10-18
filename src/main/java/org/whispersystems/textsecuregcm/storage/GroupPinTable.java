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
import java.util.UUID;

public abstract class GroupPinTable {

  public enum STATUS {
    ACTIVE,
    DISMISSED,
    EXPIRED,
    DISABLED,
  }

  @SqlUpdate("INSERT INTO group_pin VALUES (:id, :gid, :creator, :create_time, :status, :content, :conversion_id)")
  public abstract void insert(@Bind("id") String id, @Bind("gid") String gid, @Bind("creator") String creator, @Bind("create_time") long create_time, @Bind("status") int status, @Bind("content") String content, @Bind("conversion_id") String conversion_id);

  @SqlUpdate("UPDATE group_pin SET status=:status where id=:id")
  public abstract void updateStatus(@Bind("status") int status, @Bind("id") String id);

  @Mapper(GroupPinMapper.class)
  @SqlQuery("SELECT * FROM group_pin WHERE id=:id")
  public abstract GroupPin get(@Bind("id") String id);

  @Mapper(GroupPinMapper.class)
  @SqlQuery("SELECT * FROM group_pin WHERE gid=:gid and status=:status order by create_time desc offset :offset limit :limit")
  public abstract List<GroupPin> getByGid(@Bind("gid") String gid, @Bind("status") int status, @Bind("offset")int offset, @Bind("limit")int limit);

  @SqlUpdate("DELETE FROM group_pin where gid=:gid")
  public abstract void deleteGroup(@Bind("gid") String gid);

  @Mapper(GroupPinMapper.class)
  @SqlQuery("SELECT * FROM group_pin")
  public abstract List<GroupPin> getAll();


  public GroupPin create(GroupPin groupPin) {
    // generate gid
    final String id = UUID.randomUUID().toString().replace("-", "");
    insert(id, groupPin.getGid(), groupPin.getCreator(), System.currentTimeMillis(), STATUS.ACTIVE.ordinal(),groupPin.getContent(), groupPin.getConversationId() );

    groupPin = get(id);

    return groupPin;
  }

  public static class GroupPinMapper implements ResultSetMapper<GroupPin> {
    @Override
    public GroupPin map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new GroupPin(
          resultSet.getString("id"),
          resultSet.getString("gid"),
          resultSet.getString("creator"),
          resultSet.getLong("create_time"),
          resultSet.getInt("status"),
          resultSet.getString("content"),
          resultSet.getString("conversation_id")
      );
    }
  }
}
