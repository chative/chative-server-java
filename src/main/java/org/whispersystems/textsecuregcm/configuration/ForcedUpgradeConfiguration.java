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
import org.hibernate.validator.constraints.NotEmpty;


public class ForcedUpgradeConfiguration {

  @JsonProperty
  private String iOSversion;

  @JsonProperty
  private String androidVersion;

  @JsonProperty
  private String macVersion;

  public String getLinuxVersion() {
    return linuxVersion;
  }

  public String getLinuxUserAgentPatternStr() {
    return linuxUserAgentPatternStr;
  }

  @JsonProperty
  private String linuxVersion;

  @JsonProperty
  private boolean upgradeSwitch;

  @JsonProperty
  private String iOSUserAgentPatternStr;

  @JsonProperty
  private String androidUserAgentPatternStr;
  @JsonProperty
  private String macUserAgentPatternStr;
  @JsonProperty
  private String linuxUserAgentPatternStr;

  public String getiOSversion() {
    return iOSversion;
  }

  public String getAndroidVersion() {
    return androidVersion;
  }

  public void setiOSversion(String iOSversion) {
    this.iOSversion = iOSversion;
  }

  public String getMacVersion() {
    return macVersion;
  }

  public void setMacVersion(String macVersion) {
    this.macVersion = macVersion;
  }

  public boolean isUpgradeSwitch() {
    return upgradeSwitch;
  }

  public void setUpgradeSwitch(boolean upgradeSwitch) {
    this.upgradeSwitch = upgradeSwitch;
  }

  public String getiOSUserAgentPatternStr() {
    return iOSUserAgentPatternStr;
  }

  public String getAndroidUserAgentPatternStr() {
    return androidUserAgentPatternStr;
  }

  public String getMacUserAgentPatternStr() {
    return macUserAgentPatternStr;
  }
}
