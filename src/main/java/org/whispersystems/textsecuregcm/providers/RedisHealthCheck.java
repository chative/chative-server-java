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
package org.whispersystems.textsecuregcm.providers;

import com.codahale.metrics.health.HealthCheck;

import org.whispersystems.textsecuregcm.storage.MemCache;

public class RedisHealthCheck extends HealthCheck {

  private final MemCache memCache;

  public RedisHealthCheck(MemCache memCache) {
    this.memCache = memCache;
  }

  @Override
  protected Result check() throws Exception {
    memCache.set("HEALTH", "test");

    if (!"test".equals(memCache.get("HEALTH"))) {
      return Result.unhealthy("fetch failed");
    }
    return Result.healthy();
  }
}
