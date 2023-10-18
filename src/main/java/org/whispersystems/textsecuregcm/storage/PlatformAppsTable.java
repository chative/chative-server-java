package org.whispersystems.textsecuregcm.storage;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class PlatformAppsTable {

  private static final String PID     = "pid";
  private static final String APPID = "appid";
  private static final String NOTE   = "note";


  @SqlUpdate("INSERT INTO platform_apps (" + PID + ", " + APPID + ", "+ NOTE + ") VALUES (:pid,:appid,:note")
  abstract void insert(@PlatformAppBinder PlatformApp platformApp);

  @SqlUpdate("DELETE FROM platform_apps WHERE " + PID + " = :pid and "+ APPID +" =:appid")
  abstract int remove(@Bind("pid") String pid,@Bind("appid") String appid);

  @SqlUpdate("UPDATE platform_apps SET " + NOTE +"= :note"+" WHERE " + PID + " = :pid and "+ APPID +" =:appid")
  abstract void update(@PlatformAppBinder PlatformApp platformApp);

  @Mapper(PlatformAppMapper.class)
  @SqlQuery("SELECT * FROM platform_apps WHERE " + APPID + " = :appid")
  abstract PlatformApp getByAppid(@Bind("appid") String appid);

  @Mapper(PlatformAppMapper.class)
  @SqlQuery("SELECT * FROM platform_apps WHERE " + PID + " = :pid")
  abstract List<PlatformApp> get(@Bind("pid") String pid);


  public static class PlatformAppMapper implements ResultSetMapper<PlatformApp> {
    @Override
    public PlatformApp map(int i, ResultSet resultSet, StatementContext statementContext)
            throws SQLException {
        return new PlatformApp(
                resultSet.getString(PID),
                resultSet.getString(APPID),
                resultSet.getString(NOTE)
        );
    }
  }

  @BindingAnnotation(PlatformAppBinder.PlatformAppBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface PlatformAppBinder {
    public static class PlatformAppBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<PlatformAppBinder, PlatformApp>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           PlatformAppBinder platformAppBinder,
                           PlatformApp platformApp)
          {
              sql.bind(PID, platformApp.getPid());
              sql.bind(APPID, platformApp.getAppid());
              sql.bind(NOTE, platformApp.getNote());
          }
        };
      }
    }
  }

}
