// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: commonV3.proto

package com.github.difftim.common;

public final class CommonProto {
  private CommonProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_pb_BaseResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_pb_BaseResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016commonV3.proto\022\002pb\";\n\014BaseResponse\022\013\n\003" +
      "ver\030\001 \001(\r\022\016\n\006status\030\002 \001(\r\022\016\n\006reason\030\003 \001(" +
      "\t*\247\004\n\006STATUS\022\006\n\002OK\020\000\022\025\n\021INVALID_PARAMETE" +
      "R\020\001\022\021\n\rNO_PERMISSION\020\002\022\021\n\rNO_SUCH_GROUP\020" +
      "\003\022\030\n\024NO_SUCH_GROUP_MEMBER\020\004\022\021\n\rINVALID_T" +
      "OKEN\020\005\022\031\n\025SERVER_INTERNAL_ERROR\020\006\022\036\n\032NO_" +
      "SUCH_GROUP_ANNOUNCEMENT\020\007\022\020\n\014GROUP_EXIST" +
      "S\020\010\022\020\n\014NO_SUCH_FILE\020\t\022\034\n\030GROUP_IS_FULL_O" +
      "R_EXCEEDS\020\n\022\020\n\014NO_SUCH_USER\020\013\022\027\n\023RATE_LI" +
      "MIT_EXCEEDED\020\014\022\023\n\017INVALID_INVITER\020\r\022\024\n\020U" +
      "SER_IS_DISABLED\020\016\022\027\n\023PUID_IS_REGISTERING" +
      "\020\017\022 \n\034NUMBER_IS_BINDING_OTHER_PUID\020\020\022\024\n\020" +
      "TEAM_HAS_MEMBERS\020\021\022\022\n\016VOTE_IS_CLOSED\020\022\022\025" +
      "\n\021NO_SUCH_GROUP_PIN\020\023\022\024\n\020USER_EMAIL_EXIS" +
      "T\020\024\022\025\n\021USER_OKTAID_EXIST\020\025\022\036\n\032GROUP_PIN_" +
      "CONTENT_TOO_LONG\020\026\022\017\n\013OTHER_ERROR\020cB0\n\031c" +
      "om.github.difftim.commonB\013CommonProtoP\001Z" +
      "\004./pbb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_pb_BaseResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_pb_BaseResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_pb_BaseResponse_descriptor,
        new java.lang.String[] { "Ver", "Status", "Reason", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}