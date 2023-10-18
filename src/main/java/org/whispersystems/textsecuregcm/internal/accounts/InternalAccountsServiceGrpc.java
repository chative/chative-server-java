package org.whispersystems.textsecuregcm.internal.accounts;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalAccountsService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalAccountsServiceGrpc {

  private InternalAccountsServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalAccountsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getInfo",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoMethod;
    if ((getGetInfoMethod = InternalAccountsServiceGrpc.getGetInfoMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGetInfoMethod = InternalAccountsServiceGrpc.getGetInfoMethod) == null) {
          InternalAccountsServiceGrpc.getGetInfoMethod = getGetInfoMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("getInfo"))
              .build();
        }
      }
    }
    return getGetInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoByEmailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getInfoByEmail",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoByEmailMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetInfoByEmailMethod;
    if ((getGetInfoByEmailMethod = InternalAccountsServiceGrpc.getGetInfoByEmailMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGetInfoByEmailMethod = InternalAccountsServiceGrpc.getGetInfoByEmailMethod) == null) {
          InternalAccountsServiceGrpc.getGetInfoByEmailMethod = getGetInfoByEmailMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getInfoByEmail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("getInfoByEmail"))
              .build();
        }
      }
    }
    return getGetInfoByEmailMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDisableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "disable",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDisableMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDisableMethod;
    if ((getDisableMethod = InternalAccountsServiceGrpc.getDisableMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getDisableMethod = InternalAccountsServiceGrpc.getDisableMethod) == null) {
          InternalAccountsServiceGrpc.getDisableMethod = getDisableMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "disable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("disable"))
              .build();
        }
      }
    }
    return getDisableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEnableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "enable",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEnableMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEnableMethod;
    if ((getEnableMethod = InternalAccountsServiceGrpc.getEnableMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getEnableMethod = InternalAccountsServiceGrpc.getEnableMethod) == null) {
          InternalAccountsServiceGrpc.getEnableMethod = getEnableMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "enable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("enable"))
              .build();
        }
      }
    }
    return getEnableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetAllMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getAll",
      requestType = org.whispersystems.textsecuregcm.internal.common.Step.class,
      responseType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step,
      org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetAllMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getGetAllMethod;
    if ((getGetAllMethod = InternalAccountsServiceGrpc.getGetAllMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGetAllMethod = InternalAccountsServiceGrpc.getGetAllMethod) == null) {
          InternalAccountsServiceGrpc.getGetAllMethod = getGetAllMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getAll"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("getAll"))
              .build();
        }
      }
    }
    return getGetAllMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "edit",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getEditMethod;
    if ((getEditMethod = InternalAccountsServiceGrpc.getEditMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getEditMethod = InternalAccountsServiceGrpc.getEditMethod) == null) {
          InternalAccountsServiceGrpc.getEditMethod = getEditMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "edit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("edit"))
              .build();
        }
      }
    }
    return getEditMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getRenewMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "renew",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getRenewMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getRenewMethod;
    if ((getRenewMethod = InternalAccountsServiceGrpc.getRenewMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getRenewMethod = InternalAccountsServiceGrpc.getRenewMethod) == null) {
          InternalAccountsServiceGrpc.getRenewMethod = getRenewMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "renew"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("renew"))
              .build();
        }
      }
    }
    return getRenewMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createAccount",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateAccountMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateAccountMethod;
    if ((getCreateAccountMethod = InternalAccountsServiceGrpc.getCreateAccountMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getCreateAccountMethod = InternalAccountsServiceGrpc.getCreateAccountMethod) == null) {
          InternalAccountsServiceGrpc.getCreateAccountMethod = getCreateAccountMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("createAccount"))
              .build();
        }
      }
    }
    return getCreateAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getQueryAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "queryAccount",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getQueryAccountMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getQueryAccountMethod;
    if ((getQueryAccountMethod = InternalAccountsServiceGrpc.getQueryAccountMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getQueryAccountMethod = InternalAccountsServiceGrpc.getQueryAccountMethod) == null) {
          InternalAccountsServiceGrpc.getQueryAccountMethod = getQueryAccountMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("queryAccount"))
              .build();
        }
      }
    }
    return getQueryAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "upload",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UploadRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadMethod;
    if ((getUploadMethod = InternalAccountsServiceGrpc.getUploadMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getUploadMethod = InternalAccountsServiceGrpc.getUploadMethod) == null) {
          InternalAccountsServiceGrpc.getUploadMethod = getUploadMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UploadRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "upload"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UploadRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("upload"))
              .build();
        }
      }
    }
    return getUploadMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "uploadAvatar",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUploadAvatarMethod;
    if ((getUploadAvatarMethod = InternalAccountsServiceGrpc.getUploadAvatarMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getUploadAvatarMethod = InternalAccountsServiceGrpc.getUploadAvatarMethod) == null) {
          InternalAccountsServiceGrpc.getUploadAvatarMethod = getUploadAvatarMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "uploadAvatar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("uploadAvatar"))
              .build();
        }
      }
    }
    return getUploadAvatarMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickOffDeviceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "kickOffDevice",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickOffDeviceMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getKickOffDeviceMethod;
    if ((getKickOffDeviceMethod = InternalAccountsServiceGrpc.getKickOffDeviceMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getKickOffDeviceMethod = InternalAccountsServiceGrpc.getKickOffDeviceMethod) == null) {
          InternalAccountsServiceGrpc.getKickOffDeviceMethod = getKickOffDeviceMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "kickOffDevice"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("kickOffDevice"))
              .build();
        }
      }
    }
    return getKickOffDeviceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.TeamRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetUserTeamsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getUserTeams",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.TeamRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.TeamRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetUserTeamsMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.TeamRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetUserTeamsMethod;
    if ((getGetUserTeamsMethod = InternalAccountsServiceGrpc.getGetUserTeamsMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGetUserTeamsMethod = InternalAccountsServiceGrpc.getGetUserTeamsMethod) == null) {
          InternalAccountsServiceGrpc.getGetUserTeamsMethod = getGetUserTeamsMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.TeamRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getUserTeams"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.TeamRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("getUserTeams"))
              .build();
        }
      }
    }
    return getGetUserTeamsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSyncAccountBuInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "syncAccountBuInfo",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSyncAccountBuInfoMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSyncAccountBuInfoMethod;
    if ((getSyncAccountBuInfoMethod = InternalAccountsServiceGrpc.getSyncAccountBuInfoMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getSyncAccountBuInfoMethod = InternalAccountsServiceGrpc.getSyncAccountBuInfoMethod) == null) {
          InternalAccountsServiceGrpc.getSyncAccountBuInfoMethod = getSyncAccountBuInfoMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "syncAccountBuInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("syncAccountBuInfo"))
              .build();
        }
      }
    }
    return getSyncAccountBuInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "inactive",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveMethod;
    if ((getInactiveMethod = InternalAccountsServiceGrpc.getInactiveMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getInactiveMethod = InternalAccountsServiceGrpc.getInactiveMethod) == null) {
          InternalAccountsServiceGrpc.getInactiveMethod = getInactiveMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.UidsRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "inactive"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("inactive"))
              .build();
        }
      }
    }
    return getInactiveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDownloadAvatarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "downloadAvatar",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDownloadAvatarMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDownloadAvatarMethod;
    if ((getDownloadAvatarMethod = InternalAccountsServiceGrpc.getDownloadAvatarMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getDownloadAvatarMethod = InternalAccountsServiceGrpc.getDownloadAvatarMethod) == null) {
          InternalAccountsServiceGrpc.getDownloadAvatarMethod = getDownloadAvatarMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "downloadAvatar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("downloadAvatar"))
              .build();
        }
      }
    }
    return getDownloadAvatarMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenLoginInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "genLoginInfo",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenLoginInfoMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenLoginInfoMethod;
    if ((getGenLoginInfoMethod = InternalAccountsServiceGrpc.getGenLoginInfoMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGenLoginInfoMethod = InternalAccountsServiceGrpc.getGenLoginInfoMethod) == null) {
          InternalAccountsServiceGrpc.getGenLoginInfoMethod = getGenLoginInfoMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "genLoginInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("genLoginInfo"))
              .build();
        }
      }
    }
    return getGenLoginInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBlockConversationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "blockConversation",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBlockConversationMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getBlockConversationMethod;
    if ((getBlockConversationMethod = InternalAccountsServiceGrpc.getBlockConversationMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getBlockConversationMethod = InternalAccountsServiceGrpc.getBlockConversationMethod) == null) {
          InternalAccountsServiceGrpc.getBlockConversationMethod = getBlockConversationMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "blockConversation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("blockConversation"))
              .build();
        }
      }
    }
    return getBlockConversationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetConversationBlockStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getConversationBlockStatus",
      requestType = org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetConversationBlockStatusMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGetConversationBlockStatusMethod;
    if ((getGetConversationBlockStatusMethod = InternalAccountsServiceGrpc.getGetConversationBlockStatusMethod) == null) {
      synchronized (InternalAccountsServiceGrpc.class) {
        if ((getGetConversationBlockStatusMethod = InternalAccountsServiceGrpc.getGetConversationBlockStatusMethod) == null) {
          InternalAccountsServiceGrpc.getGetConversationBlockStatusMethod = getGetConversationBlockStatusMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getConversationBlockStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsServiceMethodDescriptorSupplier("getConversationBlockStatus"))
              .build();
        }
      }
    }
    return getGetConversationBlockStatusMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalAccountsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceStub>() {
        @java.lang.Override
        public InternalAccountsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsServiceStub(channel, callOptions);
        }
      };
    return InternalAccountsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalAccountsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceBlockingStub>() {
        @java.lang.Override
        public InternalAccountsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalAccountsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalAccountsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsServiceFutureStub>() {
        @java.lang.Override
        public InternalAccountsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsServiceFutureStub(channel, callOptions);
        }
      };
    return InternalAccountsServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalAccountsServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getInfo(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInfoMethod(), responseObserver);
    }

    /**
     */
    public void getInfoByEmail(org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInfoByEmailMethod(), responseObserver);
    }

    /**
     */
    public void disable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDisableMethod(), responseObserver);
    }

    /**
     */
    public void enable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEnableMethod(), responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.common.Step request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllMethod(), responseObserver);
    }

    /**
     */
    public void edit(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEditMethod(), responseObserver);
    }

    /**
     */
    public void renew(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRenewMethod(), responseObserver);
    }

    /**
     */
    public void createAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateAccountMethod(), responseObserver);
    }

    /**
     */
    public void queryAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryAccountMethod(), responseObserver);
    }

    /**
     */
    public void upload(org.whispersystems.textsecuregcm.internal.accounts.UploadRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUploadMethod(), responseObserver);
    }

    /**
     */
    public void uploadAvatar(org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUploadAvatarMethod(), responseObserver);
    }

    /**
     */
    public void kickOffDevice(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getKickOffDeviceMethod(), responseObserver);
    }

    /**
     */
    public void getUserTeams(org.whispersystems.textsecuregcm.internal.accounts.TeamRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserTeamsMethod(), responseObserver);
    }

    /**
     */
    public void syncAccountBuInfo(org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSyncAccountBuInfoMethod(), responseObserver);
    }

    /**
     */
    public void inactive(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInactiveMethod(), responseObserver);
    }

    /**
     */
    public void downloadAvatar(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDownloadAvatarMethod(), responseObserver);
    }

    /**
     */
    public void genLoginInfo(org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenLoginInfoMethod(), responseObserver);
    }

    /**
     */
    public void blockConversation(org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBlockConversationMethod(), responseObserver);
    }

    /**
     */
    public void getConversationBlockStatus(org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetConversationBlockStatusMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>(
                  this, METHODID_GET_INFO)))
          .addMethod(
            getGetInfoByEmailMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest,
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>(
                  this, METHODID_GET_INFO_BY_EMAIL)))
          .addMethod(
            getDisableMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_DISABLE)))
          .addMethod(
            getEnableMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_ENABLE)))
          .addMethod(
            getGetAllMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Step,
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>(
                  this, METHODID_GET_ALL)))
          .addMethod(
            getEditMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_EDIT)))
          .addMethod(
            getRenewMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_RENEW)))
          .addMethod(
            getCreateAccountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CREATE_ACCOUNT)))
          .addMethod(
            getQueryAccountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_QUERY_ACCOUNT)))
          .addMethod(
            getUploadMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UploadRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_UPLOAD)))
          .addMethod(
            getUploadAvatarMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_UPLOAD_AVATAR)))
          .addMethod(
            getKickOffDeviceMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_KICK_OFF_DEVICE)))
          .addMethod(
            getGetUserTeamsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.TeamRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GET_USER_TEAMS)))
          .addMethod(
            getSyncAccountBuInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_SYNC_ACCOUNT_BU_INFO)))
          .addMethod(
            getInactiveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.UidsRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_INACTIVE)))
          .addMethod(
            getDownloadAvatarMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_DOWNLOAD_AVATAR)))
          .addMethod(
            getGenLoginInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GEN_LOGIN_INFO)))
          .addMethod(
            getBlockConversationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_BLOCK_CONVERSATION)))
          .addMethod(
            getGetConversationBlockStatusMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GET_CONVERSATION_BLOCK_STATUS)))
          .build();
    }
  }

  /**
   */
  public static final class InternalAccountsServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalAccountsServiceStub> {
    private InternalAccountsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsServiceStub(channel, callOptions);
    }

    /**
     */
    public void getInfo(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getInfoByEmail(org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInfoByEmailMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void disable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDisableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void enable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEnableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.common.Step request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void edit(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEditMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void renew(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRenewMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void upload(org.whispersystems.textsecuregcm.internal.accounts.UploadRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUploadMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void uploadAvatar(org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUploadAvatarMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void kickOffDevice(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getKickOffDeviceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUserTeams(org.whispersystems.textsecuregcm.internal.accounts.TeamRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserTeamsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void syncAccountBuInfo(org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSyncAccountBuInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void inactive(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInactiveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void downloadAvatar(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDownloadAvatarMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void genLoginInfo(org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenLoginInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void blockConversation(org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBlockConversationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getConversationBlockStatus(org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetConversationBlockStatusMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalAccountsServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalAccountsServiceBlockingStub> {
    private InternalAccountsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse getInfo(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse getInfoByEmail(org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInfoByEmailMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse disable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDisableMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse enable(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEnableMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse getAll(org.whispersystems.textsecuregcm.internal.common.Step request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse edit(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEditMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse renew(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRenewMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse createAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateAccountMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse queryAccount(org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryAccountMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse upload(org.whispersystems.textsecuregcm.internal.accounts.UploadRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUploadMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse uploadAvatar(org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUploadAvatarMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse kickOffDevice(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getKickOffDeviceMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse getUserTeams(org.whispersystems.textsecuregcm.internal.accounts.TeamRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserTeamsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse syncAccountBuInfo(org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSyncAccountBuInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse inactive(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInactiveMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse downloadAvatar(org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDownloadAvatarMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse genLoginInfo(org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenLoginInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse blockConversation(org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBlockConversationMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse getConversationBlockStatus(org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetConversationBlockStatusMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalAccountsServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalAccountsServiceFutureStub> {
    private InternalAccountsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getInfo(
        org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getInfoByEmail(
        org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInfoByEmailMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> disable(
        org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDisableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> enable(
        org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEnableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse> getAll(
        org.whispersystems.textsecuregcm.internal.common.Step request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> edit(
        org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEditMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> renew(
        org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRenewMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> createAccount(
        org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateAccountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> queryAccount(
        org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryAccountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> upload(
        org.whispersystems.textsecuregcm.internal.accounts.UploadRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUploadMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> uploadAvatar(
        org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUploadAvatarMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> kickOffDevice(
        org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getKickOffDeviceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUserTeams(
        org.whispersystems.textsecuregcm.internal.accounts.TeamRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserTeamsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> syncAccountBuInfo(
        org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSyncAccountBuInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> inactive(
        org.whispersystems.textsecuregcm.internal.accounts.UidsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInactiveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> downloadAvatar(
        org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDownloadAvatarMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> genLoginInfo(
        org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenLoginInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> blockConversation(
        org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBlockConversationMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> getConversationBlockStatus(
        org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetConversationBlockStatusMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_INFO = 0;
  private static final int METHODID_GET_INFO_BY_EMAIL = 1;
  private static final int METHODID_DISABLE = 2;
  private static final int METHODID_ENABLE = 3;
  private static final int METHODID_GET_ALL = 4;
  private static final int METHODID_EDIT = 5;
  private static final int METHODID_RENEW = 6;
  private static final int METHODID_CREATE_ACCOUNT = 7;
  private static final int METHODID_QUERY_ACCOUNT = 8;
  private static final int METHODID_UPLOAD = 9;
  private static final int METHODID_UPLOAD_AVATAR = 10;
  private static final int METHODID_KICK_OFF_DEVICE = 11;
  private static final int METHODID_GET_USER_TEAMS = 12;
  private static final int METHODID_SYNC_ACCOUNT_BU_INFO = 13;
  private static final int METHODID_INACTIVE = 14;
  private static final int METHODID_DOWNLOAD_AVATAR = 15;
  private static final int METHODID_GEN_LOGIN_INFO = 16;
  private static final int METHODID_BLOCK_CONVERSATION = 17;
  private static final int METHODID_GET_CONVERSATION_BLOCK_STATUS = 18;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalAccountsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalAccountsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_INFO:
          serviceImpl.getInfo((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>) responseObserver);
          break;
        case METHODID_GET_INFO_BY_EMAIL:
          serviceImpl.getInfoByEmail((org.whispersystems.textsecuregcm.internal.accounts.EmailsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>) responseObserver);
          break;
        case METHODID_DISABLE:
          serviceImpl.disable((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_ENABLE:
          serviceImpl.enable((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_ALL:
          serviceImpl.getAll((org.whispersystems.textsecuregcm.internal.common.Step) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.accounts.AccountInfoResponse>) responseObserver);
          break;
        case METHODID_EDIT:
          serviceImpl.edit((org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_RENEW:
          serviceImpl.renew((org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_CREATE_ACCOUNT:
          serviceImpl.createAccount((org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_QUERY_ACCOUNT:
          serviceImpl.queryAccount((org.whispersystems.textsecuregcm.internal.accounts.AccountQueryRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_UPLOAD:
          serviceImpl.upload((org.whispersystems.textsecuregcm.internal.accounts.UploadRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_UPLOAD_AVATAR:
          serviceImpl.uploadAvatar((org.whispersystems.textsecuregcm.internal.accounts.UploadAvatarRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_KICK_OFF_DEVICE:
          serviceImpl.kickOffDevice((org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_USER_TEAMS:
          serviceImpl.getUserTeams((org.whispersystems.textsecuregcm.internal.accounts.TeamRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_SYNC_ACCOUNT_BU_INFO:
          serviceImpl.syncAccountBuInfo((org.whispersystems.textsecuregcm.internal.accounts.SyncAccountBuRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_INACTIVE:
          serviceImpl.inactive((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_DOWNLOAD_AVATAR:
          serviceImpl.downloadAvatar((org.whispersystems.textsecuregcm.internal.accounts.AccountInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GEN_LOGIN_INFO:
          serviceImpl.genLoginInfo((org.whispersystems.textsecuregcm.internal.accounts.LoginInfoReq) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_BLOCK_CONVERSATION:
          serviceImpl.blockConversation((org.whispersystems.textsecuregcm.internal.accounts.BlockConversationRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_CONVERSATION_BLOCK_STATUS:
          serviceImpl.getConversationBlockStatus((org.whispersystems.textsecuregcm.internal.accounts.GetConversationBlockReq) request,
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

  private static abstract class InternalAccountsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalAccountsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalAccountsService");
    }
  }

  private static final class InternalAccountsServiceFileDescriptorSupplier
      extends InternalAccountsServiceBaseDescriptorSupplier {
    InternalAccountsServiceFileDescriptorSupplier() {}
  }

  private static final class InternalAccountsServiceMethodDescriptorSupplier
      extends InternalAccountsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalAccountsServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalAccountsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalAccountsServiceFileDescriptorSupplier())
              .addMethod(getGetInfoMethod())
              .addMethod(getGetInfoByEmailMethod())
              .addMethod(getDisableMethod())
              .addMethod(getEnableMethod())
              .addMethod(getGetAllMethod())
              .addMethod(getEditMethod())
              .addMethod(getRenewMethod())
              .addMethod(getCreateAccountMethod())
              .addMethod(getQueryAccountMethod())
              .addMethod(getUploadMethod())
              .addMethod(getUploadAvatarMethod())
              .addMethod(getKickOffDeviceMethod())
              .addMethod(getGetUserTeamsMethod())
              .addMethod(getSyncAccountBuInfoMethod())
              .addMethod(getInactiveMethod())
              .addMethod(getDownloadAvatarMethod())
              .addMethod(getGenLoginInfoMethod())
              .addMethod(getBlockConversationMethod())
              .addMethod(getGetConversationBlockStatusMethod())
              .build();
        }
      }
    }
    return result;
  }
}
