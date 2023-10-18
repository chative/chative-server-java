package com.github.difftim.accountgrpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: account.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AccountGrpc {

  private AccountGrpc() {}

  public static final String SERVICE_NAME = "pb.Account";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.SyncProfileRequest,
      com.github.difftim.accountgrpc.SyncProfileResponse> getSyncProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SyncProfile",
      requestType = com.github.difftim.accountgrpc.SyncProfileRequest.class,
      responseType = com.github.difftim.accountgrpc.SyncProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.SyncProfileRequest,
      com.github.difftim.accountgrpc.SyncProfileResponse> getSyncProfileMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.SyncProfileRequest, com.github.difftim.accountgrpc.SyncProfileResponse> getSyncProfileMethod;
    if ((getSyncProfileMethod = AccountGrpc.getSyncProfileMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getSyncProfileMethod = AccountGrpc.getSyncProfileMethod) == null) {
          AccountGrpc.getSyncProfileMethod = getSyncProfileMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.SyncProfileRequest, com.github.difftim.accountgrpc.SyncProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SyncProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.SyncProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.SyncProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("SyncProfile"))
              .build();
        }
      }
    }
    return getSyncProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GenEmailVcodeRequest,
      com.github.difftim.accountgrpc.GenEmailVcodeResponse> getGenEmailVerificationCodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenEmailVerificationCode",
      requestType = com.github.difftim.accountgrpc.GenEmailVcodeRequest.class,
      responseType = com.github.difftim.accountgrpc.GenEmailVcodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GenEmailVcodeRequest,
      com.github.difftim.accountgrpc.GenEmailVcodeResponse> getGenEmailVerificationCodeMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GenEmailVcodeRequest, com.github.difftim.accountgrpc.GenEmailVcodeResponse> getGenEmailVerificationCodeMethod;
    if ((getGenEmailVerificationCodeMethod = AccountGrpc.getGenEmailVerificationCodeMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getGenEmailVerificationCodeMethod = AccountGrpc.getGenEmailVerificationCodeMethod) == null) {
          AccountGrpc.getGenEmailVerificationCodeMethod = getGenEmailVerificationCodeMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.GenEmailVcodeRequest, com.github.difftim.accountgrpc.GenEmailVcodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenEmailVerificationCode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.GenEmailVcodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.GenEmailVcodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("GenEmailVerificationCode"))
              .build();
        }
      }
    }
    return getGenEmailVerificationCodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.CheckEmailVcodeRequest,
      com.github.difftim.accountgrpc.CheckEmailVcodeResponse> getCheckEmailVerificationCodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckEmailVerificationCode",
      requestType = com.github.difftim.accountgrpc.CheckEmailVcodeRequest.class,
      responseType = com.github.difftim.accountgrpc.CheckEmailVcodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.CheckEmailVcodeRequest,
      com.github.difftim.accountgrpc.CheckEmailVcodeResponse> getCheckEmailVerificationCodeMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.CheckEmailVcodeRequest, com.github.difftim.accountgrpc.CheckEmailVcodeResponse> getCheckEmailVerificationCodeMethod;
    if ((getCheckEmailVerificationCodeMethod = AccountGrpc.getCheckEmailVerificationCodeMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getCheckEmailVerificationCodeMethod = AccountGrpc.getCheckEmailVerificationCodeMethod) == null) {
          AccountGrpc.getCheckEmailVerificationCodeMethod = getCheckEmailVerificationCodeMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.CheckEmailVcodeRequest, com.github.difftim.accountgrpc.CheckEmailVcodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckEmailVerificationCode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.CheckEmailVcodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.CheckEmailVcodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("CheckEmailVerificationCode"))
              .build();
        }
      }
    }
    return getCheckEmailVerificationCodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.HashUserMetaRequest,
      com.github.difftim.accountgrpc.HashUserMetaResponse> getHashUserMetaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HashUserMeta",
      requestType = com.github.difftim.accountgrpc.HashUserMetaRequest.class,
      responseType = com.github.difftim.accountgrpc.HashUserMetaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.HashUserMetaRequest,
      com.github.difftim.accountgrpc.HashUserMetaResponse> getHashUserMetaMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.HashUserMetaRequest, com.github.difftim.accountgrpc.HashUserMetaResponse> getHashUserMetaMethod;
    if ((getHashUserMetaMethod = AccountGrpc.getHashUserMetaMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getHashUserMetaMethod = AccountGrpc.getHashUserMetaMethod) == null) {
          AccountGrpc.getHashUserMetaMethod = getHashUserMetaMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.HashUserMetaRequest, com.github.difftim.accountgrpc.HashUserMetaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HashUserMeta"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.HashUserMetaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.HashUserMetaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("HashUserMeta"))
              .build();
        }
      }
    }
    return getHashUserMetaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.InitUserProfileRequest,
      com.github.difftim.accountgrpc.InitUserProfileResponse> getInitUserProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitUserProfile",
      requestType = com.github.difftim.accountgrpc.InitUserProfileRequest.class,
      responseType = com.github.difftim.accountgrpc.InitUserProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.InitUserProfileRequest,
      com.github.difftim.accountgrpc.InitUserProfileResponse> getInitUserProfileMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.InitUserProfileRequest, com.github.difftim.accountgrpc.InitUserProfileResponse> getInitUserProfileMethod;
    if ((getInitUserProfileMethod = AccountGrpc.getInitUserProfileMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getInitUserProfileMethod = AccountGrpc.getInitUserProfileMethod) == null) {
          AccountGrpc.getInitUserProfileMethod = getInitUserProfileMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.InitUserProfileRequest, com.github.difftim.accountgrpc.InitUserProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitUserProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.InitUserProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.InitUserProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("InitUserProfile"))
              .build();
        }
      }
    }
    return getInitUserProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GetUserProfileRequest,
      com.github.difftim.accountgrpc.GetUserProfileResponse> getGetUserProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserProfile",
      requestType = com.github.difftim.accountgrpc.GetUserProfileRequest.class,
      responseType = com.github.difftim.accountgrpc.GetUserProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GetUserProfileRequest,
      com.github.difftim.accountgrpc.GetUserProfileResponse> getGetUserProfileMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.GetUserProfileRequest, com.github.difftim.accountgrpc.GetUserProfileResponse> getGetUserProfileMethod;
    if ((getGetUserProfileMethod = AccountGrpc.getGetUserProfileMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getGetUserProfileMethod = AccountGrpc.getGetUserProfileMethod) == null) {
          AccountGrpc.getGetUserProfileMethod = getGetUserProfileMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.GetUserProfileRequest, com.github.difftim.accountgrpc.GetUserProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.GetUserProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.GetUserProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("GetUserProfile"))
              .build();
        }
      }
    }
    return getGetUserProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DelUserHashRequest,
      com.github.difftim.accountgrpc.DelUserHashResponse> getDelUserHashMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DelUserHash",
      requestType = com.github.difftim.accountgrpc.DelUserHashRequest.class,
      responseType = com.github.difftim.accountgrpc.DelUserHashResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DelUserHashRequest,
      com.github.difftim.accountgrpc.DelUserHashResponse> getDelUserHashMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DelUserHashRequest, com.github.difftim.accountgrpc.DelUserHashResponse> getDelUserHashMethod;
    if ((getDelUserHashMethod = AccountGrpc.getDelUserHashMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getDelUserHashMethod = AccountGrpc.getDelUserHashMethod) == null) {
          AccountGrpc.getDelUserHashMethod = getDelUserHashMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.DelUserHashRequest, com.github.difftim.accountgrpc.DelUserHashResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DelUserHash"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DelUserHashRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DelUserHashResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("DelUserHash"))
              .build();
        }
      }
    }
    return getDelUserHashMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DisableSearchRequest,
      com.github.difftim.accountgrpc.DisableSearchResponse> getDisableSearchMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DisableSearch",
      requestType = com.github.difftim.accountgrpc.DisableSearchRequest.class,
      responseType = com.github.difftim.accountgrpc.DisableSearchResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DisableSearchRequest,
      com.github.difftim.accountgrpc.DisableSearchResponse> getDisableSearchMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DisableSearchRequest, com.github.difftim.accountgrpc.DisableSearchResponse> getDisableSearchMethod;
    if ((getDisableSearchMethod = AccountGrpc.getDisableSearchMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getDisableSearchMethod = AccountGrpc.getDisableSearchMethod) == null) {
          AccountGrpc.getDisableSearchMethod = getDisableSearchMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.DisableSearchRequest, com.github.difftim.accountgrpc.DisableSearchResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DisableSearch"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DisableSearchRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DisableSearchResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("DisableSearch"))
              .build();
        }
      }
    }
    return getDisableSearchMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DeleteAccReq,
      com.github.difftim.accountgrpc.DeleteAccResp> getDeleteAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteAccount",
      requestType = com.github.difftim.accountgrpc.DeleteAccReq.class,
      responseType = com.github.difftim.accountgrpc.DeleteAccResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DeleteAccReq,
      com.github.difftim.accountgrpc.DeleteAccResp> getDeleteAccountMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.accountgrpc.DeleteAccReq, com.github.difftim.accountgrpc.DeleteAccResp> getDeleteAccountMethod;
    if ((getDeleteAccountMethod = AccountGrpc.getDeleteAccountMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getDeleteAccountMethod = AccountGrpc.getDeleteAccountMethod) == null) {
          AccountGrpc.getDeleteAccountMethod = getDeleteAccountMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.accountgrpc.DeleteAccReq, com.github.difftim.accountgrpc.DeleteAccResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DeleteAccReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.accountgrpc.DeleteAccResp.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("DeleteAccount"))
              .build();
        }
      }
    }
    return getDeleteAccountMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccountStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountStub>() {
        @java.lang.Override
        public AccountStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountStub(channel, callOptions);
        }
      };
    return AccountStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccountBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountBlockingStub>() {
        @java.lang.Override
        public AccountBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountBlockingStub(channel, callOptions);
        }
      };
    return AccountBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccountFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountFutureStub>() {
        @java.lang.Override
        public AccountFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountFutureStub(channel, callOptions);
        }
      };
    return AccountFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class AccountImplBase implements io.grpc.BindableService {

    /**
     */
    public void syncProfile(com.github.difftim.accountgrpc.SyncProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.SyncProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSyncProfileMethod(), responseObserver);
    }

    /**
     */
    public void genEmailVerificationCode(com.github.difftim.accountgrpc.GenEmailVcodeRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GenEmailVcodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenEmailVerificationCodeMethod(), responseObserver);
    }

    /**
     */
    public void checkEmailVerificationCode(com.github.difftim.accountgrpc.CheckEmailVcodeRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.CheckEmailVcodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckEmailVerificationCodeMethod(), responseObserver);
    }

    /**
     */
    public void hashUserMeta(com.github.difftim.accountgrpc.HashUserMetaRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.HashUserMetaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHashUserMetaMethod(), responseObserver);
    }

    /**
     */
    public void initUserProfile(com.github.difftim.accountgrpc.InitUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.InitUserProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInitUserProfileMethod(), responseObserver);
    }

    /**
     */
    public void getUserProfile(com.github.difftim.accountgrpc.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GetUserProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserProfileMethod(), responseObserver);
    }

    /**
     */
    public void delUserHash(com.github.difftim.accountgrpc.DelUserHashRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DelUserHashResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDelUserHashMethod(), responseObserver);
    }

    /**
     */
    public void disableSearch(com.github.difftim.accountgrpc.DisableSearchRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DisableSearchResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDisableSearchMethod(), responseObserver);
    }

    /**
     */
    public void deleteAccount(com.github.difftim.accountgrpc.DeleteAccReq request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DeleteAccResp> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteAccountMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSyncProfileMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.SyncProfileRequest,
                com.github.difftim.accountgrpc.SyncProfileResponse>(
                  this, METHODID_SYNC_PROFILE)))
          .addMethod(
            getGenEmailVerificationCodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.GenEmailVcodeRequest,
                com.github.difftim.accountgrpc.GenEmailVcodeResponse>(
                  this, METHODID_GEN_EMAIL_VERIFICATION_CODE)))
          .addMethod(
            getCheckEmailVerificationCodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.CheckEmailVcodeRequest,
                com.github.difftim.accountgrpc.CheckEmailVcodeResponse>(
                  this, METHODID_CHECK_EMAIL_VERIFICATION_CODE)))
          .addMethod(
            getHashUserMetaMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.HashUserMetaRequest,
                com.github.difftim.accountgrpc.HashUserMetaResponse>(
                  this, METHODID_HASH_USER_META)))
          .addMethod(
            getInitUserProfileMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.InitUserProfileRequest,
                com.github.difftim.accountgrpc.InitUserProfileResponse>(
                  this, METHODID_INIT_USER_PROFILE)))
          .addMethod(
            getGetUserProfileMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.GetUserProfileRequest,
                com.github.difftim.accountgrpc.GetUserProfileResponse>(
                  this, METHODID_GET_USER_PROFILE)))
          .addMethod(
            getDelUserHashMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.DelUserHashRequest,
                com.github.difftim.accountgrpc.DelUserHashResponse>(
                  this, METHODID_DEL_USER_HASH)))
          .addMethod(
            getDisableSearchMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.DisableSearchRequest,
                com.github.difftim.accountgrpc.DisableSearchResponse>(
                  this, METHODID_DISABLE_SEARCH)))
          .addMethod(
            getDeleteAccountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.accountgrpc.DeleteAccReq,
                com.github.difftim.accountgrpc.DeleteAccResp>(
                  this, METHODID_DELETE_ACCOUNT)))
          .build();
    }
  }

  /**
   */
  public static final class AccountStub extends io.grpc.stub.AbstractAsyncStub<AccountStub> {
    private AccountStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountStub(channel, callOptions);
    }

    /**
     */
    public void syncProfile(com.github.difftim.accountgrpc.SyncProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.SyncProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSyncProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void genEmailVerificationCode(com.github.difftim.accountgrpc.GenEmailVcodeRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GenEmailVcodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenEmailVerificationCodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkEmailVerificationCode(com.github.difftim.accountgrpc.CheckEmailVcodeRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.CheckEmailVcodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckEmailVerificationCodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void hashUserMeta(com.github.difftim.accountgrpc.HashUserMetaRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.HashUserMetaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHashUserMetaMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initUserProfile(com.github.difftim.accountgrpc.InitUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.InitUserProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInitUserProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUserProfile(com.github.difftim.accountgrpc.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GetUserProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void delUserHash(com.github.difftim.accountgrpc.DelUserHashRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DelUserHashResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDelUserHashMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void disableSearch(com.github.difftim.accountgrpc.DisableSearchRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DisableSearchResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDisableSearchMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteAccount(com.github.difftim.accountgrpc.DeleteAccReq request,
        io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DeleteAccResp> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteAccountMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AccountBlockingStub extends io.grpc.stub.AbstractBlockingStub<AccountBlockingStub> {
    private AccountBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.difftim.accountgrpc.SyncProfileResponse syncProfile(com.github.difftim.accountgrpc.SyncProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSyncProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.GenEmailVcodeResponse genEmailVerificationCode(com.github.difftim.accountgrpc.GenEmailVcodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenEmailVerificationCodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.CheckEmailVcodeResponse checkEmailVerificationCode(com.github.difftim.accountgrpc.CheckEmailVcodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckEmailVerificationCodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.HashUserMetaResponse hashUserMeta(com.github.difftim.accountgrpc.HashUserMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHashUserMetaMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.InitUserProfileResponse initUserProfile(com.github.difftim.accountgrpc.InitUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInitUserProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.GetUserProfileResponse getUserProfile(com.github.difftim.accountgrpc.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.DelUserHashResponse delUserHash(com.github.difftim.accountgrpc.DelUserHashRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDelUserHashMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.DisableSearchResponse disableSearch(com.github.difftim.accountgrpc.DisableSearchRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDisableSearchMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.accountgrpc.DeleteAccResp deleteAccount(com.github.difftim.accountgrpc.DeleteAccReq request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteAccountMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AccountFutureStub extends io.grpc.stub.AbstractFutureStub<AccountFutureStub> {
    private AccountFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.SyncProfileResponse> syncProfile(
        com.github.difftim.accountgrpc.SyncProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSyncProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.GenEmailVcodeResponse> genEmailVerificationCode(
        com.github.difftim.accountgrpc.GenEmailVcodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenEmailVerificationCodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.CheckEmailVcodeResponse> checkEmailVerificationCode(
        com.github.difftim.accountgrpc.CheckEmailVcodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckEmailVerificationCodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.HashUserMetaResponse> hashUserMeta(
        com.github.difftim.accountgrpc.HashUserMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHashUserMetaMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.InitUserProfileResponse> initUserProfile(
        com.github.difftim.accountgrpc.InitUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInitUserProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.GetUserProfileResponse> getUserProfile(
        com.github.difftim.accountgrpc.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.DelUserHashResponse> delUserHash(
        com.github.difftim.accountgrpc.DelUserHashRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDelUserHashMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.DisableSearchResponse> disableSearch(
        com.github.difftim.accountgrpc.DisableSearchRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDisableSearchMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.accountgrpc.DeleteAccResp> deleteAccount(
        com.github.difftim.accountgrpc.DeleteAccReq request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteAccountMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SYNC_PROFILE = 0;
  private static final int METHODID_GEN_EMAIL_VERIFICATION_CODE = 1;
  private static final int METHODID_CHECK_EMAIL_VERIFICATION_CODE = 2;
  private static final int METHODID_HASH_USER_META = 3;
  private static final int METHODID_INIT_USER_PROFILE = 4;
  private static final int METHODID_GET_USER_PROFILE = 5;
  private static final int METHODID_DEL_USER_HASH = 6;
  private static final int METHODID_DISABLE_SEARCH = 7;
  private static final int METHODID_DELETE_ACCOUNT = 8;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AccountImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AccountImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SYNC_PROFILE:
          serviceImpl.syncProfile((com.github.difftim.accountgrpc.SyncProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.SyncProfileResponse>) responseObserver);
          break;
        case METHODID_GEN_EMAIL_VERIFICATION_CODE:
          serviceImpl.genEmailVerificationCode((com.github.difftim.accountgrpc.GenEmailVcodeRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GenEmailVcodeResponse>) responseObserver);
          break;
        case METHODID_CHECK_EMAIL_VERIFICATION_CODE:
          serviceImpl.checkEmailVerificationCode((com.github.difftim.accountgrpc.CheckEmailVcodeRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.CheckEmailVcodeResponse>) responseObserver);
          break;
        case METHODID_HASH_USER_META:
          serviceImpl.hashUserMeta((com.github.difftim.accountgrpc.HashUserMetaRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.HashUserMetaResponse>) responseObserver);
          break;
        case METHODID_INIT_USER_PROFILE:
          serviceImpl.initUserProfile((com.github.difftim.accountgrpc.InitUserProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.InitUserProfileResponse>) responseObserver);
          break;
        case METHODID_GET_USER_PROFILE:
          serviceImpl.getUserProfile((com.github.difftim.accountgrpc.GetUserProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.GetUserProfileResponse>) responseObserver);
          break;
        case METHODID_DEL_USER_HASH:
          serviceImpl.delUserHash((com.github.difftim.accountgrpc.DelUserHashRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DelUserHashResponse>) responseObserver);
          break;
        case METHODID_DISABLE_SEARCH:
          serviceImpl.disableSearch((com.github.difftim.accountgrpc.DisableSearchRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DisableSearchResponse>) responseObserver);
          break;
        case METHODID_DELETE_ACCOUNT:
          serviceImpl.deleteAccount((com.github.difftim.accountgrpc.DeleteAccReq) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.accountgrpc.DeleteAccResp>) responseObserver);
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

  private static abstract class AccountBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccountBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.difftim.accountgrpc.AccountOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Account");
    }
  }

  private static final class AccountFileDescriptorSupplier
      extends AccountBaseDescriptorSupplier {
    AccountFileDescriptorSupplier() {}
  }

  private static final class AccountMethodDescriptorSupplier
      extends AccountBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AccountMethodDescriptorSupplier(String methodName) {
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
      synchronized (AccountGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccountFileDescriptorSupplier())
              .addMethod(getSyncProfileMethod())
              .addMethod(getGenEmailVerificationCodeMethod())
              .addMethod(getCheckEmailVerificationCodeMethod())
              .addMethod(getHashUserMetaMethod())
              .addMethod(getInitUserProfileMethod())
              .addMethod(getGetUserProfileMethod())
              .addMethod(getDelUserHashMethod())
              .addMethod(getDisableSearchMethod())
              .addMethod(getDeleteAccountMethod())
              .build();
        }
      }
    }
    return result;
  }
}
