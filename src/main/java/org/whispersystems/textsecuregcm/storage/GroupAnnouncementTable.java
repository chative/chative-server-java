package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public abstract class GroupAnnouncementTable {

  public enum STATUS {
    ACTIVE,
    DISMISSED,
    EXPIRED,
    DISABLED,
  }

  @SqlUpdate("INSERT INTO group_announcement VALUES (:id, :gid, :creator, :create_time, :reviser, :revise_time, :status, :announcement_expiry, :content)")
  public abstract void insert(@Bind("id") String id, @Bind("gid") String gid, @Bind("creator") String creator, @Bind("create_time") long create_time, @Bind("reviser") String reviser, @Bind("revise_time") long revise_time, @Bind("status") int status, @Bind("announcement_expiry") long announcement_expiry, @Bind("content") String content);

  @SqlUpdate("UPDATE group_announcement SET  announcement_expiry=:announcement_expiry, content=:content, reviser=:reviser, revise_time=:revise_time where id=:id")
  public abstract void update(@Bind("announcement_expiry") long announcement_expiry, @Bind("content") String content, @Bind("reviser") String reviser,  @Bind("revise_time") long revise_time, @Bind("id") String id);

  @SqlUpdate("UPDATE group_announcement SET status=:status where id=:id")
  public abstract void updateStatus(@Bind("status") int status, @Bind("id") String id);

  @Mapper(GroupAnnouncementMapper.class)
  @SqlQuery("SELECT * FROM group_announcement WHERE id=:id")
  public abstract GroupAnnouncement get(@Bind("id") String id);

  @Mapper(GroupAnnouncementMapper.class)
  @SqlQuery("SELECT * FROM group_announcement WHERE gid=:gid")
  public abstract List<GroupAnnouncement> getByGid(@Bind("gid") String gid);

  @SqlUpdate("DELETE FROM group_announcement where gid=:gid")
  public abstract void deleteByGroup(@Bind("gid") String gid);

  @Mapper(GroupAnnouncementMapper.class)
  @SqlQuery("SELECT * FROM group_announcement")
  public abstract List<GroupAnnouncement> getAll();


  public GroupAnnouncement create(GroupAnnouncement groupAnnouncement) {
    // generate gid
    final String id = UUID.randomUUID().toString().replace("-", "");
    insert(id, groupAnnouncement.getGid(), groupAnnouncement.getCreator(), System.currentTimeMillis(), groupAnnouncement.getReviser(), System.currentTimeMillis(), STATUS.ACTIVE.ordinal(), groupAnnouncement.getAnnouncementExpiry(),groupAnnouncement.getContent() );

    groupAnnouncement = get(id);

    return groupAnnouncement;
  }

  public static class GroupAnnouncementMapper implements ResultSetMapper<GroupAnnouncement> {
    @Override
    public GroupAnnouncement map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new GroupAnnouncement(
          resultSet.getString("id"),
          resultSet.getString("gid"),
          resultSet.getString("creator"),
          resultSet.getLong("create_time"),
          resultSet.getString("reviser"),
          resultSet.getLong("revise_time"),
          resultSet.getInt("status"),
          resultSet.getInt("announcement_expiry"),
          resultSet.getString("content")
      );
    }
  }
}
