// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: account.proto

package com.github.difftim.accountgrpc;

public interface DelUserHashRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:pb.DelUserHashRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string uid = 1;</code>
   */
  java.lang.String getUid();
  /**
   * <code>string uid = 1;</code>
   */
  com.google.protobuf.ByteString
      getUidBytes();

  /**
   * <code>bool delEmail = 2;</code>
   */
  boolean getDelEmail();

  /**
   * <code>bool delPhone = 3;</code>
   */
  boolean getDelPhone();
}
