package org.whispersystems.textsecuregcm.internal.groups;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: GroupService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GroupServiceGrpc {

  private GroupServiceGrpc() {}

  public static final String SERVICE_NAME = "GroupService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickoutAllGroupForUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "kickoutAllGroupForUser",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickoutAllGroupForUserMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickoutAllGroupForUserMethod;
    if ((getKickoutAllGroupForUserMethod = GroupServiceGrpc.getKickoutAllGroupForUserMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getKickoutAllGroupForUserMethod = GroupServiceGrpc.getKickoutAllGroupForUserMethod) == null) {
          GroupServiceGrpc.getKickoutAllGroupForUserMethod = getKickoutAllGroupForUserMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "kickoutAllGroupForUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("kickoutAllGroupForUser"))
              .build();
        }
      }
    }
    return getKickoutAllGroupForUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> getGetAllMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getAll",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.groups.GroupsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> getGetAllMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> getGetAllMethod;
    if ((getGetAllMethod = GroupServiceGrpc.getGetAllMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getGetAllMethod = GroupServiceGrpc.getGetAllMethod) == null) {
          GroupServiceGrpc.getGetAllMethod = getGetAllMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.groups.GroupsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getAll"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("getAll"))
              .build();
        }
      }
    }
    return getGetAllMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "create",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateMethod;
    if ((getCreateMethod = GroupServiceGrpc.getCreateMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getCreateMethod = GroupServiceGrpc.getCreateMethod) == null) {
          GroupServiceGrpc.getCreateMethod = getCreateMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "create"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("create"))
              .build();
        }
      }
    }
    return getCreateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "join",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod;
    if ((getJoinMethod = GroupServiceGrpc.getJoinMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getJoinMethod = GroupServiceGrpc.getJoinMethod) == null) {
          GroupServiceGrpc.getJoinMethod = getJoinMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "join"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("join"))
              .build();
        }
      }
    }
    return getJoinMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "leave",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod;
    if ((getLeaveMethod = GroupServiceGrpc.getLeaveMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getLeaveMethod = GroupServiceGrpc.getLeaveMethod) == null) {
          GroupServiceGrpc.getLeaveMethod = getLeaveMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "leave"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("leave"))
              .build();
        }
      }
    }
    return getLeaveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "edit",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod;
    if ((getEditMethod = GroupServiceGrpc.getEditMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getEditMethod = GroupServiceGrpc.getEditMethod) == null) {
          GroupServiceGrpc.getEditMethod = getEditMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "edit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("edit"))
              .build();
        }
      }
    }
    return getEditMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getAnnouncementMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "announcement",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getAnnouncementMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getAnnouncementMethod;
    if ((getAnnouncementMethod = GroupServiceGrpc.getAnnouncementMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getAnnouncementMethod = GroupServiceGrpc.getAnnouncementMethod) == null) {
          GroupServiceGrpc.getAnnouncementMethod = getAnnouncementMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "announcement"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("announcement"))
              .build();
        }
      }
    }
    return getAnnouncementMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetGroupMembersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getGroupMembers",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetGroupMembersMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetGroupMembersMethod;
    if ((getGetGroupMembersMethod = GroupServiceGrpc.getGetGroupMembersMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getGetGroupMembersMethod = GroupServiceGrpc.getGetGroupMembersMethod) == null) {
          GroupServiceGrpc.getGetGroupMembersMethod = getGetGroupMembersMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getGroupMembers"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("getGroupMembers"))
              .build();
        }
      }
    }
    return getGetGroupMembersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetMyGroupsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getMyGroups",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetMyGroupsMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetMyGroupsMethod;
    if ((getGetMyGroupsMethod = GroupServiceGrpc.getGetMyGroupsMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getGetMyGroupsMethod = GroupServiceGrpc.getGetMyGroupsMethod) == null) {
          GroupServiceGrpc.getGetMyGroupsMethod = getGetMyGroupsMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getMyGroups"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("getMyGroups"))
              .build();
        }
      }
    }
    return getGetMyGroupsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getChangeRoleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "changeRole",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getChangeRoleMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getChangeRoleMethod;
    if ((getChangeRoleMethod = GroupServiceGrpc.getChangeRoleMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getChangeRoleMethod = GroupServiceGrpc.getChangeRoleMethod) == null) {
          GroupServiceGrpc.getChangeRoleMethod = getChangeRoleMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "changeRole"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("changeRole"))
              .build();
        }
      }
    }
    return getChangeRoleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBulkJoinMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "bulkJoin",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBulkJoinMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBulkJoinMethod;
    if ((getBulkJoinMethod = GroupServiceGrpc.getBulkJoinMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getBulkJoinMethod = GroupServiceGrpc.getBulkJoinMethod) == null) {
          GroupServiceGrpc.getBulkJoinMethod = getBulkJoinMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "bulkJoin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("bulkJoin"))
              .build();
        }
      }
    }
    return getBulkJoinMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "uploadAvatar",
      requestType = org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod;
    if ((getUploadAvatarMethod = GroupServiceGrpc.getUploadAvatarMethod) == null) {
      synchronized (GroupServiceGrpc.class) {
        if ((getUploadAvatarMethod = GroupServiceGrpc.getUploadAvatarMethod) == null) {
          GroupServiceGrpc.getUploadAvatarMethod = getUploadAvatarMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "uploadAvatar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GroupServiceMethodDescriptorSupplier("uploadAvatar"))
              .build();
        }
      }
    }
    return getUploadAvatarMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GroupServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GroupServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GroupServiceStub>() {
        @java.lang.Override
        public GroupServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GroupServiceStub(channel, callOptions);
        }
      };
    return GroupServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GroupServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GroupServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GroupServiceBlockingStub>() {
        @java.lang.Override
        public GroupServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GroupServiceBlockingStub(channel, callOptions);
        }
      };
    return GroupServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GroupServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GroupServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GroupServiceFutureStub>() {
        @java.lang.Override
        public GroupServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GroupServiceFutureStub(channel, callOptions);
        }
      };
    return GroupServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class GroupServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void kickoutAllGroupForUser(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getKickoutAllGroupForUserMethod(), responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllMethod(), responseObserver);
    }

    /**
     */
    public void create(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateMethod(), responseObserver);
    }

    /**
     */
    public void join(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getJoinMethod(), responseObserver);
    }

    /**
     */
    public void leave(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLeaveMethod(), responseObserver);
    }

    /**
     */
    public void edit(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEditMethod(), responseObserver);
    }

    /**
     */
    public void announcement(org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnnouncementMethod(), responseObserver);
    }

    /**
     */
    public void getGroupMembers(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetGroupMembersMethod(), responseObserver);
    }

    /**
     */
    public void getMyGroups(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMyGroupsMethod(), responseObserver);
    }

    /**
     */
    public void changeRole(org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getChangeRoleMethod(), responseObserver);
    }

    /**
     */
    public void bulkJoin(org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBulkJoinMethod(), responseObserver);
    }

    /**
     */
    public void uploadAvatar(org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUploadAvatarMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getKickoutAllGroupForUserMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_KICKOUT_ALL_GROUP_FOR_USER)))
          .addMethod(
            getGetAllMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
                org.whispersystems.textsecuregcm.internal.groups.GroupsResponse>(
                  this, METHODID_GET_ALL)))
          .addMethod(
            getCreateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CREATE)))
          .addMethod(
            getJoinMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_JOIN)))
          .addMethod(
            getLeaveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_LEAVE)))
          .addMethod(
            getEditMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_EDIT)))
          .addMethod(
            getAnnouncementMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_ANNOUNCEMENT)))
          .addMethod(
            getGetGroupMembersMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GET_GROUP_MEMBERS)))
          .addMethod(
            getGetMyGroupsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GET_MY_GROUPS)))
          .addMethod(
            getChangeRoleMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CHANGE_ROLE)))
          .addMethod(
            getBulkJoinMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_BULK_JOIN)))
          .addMethod(
            getUploadAvatarMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_UPLOAD_AVATAR)))
          .build();
    }
  }

  /**
   */
  public static final class GroupServiceStub extends io.grpc.stub.AbstractAsyncStub<GroupServiceStub> {
    private GroupServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GroupServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GroupServiceStub(channel, callOptions);
    }

    /**
     */
    public void kickoutAllGroupForUser(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getKickoutAllGroupForUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void create(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void join(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getJoinMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void leave(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void edit(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEditMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void announcement(org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnnouncementMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getGroupMembers(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetGroupMembersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMyGroups(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMyGroupsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void changeRole(org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getChangeRoleMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bulkJoin(org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBulkJoinMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void uploadAvatar(org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUploadAvatarMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GroupServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<GroupServiceBlockingStub> {
    private GroupServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GroupServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GroupServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse kickoutAllGroupForUser(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getKickoutAllGroupForUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsResponse getAll(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse create(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse join(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getJoinMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse leave(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLeaveMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse edit(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEditMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse announcement(org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnnouncementMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse getGroupMembers(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetGroupMembersMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse getMyGroups(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMyGroupsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse changeRole(org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getChangeRoleMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse bulkJoin(org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBulkJoinMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse uploadAvatar(org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUploadAvatarMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GroupServiceFutureStub extends io.grpc.stub.AbstractFutureStub<GroupServiceFutureStub> {
    private GroupServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GroupServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GroupServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> kickoutAllGroupForUser(
        org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getKickoutAllGroupForUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.groups.GroupsResponse> getAll(
        org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> create(
        org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> join(
        org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getJoinMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> leave(
        org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> edit(
        org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEditMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> announcement(
        org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnnouncementMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGroupMembers(
        org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetGroupMembersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> getMyGroups(
        org.whispersystems.textsecuregcm.internal.groups.GroupsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMyGroupsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> changeRole(
        org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getChangeRoleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> bulkJoin(
        org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBulkJoinMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> uploadAvatar(
        org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUploadAvatarMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_KICKOUT_ALL_GROUP_FOR_USER = 0;
  private static final int METHODID_GET_ALL = 1;
  private static final int METHODID_CREATE = 2;
  private static final int METHODID_JOIN = 3;
  private static final int METHODID_LEAVE = 4;
  private static final int METHODID_EDIT = 5;
  private static final int METHODID_ANNOUNCEMENT = 6;
  private static final int METHODID_GET_GROUP_MEMBERS = 7;
  private static final int METHODID_GET_MY_GROUPS = 8;
  private static final int METHODID_CHANGE_ROLE = 9;
  private static final int METHODID_BULK_JOIN = 10;
  private static final int METHODID_UPLOAD_AVATAR = 11;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GroupServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GroupServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_KICKOUT_ALL_GROUP_FOR_USER:
          serviceImpl.kickoutAllGroupForUser((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_ALL:
          serviceImpl.getAll((org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.groups.GroupsResponse>) responseObserver);
          break;
        case METHODID_CREATE:
          serviceImpl.create((org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_JOIN:
          serviceImpl.join((org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_LEAVE:
          serviceImpl.leave((org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_EDIT:
          serviceImpl.edit((org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_ANNOUNCEMENT:
          serviceImpl.announcement((org.whispersystems.textsecuregcm.internal.groups.GroupsAnnouncementRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_GROUP_MEMBERS:
          serviceImpl.getGroupMembers((org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_MY_GROUPS:
          serviceImpl.getMyGroups((org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_CHANGE_ROLE:
          serviceImpl.changeRole((org.whispersystems.textsecuregcm.internal.groups.GroupMembersResponse) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_BULK_JOIN:
          serviceImpl.bulkJoin((org.whispersystems.textsecuregcm.internal.groups.GroupJoinRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_UPLOAD_AVATAR:
          serviceImpl.uploadAvatar((org.whispersystems.textsecuregcm.internal.groups.GroupAvatarRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GroupServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GroupServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GroupService");
    }
  }

  private static final class GroupServiceFileDescriptorSupplier
      extends GroupServiceBaseDescriptorSupplier {
    GroupServiceFileDescriptorSupplier() {}
  }

  private static final class GroupServiceMethodDescriptorSupplier
      extends GroupServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GroupServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GroupServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GroupServiceFileDescriptorSupplier())
              .addMethod(getKickoutAllGroupForUserMethod())
              .addMethod(getGetAllMethod())
              .addMethod(getCreateMethod())
              .addMethod(getJoinMethod())
              .addMethod(getLeaveMethod())
              .addMethod(getEditMethod())
              .addMethod(getAnnouncementMethod())
              .addMethod(getGetGroupMembersMethod())
              .addMethod(getGetMyGroupsMethod())
              .addMethod(getChangeRoleMethod())
              .addMethod(getBulkJoinMethod())
              .addMethod(getUploadAvatarMethod())
              .build();
        }
      }
    }
    return result;
  }
}
