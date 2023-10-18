package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.whispersystems.textsecuregcm.entities.Notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public abstract class GroupsTable {

  public enum STATUS {
    ACTIVE,
    DISMISSED,
    EXPIRED,
    DISABLED,
  }

  public enum RemindCycle {
    NONE("none"),
    WEEKLY("weekly"),
    MONTHLY("monthly");
    private String value;
    RemindCycle(String value){
      this.value=value;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
    public static RemindCycle fromValue(String value) {
      for(RemindCycle remindCycle: RemindCycle.values()){
        if(remindCycle.getValue().equals(value)){
          return remindCycle;
        }
      }
      return null;
    }
  }

  @SqlUpdate("INSERT INTO groups VALUES (:id, :name, :creator, :create_time, :status, :message_expiry, :avatar, :version ,:invitation_rule , :last_active_time ,:remind_cycle,:anyone_remove,:rejoin,:ext,:publish_rule)")
  public abstract void insert(@Bind("id") String id, @Bind("name") String name, @Bind("creator") String creator, @Bind("create_time") long create_time, @Bind("status") int status, @Bind("message_expiry") long message_expiry, @Bind("avatar") String avatar, @Bind("version") int version, @Bind("invitation_rule") int invitationRule,@Bind("last_active_time") long lastActiveTime,@Bind("remind_cycle") String remind_cycle
          ,@Bind("anyone_remove") boolean anyoneRemove,@Bind("rejoin") boolean rejoin,@Bind("ext") boolean ext,@Bind("publish_rule") int publishRule);

  @SqlUpdate("UPDATE groups SET name=:name, status=:status, message_expiry=:message_expiry, avatar=:avatar ,version=:version ,invitation_rule=:invitation_rule , last_active_time=:last_active_time ,remind_cycle=:remind_cycle,anyone_remove=:anyone_remove,link_invite_switch=:link_invite_switch,rejoin=:rejoin,ext=:ext,publish_rule=:publish_rule where id=:id")
  public abstract void update(@Bind("name") String name, @Bind("status") int status, @Bind("message_expiry") long message_expiry, @Bind("avatar") String avatar,  @Bind("version") int version, @Bind("invitation_rule") int invitationRule,@Bind("last_active_time") long lastActiveTime,@Bind("remind_cycle") String remind_cycle ,@Bind("anyone_remove") boolean anyoneRemove,@Bind("link_invite_switch") boolean linkInviteSwitch,@Bind("rejoin") boolean rejoin,@Bind("ext") boolean ext,@Bind("publish_rule") int publishRule,@Bind("id") String id);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups WHERE id=:id")
  public abstract Group get(@Bind("id") String id);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups WHERE id=:id FOR UPDATE")
  public abstract Group getForUpdate(@Bind("id") String id);

  @SqlUpdate("UPDATE groups SET version=:version ,last_active_time=:last_active_time,ext=:ext WHERE id=:id")
  public abstract void updateVersion( @Bind("version") int version,@Bind("last_active_time") long lastActiveTime,@Bind("ext") boolean ext,@Bind("id") String id);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups")
  public abstract List<Group> getAll();

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups where  last_active_time<:last_active_time")
  public abstract List<Group> ListGroupByActiveTime(@Bind("last_active_time") long  lastActiveTime);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups where status =0 and last_active_time<:last_active_time")
  public abstract List<Group> getGroupByActiveTime(@Bind("last_active_time") long  lastActiveTime);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups where status =0 and remind_cycle=:remind_cycle")
  public abstract List<Group> getGroupByRemindCycle(@Bind("remind_cycle") String  remindCycle);

  @Mapper(GroupsMapper.class)
  @SqlQuery("SELECT * FROM groups WHERE COALESCE (name,'') ILIKE CONCAT('%',COALESCE(:name, COALESCE(name, '')),'%') OR COALESCE (id,'') ILIKE CONCAT('%',COALESCE(:name, COALESCE(id, '')),'%') OR COALESCE (creator,'') ILIKE CONCAT('%',COALESCE(:name, COALESCE(creator, '')),'%') ORDER BY create_time DESC OFFSET :offset LIMIT :limit")
  public abstract List<Group> getGroupsList(@Bind("name") String name, @Bind("offset") int offset, @Bind("limit") int limit);

  @SqlQuery("SELECT COUNT(*) FROM groups WHERE COALESCE (name,'') ILIKE CONCAT('%',COALESCE(:name, COALESCE(name, '')),'%')")
  public abstract long getGroupsTotal(@Bind("name") String name);

  @SqlUpdate("DELETE FROM groups where id=:id")
  public abstract void deleteGroup(@Bind("id") String id);

  public Group updateLastActiveTime(String gid){
    Group groupDB=this.getForUpdate(gid);
    updateVersion(groupDB.getVersion(),System.currentTimeMillis(), groupDB.isExt(), gid);
    return get(gid);
  }

  public Group updateOnlyVersion(String gid){
    Group groupDB=this.getForUpdate(gid);
    updateVersion(groupDB.getVersion()+1,groupDB.getLastActiveTime(), groupDB.isExt(),gid);
    return get(gid);
  }

  public Group updateForVersion(Group group) {
    Group groupDB=this.getForUpdate(group.getId());
    update(group.getName(),group.getStatus(),group.getMessageExpiry(),group.getAvatar(),groupDB.getVersion()+1,group.getInvitationRule(),System.currentTimeMillis(),group.getRemindCycle(),group.isAnyoneRemove(),group.linkInviteSwitchOn(),group.isRejoin(),group.isExt(),group.getPublishRule(),group.getId());
    group = get(group.getId());
    return group;
  }

  public Group updateForVersionWithoutLastActiveTime(Group group) {
    Group groupDB=this.getForUpdate(group.getId());
    update(group.getName(),group.getStatus(),group.getMessageExpiry(),group.getAvatar(),groupDB.getVersion()+1,group.getInvitationRule(),group.getLastActiveTime(),group.getRemindCycle(),group.isAnyoneRemove(), group.linkInviteSwitchOn(), group.isRejoin(),group.isExt(),group.getPublishRule(),group.getId());
    group = get(group.getId());
    return group;
  }

  public Group updateForVersionWithLastActiveTime(String gid,Boolean ext) {
    Group groupDB=this.getForUpdate(gid);
    if(ext==null){
      ext=groupDB.isExt();
    }
    updateVersion(groupDB.getVersion()+1,System.currentTimeMillis(),ext,gid);
    return get(gid);
  }
  public Group upgradeCreate(Group group) {
    insert(group.getId(), group.getName(), group.getCreator(), System.currentTimeMillis(), 0, group.getMessageExpiry(), group.getAvatar(),group.getVersion(),group.getInvitationRule(),System.currentTimeMillis(),group.getRemindCycle(),group.isAnyoneRemove(),group.isRejoin(),group.isExt(),group.getPublishRule());
    group = get(group.getId());
    return group;
  }
  public Group create(Group group) {
    // generate gid
    final String id = UUID.randomUUID().toString().replace("-", "");

    insert(id, group.getName(), group.getCreator(), System.currentTimeMillis(), 0, group.getMessageExpiry(), group.getAvatar(),group.getVersion(),group.getInvitationRule(),System.currentTimeMillis(),group.getRemindCycle(),group.isAnyoneRemove(),group.isRejoin(),group.isExt(),group.getPublishRule());

    group = get(id);

    return group;
  }

  public static class GroupsMapper implements ResultSetMapper<Group> {
    @Override
    public Group map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new Group(
              resultSet.getString("id"),
              resultSet.getString("name"),
              resultSet.getString("creator"),
              resultSet.getLong("create_time"),
              resultSet.getInt("status"),
              resultSet.getLong("message_expiry"),
              resultSet.getString("avatar"),
              resultSet.getInt("invitation_rule"),
              resultSet.getInt("version"),
              resultSet.getLong("last_active_time"),
              resultSet.getString("remind_cycle"),
              resultSet.getBoolean("anyone_remove"),
              resultSet.getBoolean("rejoin"),
              resultSet.getBoolean("ext"),
              resultSet.getInt("publish_rule"),
              resultSet.getBoolean("link_invite_switch")
      );
    }
  }
}
