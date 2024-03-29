/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.io.IOException;
import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public abstract class Accounts {

  private static final String ID     = "id";
  private static final String NUMBER = "number";
  private static final String DATA   = "data";

  private static final ObjectMapper mapper = SystemMapper.getMapper();

  @SqlUpdate("INSERT INTO accounts (" + NUMBER + ", " + DATA + ") VALUES (:number, CAST(:data AS json))")
  abstract void insertStep(@AccountBinder Account account);

  @SqlUpdate("DELETE FROM accounts WHERE " + NUMBER + " = :number")
  abstract int removeAccount(@Bind("number") String number);

  @Mapper(AccountMapper.class)
  @SqlQuery("SELECT * FROM accounts WHERE DATA::JSON ->> 'puid'= :puid and DATA::JSON ->> 'pid'= :pid")
  abstract Account getByPuid(@Bind("pid") String pid,@Bind("puid") String puid);

  @SqlUpdate("UPDATE accounts SET " + DATA + " = CAST(:data AS json) WHERE " + NUMBER + " = :number")
  abstract void update(@AccountBinder Account account);

  @Mapper(AccountMapper.class)
  @SqlQuery("SELECT * FROM accounts WHERE " + NUMBER + " = :number for update")
  abstract Account getForUpdate(@Bind("number") String number);

  @Mapper(AccountMapper.class)
  @SqlQuery("SELECT * FROM accounts WHERE " + NUMBER + " = :number")
  abstract Account get(@Bind("number") String number);

  @SqlQuery("SELECT COUNT(DISTINCT " + NUMBER + ") from accounts")
  abstract long getCount();

  @Mapper(AccountMapper.class)
  @SqlQuery("SELECT * FROM accounts OFFSET :offset LIMIT :limit")
  abstract List<Account> getAll(@Bind("offset") int offset, @Bind("limit") int length);

  @Mapper(AccountMapper.class)
  @SqlQuery("SELECT * FROM accounts")
  public abstract Iterator<Account> getAll();

  @SqlQuery("SELECT COUNT(*) FROM accounts a, json_array_elements(a.data->'devices') devices WHERE devices->>'id' = '1' AND (devices->>'gcmId') is not null AND (devices->>'lastSeen')\\:\\:bigint >= :since")
  public abstract int getAndroidActiveSinceCount(@Bind("since") long since);

  @SqlQuery("SELECT COUNT(*) FROM accounts a, json_array_elements(a.data->'devices') devices WHERE devices->>'id' = '1' AND (devices->>'apnId') is not null AND (devices->>'lastSeen')\\:\\:bigint >= :since")
  public abstract int getIosActiveSinceCount(@Bind("since") long since);

  @SqlQuery("SELECT count(*) FROM accounts a, json_array_elements(a.data->'devices') devices WHERE devices->>'id' = '1' AND (devices->>'lastSeen')\\:\\:bigint >= :since AND (devices->>'signedPreKey') is null AND (devices->>'gcmId') is not null")
  public abstract int getUnsignedKeysCount(@Bind("since") long since);

  @SqlQuery("SELECT number\n" +
          "FROM\n" +
          "\t(SELECT number,MAX(LASTSEEN) LASTSEEN\n" +
          "\t\tFROM\n" +
          "\t\t\t(SELECT A.NUMBER,\n" +
          "\t\t\t\t\tCOALESCE((DEVICES ->> 'lastSeen')::bigint,0) AS LASTSEEN\n" +
          "\t\t\t\tFROM ACCOUNTS A,\n" +
          "\t\t\t\t\tJSON_ARRAY_ELEMENTS(A.DATA -> 'devices') DEVICES,\n" +
          "\t\t\t\t\tINTERNAL_ACCOUNTS I\n" +
          "\t\t\t\tWHERE A.NUMBER = I.NUMBER\n" +
          "\t\t\t\tAND  (A.DATA::JSON#>>'{pid}' is null )\n" +
          "\t\t\t\t\tAND I.DISABLED = 'false' and i.inactive='false' ) B\n" +
          "\t\tGROUP BY number) C\n" +
          "WHERE C.LASTSEEN < :to and C.LASTSEEN >= :since")
  public abstract List<String> getAccountsByLastSeen(@Bind("since") long since,@Bind("to") long to);

  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  public boolean create(Account account) {
    int rows = removeAccount(account.getNumber());
    insertStep(account);

    return rows == 0;
  }

  @SqlUpdate("VACUUM accounts")
  public abstract void vacuum();

  public static class AccountMapper implements ResultSetMapper<Account> {
    @Override
    public Account map(int i, ResultSet resultSet, StatementContext statementContext)
        throws SQLException
    {
      try {
        Account account = mapper.readValue(resultSet.getString(DATA), Account.class);
//        account.setId(resultSet.getLong(ID));

        return account;
      } catch (IOException e) {
        throw new SQLException(e);
      }
    }
  }

  @BindingAnnotation(AccountBinder.AccountBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface AccountBinder {
    public static class AccountBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<AccountBinder, Account>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           AccountBinder accountBinder,
                           Account account)
          {
            try {
              String serialized = mapper.writeValueAsString(account);

              sql.bind(NUMBER, account.getNumber());
              sql.bind(DATA, serialized);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException(e);
            }
          }
        };
      }
    }
  }

}
