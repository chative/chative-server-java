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


public class AccountManagerConfiguration {

  @JsonProperty
  private String algorithm;

  @JsonProperty
  private String key;

  @JsonProperty
  private String iv;

  @JsonProperty
  private int defaultGlobalNotification=1;

  @JsonProperty
  private int accountExpireThreshold=60;

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getIv() {
    return iv;
  }

  public void setIv(String iv) {
    this.iv = iv;
  }

  public int getDefaultGlobalNotification() {
    return defaultGlobalNotification;
  }

  public void setDefaultGlobalNotification(int defaultGlobalNotification) {
    this.defaultGlobalNotification = defaultGlobalNotification;
  }

  public int getAccountExpireThreshold() {
    return accountExpireThreshold;
  }

  public void setAccountExpireThreshold(int accountExpireThreshold) {
    if(accountExpireThreshold>=30) {
      this.accountExpireThreshold = accountExpireThreshold;
    }
  }
}
