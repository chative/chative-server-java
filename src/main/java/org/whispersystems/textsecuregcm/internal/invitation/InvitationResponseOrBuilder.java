// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsInvitationService.proto

package org.whispersystems.textsecuregcm.internal.invitation;

public interface InvitationResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:InvitationResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .InvitationResponse.InvitationInfo invitationInfo = 1;</code>
   */
  java.util.List<org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.InvitationInfo> 
      getInvitationInfoList();
  /**
   * <code>repeated .InvitationResponse.InvitationInfo invitationInfo = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.InvitationInfo getInvitationInfo(int index);
  /**
   * <code>repeated .InvitationResponse.InvitationInfo invitationInfo = 1;</code>
   */
  int getInvitationInfoCount();
  /**
   * <code>repeated .InvitationResponse.InvitationInfo invitationInfo = 1;</code>
   */
  java.util.List<? extends org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.InvitationInfoOrBuilder> 
      getInvitationInfoOrBuilderList();
  /**
   * <code>repeated .InvitationResponse.InvitationInfo invitationInfo = 1;</code>
   */
  org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.InvitationInfoOrBuilder getInvitationInfoOrBuilder(
      int index);

  /**
   * <code>optional uint64 total = 2;</code>
   */
  boolean hasTotal();
  /**
   * <code>optional uint64 total = 2;</code>
   */
  long getTotal();
}
