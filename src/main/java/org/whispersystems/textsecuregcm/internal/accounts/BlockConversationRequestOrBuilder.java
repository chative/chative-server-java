// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

public interface BlockConversationRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:BlockConversationRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string operator = 1;</code>
   */
  boolean hasOperator();
  /**
   * <code>required string operator = 1;</code>
   */
  java.lang.String getOperator();
  /**
   * <code>required string operator = 1;</code>
   */
  com.google.protobuf.ByteString
      getOperatorBytes();

  /**
   * <code>required string conversationId = 2;</code>
   */
  boolean hasConversationId();
  /**
   * <code>required string conversationId = 2;</code>
   */
  java.lang.String getConversationId();
  /**
   * <code>required string conversationId = 2;</code>
   */
  com.google.protobuf.ByteString
      getConversationIdBytes();

  /**
   * <code>required int32 block = 3;</code>
   */
  boolean hasBlock();
  /**
   * <code>required int32 block = 3;</code>
   */
  int getBlock();
}
