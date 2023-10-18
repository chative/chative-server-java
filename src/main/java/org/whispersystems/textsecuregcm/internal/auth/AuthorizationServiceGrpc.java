package org.whispersystems.textsecuregcm.internal.auth;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: AuthorizationService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AuthorizationServiceGrpc {

  private AuthorizationServiceGrpc() {}

  public static final String SERVICE_NAME = "AuthorizationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateTokenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createToken",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateTokenMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateTokenMethod;
    if ((getCreateTokenMethod = AuthorizationServiceGrpc.getCreateTokenMethod) == null) {
      synchronized (AuthorizationServiceGrpc.class) {
        if ((getCreateTokenMethod = AuthorizationServiceGrpc.getCreateTokenMethod) == null) {
          AuthorizationServiceGrpc.getCreateTokenMethod = getCreateTokenMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createToken"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthorizationServiceMethodDescriptorSupplier("createToken"))
              .build();
        }
      }
    }
    return getCreateTokenMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getVerifyTokenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "verifyToken",
      requestType = org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getVerifyTokenMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getVerifyTokenMethod;
    if ((getVerifyTokenMethod = AuthorizationServiceGrpc.getVerifyTokenMethod) == null) {
      synchronized (AuthorizationServiceGrpc.class) {
        if ((getVerifyTokenMethod = AuthorizationServiceGrpc.getVerifyTokenMethod) == null) {
          AuthorizationServiceGrpc.getVerifyTokenMethod = getVerifyTokenMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "verifyToken"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthorizationServiceMethodDescriptorSupplier("verifyToken"))
              .build();
        }
      }
    }
    return getVerifyTokenMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AuthorizationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceStub>() {
        @java.lang.Override
        public AuthorizationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthorizationServiceStub(channel, callOptions);
        }
      };
    return AuthorizationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AuthorizationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceBlockingStub>() {
        @java.lang.Override
        public AuthorizationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthorizationServiceBlockingStub(channel, callOptions);
        }
      };
    return AuthorizationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AuthorizationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthorizationServiceFutureStub>() {
        @java.lang.Override
        public AuthorizationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthorizationServiceFutureStub(channel, callOptions);
        }
      };
    return AuthorizationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class AuthorizationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void createToken(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateTokenMethod(), responseObserver);
    }

    /**
     */
    public void verifyToken(org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyTokenMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateTokenMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CREATE_TOKEN)))
          .addMethod(
            getVerifyTokenMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_VERIFY_TOKEN)))
          .build();
    }
  }

  /**
   */
  public static final class AuthorizationServiceStub extends io.grpc.stub.AbstractAsyncStub<AuthorizationServiceStub> {
    private AuthorizationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthorizationServiceStub(channel, callOptions);
    }

    /**
     */
    public void createToken(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateTokenMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyToken(org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyTokenMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AuthorizationServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<AuthorizationServiceBlockingStub> {
    private AuthorizationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthorizationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse createToken(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateTokenMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse verifyToken(org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyTokenMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AuthorizationServiceFutureStub extends io.grpc.stub.AbstractFutureStub<AuthorizationServiceFutureStub> {
    private AuthorizationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthorizationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> createToken(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateTokenMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> verifyToken(
        org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyTokenMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_TOKEN = 0;
  private static final int METHODID_VERIFY_TOKEN = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AuthorizationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AuthorizationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_TOKEN:
          serviceImpl.createToken((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_VERIFY_TOKEN:
          serviceImpl.verifyToken((org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest) request,
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

  private static abstract class AuthorizationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AuthorizationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.auth.AuthorizationServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AuthorizationService");
    }
  }

  private static final class AuthorizationServiceFileDescriptorSupplier
      extends AuthorizationServiceBaseDescriptorSupplier {
    AuthorizationServiceFileDescriptorSupplier() {}
  }

  private static final class AuthorizationServiceMethodDescriptorSupplier
      extends AuthorizationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AuthorizationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (AuthorizationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AuthorizationServiceFileDescriptorSupplier())
              .addMethod(getCreateTokenMethod())
              .addMethod(getVerifyTokenMethod())
              .build();
        }
      }
    }
    return result;
  }
}
