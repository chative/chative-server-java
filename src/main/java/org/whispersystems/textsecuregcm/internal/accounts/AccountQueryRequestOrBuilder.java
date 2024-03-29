// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

public interface AccountQueryRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:AccountQueryRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string appid = 1;</code>
   */
  boolean hasAppid();
  /**
   * <code>required string appid = 1;</code>
   */
  java.lang.String getAppid();
  /**
   * <code>required string appid = 1;</code>
   */
  com.google.protobuf.ByteString
      getAppidBytes();

  /**
   * <code>repeated string emails = 2;</code>
   */
  java.util.List<java.lang.String>
      getEmailsList();
  /**
   * <code>repeated string emails = 2;</code>
   */
  int getEmailsCount();
  /**
   * <code>repeated string emails = 2;</code>
   */
  java.lang.String getEmails(int index);
  /**
   * <code>repeated string emails = 2;</code>
   */
  com.google.protobuf.ByteString
      getEmailsBytes(int index);

  /**
   * <code>repeated string wuids = 3;</code>
   */
  java.util.List<java.lang.String>
      getWuidsList();
  /**
   * <code>repeated string wuids = 3;</code>
   */
  int getWuidsCount();
  /**
   * <code>repeated string wuids = 3;</code>
   */
  java.lang.String getWuids(int index);
  /**
   * <code>repeated string wuids = 3;</code>
   */
  com.google.protobuf.ByteString
      getWuidsBytes(int index);

  /**
   * <code>optional string inviter = 4;</code>
   */
  boolean hasInviter();
  /**
   * <code>optional string inviter = 4;</code>
   */
  java.lang.String getInviter();
  /**
   * <code>optional string inviter = 4;</code>
   */
  com.google.protobuf.ByteString
      getInviterBytes();

  /**
   * <code>optional string operator = 5;</code>
   */
  boolean hasOperator();
  /**
   * <code>optional string operator = 5;</code>
   */
  java.lang.String getOperator();
  /**
   * <code>optional string operator = 5;</code>
   */
  com.google.protobuf.ByteString
      getOperatorBytes();

  /**
   * <code>optional string oktaOrg = 6;</code>
   */
  boolean hasOktaOrg();
  /**
   * <code>optional string oktaOrg = 6;</code>
   */
  java.lang.String getOktaOrg();
  /**
   * <code>optional string oktaOrg = 6;</code>
   */
  com.google.protobuf.ByteString
      getOktaOrgBytes();

  /**
   * <code>optional bool fetchAvatar = 7;</code>
   */
  boolean hasFetchAvatar();
  /**
   * <code>optional bool fetchAvatar = 7;</code>
   */
  boolean getFetchAvatar();

  /**
   * <code>optional bool fetchSig = 8;</code>
   */
  boolean hasFetchSig();
  /**
   * <code>optional bool fetchSig = 8;</code>
   */
  boolean getFetchSig();

  /**
   * <code>optional bool fetchNickname = 9;</code>
   */
  boolean hasFetchNickname();
  /**
   * <code>optional bool fetchNickname = 9;</code>
   */
  boolean getFetchNickname();

  /**
   * <code>optional bool fetchFullName = 10;</code>
   */
  boolean hasFetchFullName();
  /**
   * <code>optional bool fetchFullName = 10;</code>
   */
  boolean getFetchFullName();

  /**
   * <code>optional bool fetchEmail = 11;</code>
   */
  boolean hasFetchEmail();
  /**
   * <code>optional bool fetchEmail = 11;</code>
   */
  boolean getFetchEmail();
}
