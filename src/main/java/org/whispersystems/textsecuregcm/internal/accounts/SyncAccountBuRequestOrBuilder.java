// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

public interface SyncAccountBuRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:SyncAccountBuRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string oktaOrg = 1;</code>
   */
  boolean hasOktaOrg();
  /**
   * <code>required string oktaOrg = 1;</code>
   */
  java.lang.String getOktaOrg();
  /**
   * <code>required string oktaOrg = 1;</code>
   */
  com.google.protobuf.ByteString
      getOktaOrgBytes();

  /**
   * <code>map&lt;string, string&gt; buInfo = 2;</code>
   */
  int getBuInfoCount();
  /**
   * <code>map&lt;string, string&gt; buInfo = 2;</code>
   */
  boolean containsBuInfo(
      java.lang.String key);
  /**
   * Use {@link #getBuInfoMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getBuInfo();
  /**
   * <code>map&lt;string, string&gt; buInfo = 2;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getBuInfoMap();
  /**
   * <code>map&lt;string, string&gt; buInfo = 2;</code>
   */

  java.lang.String getBuInfoOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; buInfo = 2;</code>
   */

  java.lang.String getBuInfoOrThrow(
      java.lang.String key);
}
