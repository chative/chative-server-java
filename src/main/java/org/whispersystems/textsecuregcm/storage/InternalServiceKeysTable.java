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
import com.github.difftim.security.signing.SignatureVerifier;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.io.IOException;
import java.lang.annotation.*;
import java.sql.*;
import java.util.*;

public abstract class InternalServiceKeysTable {

  private static final String APPID     = "appid";
  private static final String ALGORITHM = "algorithm";
  private static final String KEY   = "key";
  private static final String SIGNATURE_EXPIRE_TIME  = "signature_expire_time";
  private static final String ALLOWED_IP_LIST   = "allowed_ip_list";
  private static final ObjectMapper mapper = SystemMapper.getMapper();

  @SqlUpdate("INSERT INTO internal_service_keys (" + APPID + ", " + ALGORITHM + ", "+ KEY + ", "+ SIGNATURE_EXPIRE_TIME + ", "+ ALLOWED_IP_LIST + ") VALUES (:appid,:algorithm,:key,:signature_expire_time, CAST(:allowed_ip_list AS json))")
  abstract void insert(@InternalServiceKeyBinder InternalServiceKey internalServiceKey);

  @SqlUpdate("DELETE FROM internal_service_keys WHERE " + APPID + " = :appid")
  abstract int remove(@Bind("appid") String appid);

  @SqlUpdate("UPDATE internal_service_keys SET " + ALGORITHM + " = :algorithm" +", "+ KEY +"= :key"+", "+ SIGNATURE_EXPIRE_TIME +"= :signature_expire_time"+", "+ ALLOWED_IP_LIST +"= CAST(:allowed_ip_list AS json) "+" WHERE " + APPID + " = :appid")
  abstract void update(@InternalServiceKeyBinder InternalServiceKey internalServiceKey);

  @Mapper(InternalServiceKeyMapper.class)
  @SqlQuery("SELECT * FROM internal_service_keys WHERE " + APPID + " = :appid")
  abstract InternalServiceKey get(@Bind("appid") String appid);



  public static class InternalServiceKeyMapper implements ResultSetMapper<InternalServiceKey> {
    @Override
    public InternalServiceKey map(int i, ResultSet resultSet, StatementContext statementContext)
            throws SQLException {
      try {
        List<String> allowedIpList = mapper.readValue(resultSet.getString(ALLOWED_IP_LIST), List.class);
        return new InternalServiceKey(
                resultSet.getString(APPID),
                resultSet.getString(ALGORITHM),
                resultSet.getBytes(KEY),
                resultSet.getInt(SIGNATURE_EXPIRE_TIME),
                allowedIpList
        );
      } catch (IOException e) {
        throw new SQLException(e);
      }
    }
  }

  @BindingAnnotation(InternalServiceKeyBinder.InternalServiceKeyBinderFactory.class)
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public @interface InternalServiceKeyBinder {
    public static class InternalServiceKeyBinderFactory implements BinderFactory {
      @Override
      public Binder build(Annotation annotation) {
        return new Binder<InternalServiceKeyBinder, InternalServiceKey>() {
          @Override
          public void bind(SQLStatement<?> sql,
                           InternalServiceKeyBinder internalServiceKeyBinder,
                           InternalServiceKey internalServiceKey)
          {
            try {
              String serialized = mapper.writeValueAsString(internalServiceKey.getAllowedIPList());
              sql.bind(APPID, internalServiceKey.getAppid());
              sql.bind(ALGORITHM, internalServiceKey.getAlgorithm());
              sql.bind(KEY, internalServiceKey.getKey());
              sql.bind(SIGNATURE_EXPIRE_TIME, internalServiceKey.getSignatureExpireTime());
              sql.bind(ALLOWED_IP_LIST, serialized);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException(e);
            }
          }
        };
      }
    }
  }

}
