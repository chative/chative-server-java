// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

public interface UploadResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:UploadResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>map&lt;string, string&gt; map = 1;</code>
   */
  int getMapCount();
  /**
   * <code>map&lt;string, string&gt; map = 1;</code>
   */
  boolean containsMap(
      java.lang.String key);
  /**
   * Use {@link #getMapMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getMap();
  /**
   * <code>map&lt;string, string&gt; map = 1;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getMapMap();
  /**
   * <code>map&lt;string, string&gt; map = 1;</code>
   */

  java.lang.String getMapOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; map = 1;</code>
   */

  java.lang.String getMapOrThrow(
      java.lang.String key);
}
