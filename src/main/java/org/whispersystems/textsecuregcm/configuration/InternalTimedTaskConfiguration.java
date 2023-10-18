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


public class InternalTimedTaskConfiguration {

  @JsonProperty
  private Integer messageExpireThreshold;

  @JsonProperty
  private Integer groupExpireThreshold;

  @JsonProperty
  private Integer deivceExpireThreshold;

  @JsonProperty
  private String emailSubject;

  @JsonProperty
  private String emailTemplatate;

  @JsonProperty
  private int messageRemindBeforeDaysStart;

  @JsonProperty
  private int messageRemindBeforeDaysEnd;

  public Integer getMessageExpireThreshold() {
    return messageExpireThreshold;
  }

  public void setMessageExpireThreshold(Integer messageExpireThreshold) {
    this.messageExpireThreshold = messageExpireThreshold;
  }

  public Integer getGroupExpireThreshold() {
    return groupExpireThreshold;
  }

  public void setGroupExpireThreshold(Integer groupExpireThreshold) {
    this.groupExpireThreshold = groupExpireThreshold;
  }

  public Integer getDeivceExpireThreshold() {
    return deivceExpireThreshold;
  }

  public void setDeivceExpireThreshold(Integer deivceExpireThreshold) {
    this.deivceExpireThreshold = deivceExpireThreshold;
  }

  public String getEmailTemplatate() {
    return emailTemplatate;
  }

  public void setEmailTemplatate(String emailTemplatate) {
    this.emailTemplatate = emailTemplatate;
  }

  public String getEmailSubject() {
    return emailSubject;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public int getMessageRemindBeforeDaysStart() {
    return messageRemindBeforeDaysStart;
  }

  public void setMessageRemindBeforeDaysStart(int messageRemindBeforeDaysStart) {
    this.messageRemindBeforeDaysStart = messageRemindBeforeDaysStart;
  }

  public int getMessageRemindBeforeDaysEnd() {
    return messageRemindBeforeDaysEnd;
  }

  public void setMessageRemindBeforeDaysEnd(int messageRemindBeforeDaysEnd) {
    this.messageRemindBeforeDaysEnd = messageRemindBeforeDaysEnd;
  }
}
