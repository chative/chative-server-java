// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UserStatusService.proto

package org.whispersystems.textsecuregcm.internal.status;

public final class UserStatusServiceOuterClass {
  private UserStatusServiceOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_userstatus_UserStatusResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_userstatus_UserStatusResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_userstatus_UserStatusRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_userstatus_UserStatusRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027UserStatusService.proto\022\nuserstatus\032\014c" +
      "ommon.proto\"#\n\022UserStatusResponse\022\r\n\005isD" +
      "ND\030\001 \002(\010\"#\n\021UserStatusRequest\022\016\n\006number\030" +
      "\001 \002(\t2J\n\021UserStatusService\0225\n\005isDND\022\035.us" +
      "erstatus.UserStatusRequest\032\r.BaseRespons" +
      "eB4\n0org.whispersystems.textsecuregcm.in" +
      "ternal.statusP\001"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.whispersystems.textsecuregcm.internal.common.Common.getDescriptor(),
        });
    internal_static_userstatus_UserStatusResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_userstatus_UserStatusResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_userstatus_UserStatusResponse_descriptor,
        new java.lang.String[] { "IsDND", });
    internal_static_userstatus_UserStatusRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_userstatus_UserStatusRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_userstatus_UserStatusRequest_descriptor,
        new java.lang.String[] { "Number", });
    org.whispersystems.textsecuregcm.internal.common.Common.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
