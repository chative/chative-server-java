package org.whispersystems.textsecuregcm.internal.status;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: UserStatusService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserStatusServiceGrpc {

  private UserStatusServiceGrpc() {}

  public static final String SERVICE_NAME = "userstatus.UserStatusService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.status.UserStatusRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getIsDNDMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "isDND",
      requestType = org.whispersystems.textsecuregcm.internal.status.UserStatusRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.status.UserStatusRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getIsDNDMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.status.UserStatusRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getIsDNDMethod;
    if ((getIsDNDMethod = UserStatusServiceGrpc.getIsDNDMethod) == null) {
      synchronized (UserStatusServiceGrpc.class) {
        if ((getIsDNDMethod = UserStatusServiceGrpc.getIsDNDMethod) == null) {
          UserStatusServiceGrpc.getIsDNDMethod = getIsDNDMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.status.UserStatusRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "isDND"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.status.UserStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserStatusServiceMethodDescriptorSupplier("isDND"))
              .build();
        }
      }
    }
    return getIsDNDMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserStatusServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceStub>() {
        @java.lang.Override
        public UserStatusServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusServiceStub(channel, callOptions);
        }
      };
    return UserStatusServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserStatusServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceBlockingStub>() {
        @java.lang.Override
        public UserStatusServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusServiceBlockingStub(channel, callOptions);
        }
      };
    return UserStatusServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserStatusServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusServiceFutureStub>() {
        @java.lang.Override
        public UserStatusServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusServiceFutureStub(channel, callOptions);
        }
      };
    return UserStatusServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class UserStatusServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void isDND(org.whispersystems.textsecuregcm.internal.status.UserStatusRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getIsDNDMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getIsDNDMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.status.UserStatusRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_IS_DND)))
          .build();
    }
  }

  /**
   */
  public static final class UserStatusServiceStub extends io.grpc.stub.AbstractAsyncStub<UserStatusServiceStub> {
    private UserStatusServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusServiceStub(channel, callOptions);
    }

    /**
     */
    public void isDND(org.whispersystems.textsecuregcm.internal.status.UserStatusRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getIsDNDMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserStatusServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<UserStatusServiceBlockingStub> {
    private UserStatusServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse isDND(org.whispersystems.textsecuregcm.internal.status.UserStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getIsDNDMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserStatusServiceFutureStub extends io.grpc.stub.AbstractFutureStub<UserStatusServiceFutureStub> {
    private UserStatusServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> isDND(
        org.whispersystems.textsecuregcm.internal.status.UserStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getIsDNDMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_IS_DND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserStatusServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserStatusServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_IS_DND:
          serviceImpl.isDND((org.whispersystems.textsecuregcm.internal.status.UserStatusRequest) request,
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

  private static abstract class UserStatusServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserStatusServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.status.UserStatusServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserStatusService");
    }
  }

  private static final class UserStatusServiceFileDescriptorSupplier
      extends UserStatusServiceBaseDescriptorSupplier {
    UserStatusServiceFileDescriptorSupplier() {}
  }

  private static final class UserStatusServiceMethodDescriptorSupplier
      extends UserStatusServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserStatusServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (UserStatusServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserStatusServiceFileDescriptorSupplier())
              .addMethod(getIsDNDMethod())
              .build();
        }
      }
    }
    return result;
  }
}
