// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalTeamsService.proto

package org.whispersystems.textsecuregcm.internal.teams;

public interface JoinLeaveRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:JoinLeaveRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .JoinLeaveRequest.JoinLeaveInfo joinleaves = 1;</code>
   */
  java.util.List<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.JoinLeaveInfo> 
      getJoinleavesList();
  /**
   * <code>repeated .JoinLeaveRequest.JoinLeaveInfo joinleaves = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.JoinLeaveInfo getJoinleaves(int index);
  /**
   * <code>repeated .JoinLeaveRequest.JoinLeaveInfo joinleaves = 1;</code>
   */
  int getJoinleavesCount();
  /**
   * <code>repeated .JoinLeaveRequest.JoinLeaveInfo joinleaves = 1;</code>
   */
  java.util.List<? extends org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.JoinLeaveInfoOrBuilder> 
      getJoinleavesOrBuilderList();
  /**
   * <code>repeated .JoinLeaveRequest.JoinLeaveInfo joinleaves = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.JoinLeaveInfoOrBuilder getJoinleavesOrBuilder(
      int index);

  /**
   * <code>optional string appid = 2;</code>
   */
  boolean hasAppid();
  /**
   * <code>optional string appid = 2;</code>
   */
  java.lang.String getAppid();
  /**
   * <code>optional string appid = 2;</code>
   */
  com.google.protobuf.ByteString
      getAppidBytes();

  /**
   * <code>optional string pid = 3;</code>
   */
  boolean hasPid();
  /**
   * <code>optional string pid = 3;</code>
   */
  java.lang.String getPid();
  /**
   * <code>optional string pid = 3;</code>
   */
  com.google.protobuf.ByteString
      getPidBytes();
}
