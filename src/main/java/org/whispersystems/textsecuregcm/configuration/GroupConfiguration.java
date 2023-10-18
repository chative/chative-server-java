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


public class GroupConfiguration {

  @JsonProperty
  private int membersMaxSize;

  @JsonProperty
  private int defaultInvitationRule=2;

  @JsonProperty
  private int defaultMemberNotification=1;

  @JsonProperty
  private int pinMaxSize;

  @JsonProperty
  private long effectiveDuration;

  public int getMembersMaxSize() {
    if (membersMaxSize > 0) {
      return membersMaxSize;
    }else{
      return 100;
    }
  }

  public void setMembersMaxSize(int membersMaxSize) {
    this.membersMaxSize = membersMaxSize;
  }

  public GroupMembersTable.ROLE getDefaultInvitationRule() {
    if(GroupMembersTable.ROLE.fromOrdinal(defaultInvitationRule)!=null){
      return GroupMembersTable.ROLE.fromOrdinal(defaultInvitationRule);
    }else {
      return GroupMembersTable.ROLE.fromOrdinal(2);
    }
  }

  public void setDefaultInvitationRule(int defaultInvitationRule) {
    this.defaultInvitationRule = defaultInvitationRule;
  }

  public GroupMembersTable.NOTIFICATION getDefaultMemberNotification() {
    if(GroupMembersTable.NOTIFICATION.fromOrdinal(defaultMemberNotification)!=null) {
      return GroupMembersTable.NOTIFICATION.fromOrdinal(defaultMemberNotification);
    }else{
      return GroupMembersTable.NOTIFICATION.fromOrdinal(1);
    }
  }

  public void setDefaultMemberNotification(int defaultMemberNotification) {
    this.defaultMemberNotification = defaultMemberNotification;
  }

  public long getEffectiveDuration() {
    return effectiveDuration;
  }

  public void setEffectiveDuration(long effectiveDuration) {
    this.effectiveDuration = effectiveDuration;
  }

  public int getPinMaxSize() {
    if (pinMaxSize > 0) {
      return pinMaxSize;
    }else{
      return 100;
    }
  }

  public void setPinMaxSize(int pinMaxSize) {
    this.pinMaxSize = pinMaxSize;
  }

  public static void main(String[] args) {
    GroupConfiguration groupConfiguration=new GroupConfiguration();
    System.out.println(groupConfiguration.getMembersMaxSize());
    System.out.println(groupConfiguration.getDefaultInvitationRule());
    System.out.println(groupConfiguration.getDefaultMemberNotification());
    groupConfiguration.setMembersMaxSize(1);
    groupConfiguration.setDefaultInvitationRule(1);
    groupConfiguration.setDefaultMemberNotification(2);
    System.out.println(groupConfiguration.getMembersMaxSize());
    System.out.println(groupConfiguration.getDefaultInvitationRule());
    System.out.println(groupConfiguration.getDefaultMemberNotification());
    groupConfiguration.setMembersMaxSize(-1);
    groupConfiguration.setDefaultInvitationRule(55);
    groupConfiguration.setDefaultMemberNotification(99);
    System.out.println(groupConfiguration.getMembersMaxSize());
    System.out.println(groupConfiguration.getDefaultInvitationRule());
    System.out.println(groupConfiguration.getDefaultMemberNotification());


  }
}
