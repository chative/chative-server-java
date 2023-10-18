package org.whispersystems.textsecuregcm.storage;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
public abstract class PlatformsTable {
  public static final int MEMCACHE_VERION = 1;
  private static final String PID     = "pid";
  private static final String OWNER = "owner";
  private static final String NOTE   = "note";

  @SqlUpdate("INSERT INTO platforms (" + PID + ", " + OWNER + ", "+ NOTE + ") VALUES (:pid,:owner,:note")
  abstract void insert(@PlatformBinder Platform platform);

  @SqlUpdate("DELETE FROM platforms WHERE " + PID + " = :pid")
  abstract int remove(@Bind("pid") String pid);

  @SqlUpdate("UPDATE platforms SET " + OWNER + " = :owner" +", "+ NOTE +"= :note"+" WHERE " + PID + " = :pid")
  abstract void update(@PlatformBinder Platform platform);

  @Mapper(PlatformMapper.class)
  @SqlQuery("SELECT * FROM platforms WHERE " + PID + " = :pid")
  abstract Platform get(@Bind("pid") String pid);


  public static class PlatformMapper implements ResultSetMapper<Platform> {
    @Override
    public Platform map(int i, ResultSet resultSet, StatementContext statementContext)
            throws SQLException {
        return new Platform(
                resultSet.getString(PID),
                resultSet.getString(OWNER),
                resultSet.getString(NOTE)
        );
    }
  }

  @BindingAnnotation(PlatformBinder.PlatformBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface PlatformBinder {
    public static class PlatformBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<PlatformBinder, Platform>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           PlatformBinder platformBinder,
                           Platform platform)
          {
              sql.bind(PID, platform.getPid());
              sql.bind(OWNER, platform.getOwner());
              sql.bind(NOTE, platform.getNote());
          }
        };
      }
    }
  }

}
