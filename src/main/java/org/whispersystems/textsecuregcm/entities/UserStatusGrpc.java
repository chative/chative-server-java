package org.whispersystems.textsecuregcm.entities;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: userStatus.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserStatusGrpc {

  private UserStatusGrpc() {}

  public static final String SERVICE_NAME = "UserStatus";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest,
      org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> getGetUserStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserStatus",
      requestType = org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest.class,
      responseType = org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest,
      org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> getGetUserStatusMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest, org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> getGetUserStatusMethod;
    if ((getGetUserStatusMethod = UserStatusGrpc.getGetUserStatusMethod) == null) {
      synchronized (UserStatusGrpc.class) {
        if ((getGetUserStatusMethod = UserStatusGrpc.getGetUserStatusMethod) == null) {
          UserStatusGrpc.getGetUserStatusMethod = getGetUserStatusMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest, org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserStatusMethodDescriptorSupplier("GetUserStatus"))
              .build();
        }
      }
    }
    return getGetUserStatusMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserStatusStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusStub>() {
        @java.lang.Override
        public UserStatusStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusStub(channel, callOptions);
        }
      };
    return UserStatusStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserStatusBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusBlockingStub>() {
        @java.lang.Override
        public UserStatusBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusBlockingStub(channel, callOptions);
        }
      };
    return UserStatusBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserStatusFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserStatusFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserStatusFutureStub>() {
        @java.lang.Override
        public UserStatusFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserStatusFutureStub(channel, callOptions);
        }
      };
    return UserStatusFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class UserStatusImplBase implements io.grpc.BindableService {

    /**
     */
    public void getUserStatus(org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserStatusMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetUserStatusMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest,
                org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse>(
                  this, METHODID_GET_USER_STATUS)))
          .build();
    }
  }

  /**
   */
  public static final class UserStatusStub extends io.grpc.stub.AbstractAsyncStub<UserStatusStub> {
    private UserStatusStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusStub(channel, callOptions);
    }

    /**
     */
    public void getUserStatus(org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserStatusMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserStatusBlockingStub extends io.grpc.stub.AbstractBlockingStub<UserStatusBlockingStub> {
    private UserStatusBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse getUserStatus(org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserStatusMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserStatusFutureStub extends io.grpc.stub.AbstractFutureStub<UserStatusFutureStub> {
    private UserStatusFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserStatusFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserStatusFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse> getUserStatus(
        org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserStatusMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_STATUS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserStatusImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserStatusImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_USER_STATUS:
          serviceImpl.getUserStatus((org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.UserStatusResponse>) responseObserver);
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

  private static abstract class UserStatusBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserStatusBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.entities.UserStatusOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserStatus");
    }
  }

  private static final class UserStatusFileDescriptorSupplier
      extends UserStatusBaseDescriptorSupplier {
    UserStatusFileDescriptorSupplier() {}
  }

  private static final class UserStatusMethodDescriptorSupplier
      extends UserStatusBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserStatusMethodDescriptorSupplier(String methodName) {
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
      synchronized (UserStatusGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserStatusFileDescriptorSupplier())
              .addMethod(getGetUserStatusMethod())
              .build();
        }
      }
    }
    return result;
  }
}
