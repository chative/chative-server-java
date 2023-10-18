// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

public interface AccountCreateRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:AccountCreateRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string pid = 1;</code>
   */
  boolean hasPid();
  /**
   * <code>required string pid = 1;</code>
   */
  java.lang.String getPid();
  /**
   * <code>required string pid = 1;</code>
   */
  com.google.protobuf.ByteString
      getPidBytes();

  /**
   * <code>required string puid = 2;</code>
   */
  boolean hasPuid();
  /**
   * <code>required string puid = 2;</code>
   */
  java.lang.String getPuid();
  /**
   * <code>required string puid = 2;</code>
   */
  com.google.protobuf.ByteString
      getPuidBytes();

  /**
   * <code>required string nickname = 4;</code>
   */
  boolean hasNickname();
  /**
   * <code>required string nickname = 4;</code>
   */
  java.lang.String getNickname();
  /**
   * <code>required string nickname = 4;</code>
   */
  com.google.protobuf.ByteString
      getNicknameBytes();

  /**
   * <code>required string identityKey = 5;</code>
   */
  boolean hasIdentityKey();
  /**
   * <code>required string identityKey = 5;</code>
   */
  java.lang.String getIdentityKey();
  /**
   * <code>required string identityKey = 5;</code>
   */
  com.google.protobuf.ByteString
      getIdentityKeyBytes();

  /**
   * <code>optional string inviter = 6;</code>
   */
  boolean hasInviter();
  /**
   * <code>optional string inviter = 6;</code>
   */
  java.lang.String getInviter();
  /**
   * <code>optional string inviter = 6;</code>
   */
  com.google.protobuf.ByteString
      getInviterBytes();

  /**
   * <code>required .AccountCreateRequest.DeviceInfo deviceInfo = 7;</code>
   */
  boolean hasDeviceInfo();
  /**
   * <code>required .AccountCreateRequest.DeviceInfo deviceInfo = 7;</code>
   */
  org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest.DeviceInfo getDeviceInfo();
  /**
   * <code>required .AccountCreateRequest.DeviceInfo deviceInfo = 7;</code>
   */
  org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest.DeviceInfoOrBuilder getDeviceInfoOrBuilder();

  /**
   * <code>optional string wuid = 8;</code>
   */
  boolean hasWuid();
  /**
   * <code>optional string wuid = 8;</code>
   */
  java.lang.String getWuid();
  /**
   * <code>optional string wuid = 8;</code>
   */
  com.google.protobuf.ByteString
      getWuidBytes();

  /**
   * <pre>
   *number分段
   * </pre>
   *
   * <code>optional string segment = 9;</code>
   */
  boolean hasSegment();
  /**
   * <pre>
   *number分段
   * </pre>
   *
   * <code>optional string segment = 9;</code>
   */
  java.lang.String getSegment();
  /**
   * <pre>
   *number分段
   * </pre>
   *
   * <code>optional string segment = 9;</code>
   */
  com.google.protobuf.ByteString
      getSegmentBytes();

  /**
   * <pre>
   *0:正常收发 1：不接收消息 2：不发送消息 3：不发不收，默认0
   * </pre>
   *
   * <code>optional int32 accountMsgHandleType = 10;</code>
   */
  boolean hasAccountMsgHandleType();
  /**
   * <pre>
   *0:正常收发 1：不接收消息 2：不发送消息 3：不发不收，默认0
   * </pre>
   *
   * <code>optional int32 accountMsgHandleType = 10;</code>
   */
  int getAccountMsgHandleType();
}
