// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GroupService.proto

package org.whispersystems.textsecuregcm.internal.groups;

public interface GroupsResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:GroupsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional uint64 total = 1;</code>
   */
  boolean hasTotal();
  /**
   * <code>optional uint64 total = 1;</code>
   */
  long getTotal();

  /**
   * <code>repeated .GroupsInfo groupsInfo = 2;</code>
   */
  java.util.List<org.whispersystems.textsecuregcm.internal.groups.GroupsInfo> 
      getGroupsInfoList();
  /**
   * <code>repeated .GroupsInfo groupsInfo = 2;</code>
   */
  org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupsInfo(int index);
  /**
   * <code>repeated .GroupsInfo groupsInfo = 2;</code>
   */
  int getGroupsInfoCount();
  /**
   * <code>repeated .GroupsInfo groupsInfo = 2;</code>
   */
  java.util.List<? extends org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder> 
      getGroupsInfoOrBuilderList();
  /**
   * <code>repeated .GroupsInfo groupsInfo = 2;</code>
   */
  org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupsInfoOrBuilder(
      int index);
}
