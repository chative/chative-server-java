// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GroupService.proto

package org.whispersystems.textsecuregcm.internal.groups;

public interface GroupsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:GroupsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional .Step step = 1;</code>
   */
  boolean hasStep();
  /**
   * <code>optional .Step step = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.common.Step getStep();
  /**
   * <code>optional .Step step = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.common.StepOrBuilder getStepOrBuilder();

  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  boolean hasGroupInfo();
  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupInfo();
  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupInfoOrBuilder();
}
