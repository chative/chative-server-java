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
package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.GroupMembersTable;


public class DirectoryNotifyConfiguration {

  @JsonProperty
  private long accountBasicInfoChangeTimerLockLeaseTime=300000;

  @JsonProperty
  private long accountBasicInfoChangeTimerDelay=300000;
  @JsonProperty
  private int accountBasicInfoChangeTimerPeriod=600000;

  private boolean sendDirectoryNotify;

  public long getAccountBasicInfoChangeTimerLockLeaseTime() {
    return accountBasicInfoChangeTimerLockLeaseTime;
  }

  public void setAccountBasicInfoChangeTimerLockLeaseTime(long accountBasicInfoChangeTimerLockLeaseTime) {
    this.accountBasicInfoChangeTimerLockLeaseTime = accountBasicInfoChangeTimerLockLeaseTime;
  }

  public long getAccountBasicInfoChangeTimerDelay() {
    return accountBasicInfoChangeTimerDelay;
  }

  public void setAccountBasicInfoChangeTimerDelay(long accountBasicInfoChangeTimerDelay) {
    this.accountBasicInfoChangeTimerDelay = accountBasicInfoChangeTimerDelay;
  }

  public int getAccountBasicInfoChangeTimerPeriod() {
    return accountBasicInfoChangeTimerPeriod;
  }

  public void setAccountBasicInfoChangeTimerPeriod(int accountBasicInfoChangeTimerPeriod) {
    this.accountBasicInfoChangeTimerPeriod = accountBasicInfoChangeTimerPeriod;
  }

  public boolean isSendDirectoryNotify() {
    return sendDirectoryNotify;
  }

  public void setSendDirectoryNotify(boolean sendDirectoryNotify) {
    this.sendDirectoryNotify = sendDirectoryNotify;
  }
}
