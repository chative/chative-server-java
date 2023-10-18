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

public abstract class GroupMembersTable {

  public enum ROLE {
    OWNER,
    ADMIN,
    MEMBER;

    private static ROLE[] allValues = values();

    public static ROLE fromOrdinal(int n) {
      switch (n) {
        case 0: return OWNER;
        case 1: return ADMIN;
        case 2: return MEMBER;
        default: return null;
      }
    }
  }

  public enum RAPID_ROLE {
    NONE,
    RECOMMEND,
    AGREE,
    PERFORM,
    INPUT,
    DECIDER,
    OBSERVER;

    private static RAPID_ROLE[] allValues = values();

    public static RAPID_ROLE fromOrdinal(int n) {
      switch (n) {
        case 0: return NONE;
        case 1: return RECOMMEND;
        case 2: return AGREE;
        case 3: return PERFORM;
        case 4: return INPUT;
        case 5: return DECIDER;
        case 6: return OBSERVER;
        default: return null;
      }
    }
  }


  public enum NOTIFICATION {
    ALL,
    MENTION,
    OFF;

    private static NOTIFICATION[] allValues = values();
    public static NOTIFICATION fromOrdinal(int n) {
      switch (n) {
        case 0: return ALL;
        case 1: return MENTION;
        case 2: return OFF;
        default: return null;
      }
    }
  }

  @SqlUpdate("INSERT INTO group_members VALUES (:gid, :uid, :role, cast(extract(epoch from current_timestamp) * 1000 as BigInt), :inviter, :display_name, :remark, :notification, :use_global,:rapid_role)")
  public abstract void insert(@Bind("gid") String gid, @Bind("uid") String uid, @Bind("role") int role,
                              @Bind("inviter") String inviter, @Bind("display_name") String display_name,
                              @Bind("remark") String remark, @Bind("notification") int notification,@Bind("use_global") boolean useGlobal, @Bind("rapid_role") int rapid_role);

  @SqlUpdate("UPDATE group_members set role=:role, display_name=:display_name, remark=:remark, notification=:notification, use_global=:use_global ,rapid_role=:rapid_role where gid=:gid and uid=:uid")
  public abstract void update( @Bind("role") int role,
                              @Bind("display_name") String display_name, @Bind("remark") String remark,
                              @Bind("notification") int notification,@Bind("use_global") boolean useGlobal,
                               @Bind("gid") String gid, @Bind("uid") String uid, @Bind("rapid_role") int rapid_role);

  @SqlUpdate("UPDATE group_members set  invite_code = :invite_code  where gid=:gid and uid=:uid")
  public abstract void saveInviteCode( @Bind("gid") String gid, @Bind("uid") String uid, @Bind("invite_code") String inviteCode);
  @SqlUpdate("DELETE FROM group_members where gid=:gid")
  public abstract void deleteByGroup(@Bind("gid") String gid);

  @SqlUpdate("DELETE FROM group_members where gid=:gid and uid=:uid")
  public abstract void delete(@Bind("gid") String gid, @Bind("uid") String uid);

  @Mapper(GroupMembersMapper.class)
  @SqlQuery("SELECT * FROM group_members WHERE gid=:gid")
  public abstract List<GroupMember> getGroupMembersInternal(@Bind("gid") String gid);

  public  List<GroupMember> getGroupMembers(String gid){
    List<GroupMember> groupMembers = getGroupMembersInternal(gid);
    for (GroupMember member : groupMembers) { // 不暴露inviteCode
      member.setInviteCode(null);
    }
    return groupMembers;
  }

  @Mapper(GroupMembersMapper.class)
  @SqlQuery("SELECT * FROM group_members WHERE uid=:uid")
  public abstract List<GroupMember> getMemberGroups(@Bind("uid") String uid);

  @Mapper(GroupMembersMapper.class)
  @SqlQuery("SELECT * FROM group_members WHERE gid=:gid and uid=:uid")
  public abstract GroupMember getGroupMember(@Bind("gid") String gid, @Bind("uid") String uid);

  @Mapper(GroupMembersMapper.class)
  @SqlQuery("SELECT * FROM group_members WHERE invite_code =:invite_code ")
  public abstract GroupMember getGroupMemberByInviteCode(@Bind("invite_code") String inviteCode);

  public static class GroupMembersMapper implements ResultSetMapper<GroupMember> {
    @Override
    public GroupMember map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
      return new GroupMember(
        resultSet.getString("gid"),
        resultSet.getString("uid"),
        resultSet.getInt("role"),
        resultSet.getLong("create_time"),
        resultSet.getString("inviter"),
        resultSet.getString("display_name"),
        resultSet.getString("remark"),
        resultSet.getInt("notification"),
        resultSet.getBoolean("use_global"),
        resultSet.getInt("rapid_role"),
        resultSet.getString("invite_code")
      );
    }
  }
}
