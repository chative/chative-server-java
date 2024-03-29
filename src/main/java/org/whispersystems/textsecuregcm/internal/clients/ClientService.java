// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: clientService.proto

package org.whispersystems.textsecuregcm.internal.clients;

public final class ClientService {
  private ClientService() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientVersionResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientVersionResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientVersionResponse_ClientVersionCnt_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientVersionResponse_ClientVersionCnt_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientVersionResponse_ClientVersionCntWithOS_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientVersionResponse_ClientVersionCntWithOS_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientVersionRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientVersionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientVersionRequest_OSVersion_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientVersionRequest_OSVersion_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientQueryRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientQueryRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ClientInfoResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ClientInfoResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023clientService.proto\032\014common.proto\"\355\001\n\025" +
      "ClientVersionResponse\022A\n\nosVersions\030\001 \003(" +
      "\0132-.ClientVersionResponse.ClientVersionC" +
      "ntWithOS\0320\n\020ClientVersionCnt\022\017\n\007version\030" +
      "\001 \002(\t\022\013\n\003cnt\030\002 \002(\r\032_\n\026ClientVersionCntWi" +
      "thOS\022\n\n\002os\030\001 \001(\t\0229\n\010Versions\030\002 \003(\0132\'.Cli" +
      "entVersionResponse.ClientVersionCnt\"x\n\024C" +
      "lientVersionRequest\0223\n\nosVersions\030\001 \003(\0132" +
      "\037.ClientVersionRequest.OSVersion\032+\n\tOSVe" +
      "rsion\022\n\n\002os\030\001 \002(\t\022\022\n\nminVersion\030\002 \002(\t\"3\n" +
      "\022ClientQueryRequest\022\013\n\003uid\030\001 \002(\t\022\020\n\010devi" +
      "ceId\030\002 \002(\t\"=\n\022ClientInfoResponse\022\n\n\002os\030\001" +
      " \001(\t\022\017\n\007version\030\002 \001(\t\022\n\n\002ua\030\003 \001(\t2\311\001\n\016Cl" +
      "ientsService\0220\n\016getVersionInfo\022\006.Empty\032\026" +
      ".ClientVersionResponse\022D\n\023getVersionInfo" +
      "ByVer\022\025.ClientVersionRequest\032\026.ClientVer" +
      "sionResponse\022?\n\023getVersionInfoByUid\022\023.Cl" +
      "ientQueryRequest\032\023.ClientInfoResponseB5\n" +
      "1org.whispersystems.textsecuregcm.intern" +
      "al.clientsP\001"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.whispersystems.textsecuregcm.internal.common.Common.getDescriptor(),
        });
    internal_static_ClientVersionResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ClientVersionResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientVersionResponse_descriptor,
        new java.lang.String[] { "OsVersions", });
    internal_static_ClientVersionResponse_ClientVersionCnt_descriptor =
      internal_static_ClientVersionResponse_descriptor.getNestedTypes().get(0);
    internal_static_ClientVersionResponse_ClientVersionCnt_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientVersionResponse_ClientVersionCnt_descriptor,
        new java.lang.String[] { "Version", "Cnt", });
    internal_static_ClientVersionResponse_ClientVersionCntWithOS_descriptor =
      internal_static_ClientVersionResponse_descriptor.getNestedTypes().get(1);
    internal_static_ClientVersionResponse_ClientVersionCntWithOS_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientVersionResponse_ClientVersionCntWithOS_descriptor,
        new java.lang.String[] { "Os", "Versions", });
    internal_static_ClientVersionRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ClientVersionRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientVersionRequest_descriptor,
        new java.lang.String[] { "OsVersions", });
    internal_static_ClientVersionRequest_OSVersion_descriptor =
      internal_static_ClientVersionRequest_descriptor.getNestedTypes().get(0);
    internal_static_ClientVersionRequest_OSVersion_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientVersionRequest_OSVersion_descriptor,
        new java.lang.String[] { "Os", "MinVersion", });
    internal_static_ClientQueryRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_ClientQueryRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientQueryRequest_descriptor,
        new java.lang.String[] { "Uid", "DeviceId", });
    internal_static_ClientInfoResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_ClientInfoResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ClientInfoResponse_descriptor,
        new java.lang.String[] { "Os", "Version", "Ua", });
    org.whispersystems.textsecuregcm.internal.common.Common.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
