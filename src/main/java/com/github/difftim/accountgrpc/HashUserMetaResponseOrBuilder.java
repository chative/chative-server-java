// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: account.proto

package com.github.difftim.accountgrpc;

public interface HashUserMetaResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:pb.HashUserMetaResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  boolean hasBase();
  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  com.github.difftim.common.BaseResponse getBase();
  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  com.github.difftim.common.BaseResponseOrBuilder getBaseOrBuilder();

  /**
   * <code>string emailHash = 2;</code>
   */
  java.lang.String getEmailHash();
  /**
   * <code>string emailHash = 2;</code>
   */
  com.google.protobuf.ByteString
      getEmailHashBytes();

  /**
   * <code>string phoneHash = 3;</code>
   */
  java.lang.String getPhoneHash();
  /**
   * <code>string phoneHash = 3;</code>
   */
  com.google.protobuf.ByteString
      getPhoneHashBytes();
}
