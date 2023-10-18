/**
 * Copyright (C) 2014 Open WhisperSystems
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.auth.StoredVerificationCode;
import org.whispersystems.textsecuregcm.util.SystemMapper;
import java.util.Optional;

public class PendingDevicesManager {

  private final Logger logger = LoggerFactory.getLogger(PendingDevicesManager.class);

  private static final String CACHE_PREFIX = "pending_devices2::";

  private final PendingDevices      pendingDevices;
  private final MemCache memCache;
  private final ObjectMapper        mapper;

  public PendingDevicesManager(PendingDevices pendingDevices, MemCache memCache) {
    this.pendingDevices = pendingDevices;
    this.memCache    = memCache;
    this.mapper         = SystemMapper.getMapper();
  }

  public void store(String number, StoredVerificationCode code) {
    memCache.set(CACHE_PREFIX +number, code);
    pendingDevices.insert(number, code.getCode(), code.getTimestamp());
  }

  public void remove(String number) {
    memCache.remove(CACHE_PREFIX+number);
    pendingDevices.remove(number);
  }

  public Optional<StoredVerificationCode> getCodeForNumber(String number) {
    Optional<StoredVerificationCode> code = memcacheGet(number);

    if (!code.isPresent()) {
      code = Optional.ofNullable(pendingDevices.getCodeForNumber(number));

      if (code.isPresent()) {
        memCache.set(CACHE_PREFIX+number, code.get());
      }
    }

    return code;
  }


  private Optional<StoredVerificationCode> memcacheGet(String number) {
    StoredVerificationCode storedVerificationCode = (StoredVerificationCode) memCache.get(CACHE_PREFIX + number,StoredVerificationCode.class);
    if (storedVerificationCode == null) return Optional.empty();
    else              return Optional.of(storedVerificationCode);
  }


}
