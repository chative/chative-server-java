package org.whispersystems.textsecuregcm.internal.message;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalMessageService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalMessageServiceGrpc {

  private InternalMessageServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalMessageService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getMsgCount",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.message.MsgCountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountMethod;
    if ((getGetMsgCountMethod = InternalMessageServiceGrpc.getGetMsgCountMethod) == null) {
      synchronized (InternalMessageServiceGrpc.class) {
        if ((getGetMsgCountMethod = InternalMessageServiceGrpc.getGetMsgCountMethod) == null) {
          InternalMessageServiceGrpc.getGetMsgCountMethod = getGetMsgCountMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getMsgCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.message.MsgCountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalMessageServiceMethodDescriptorSupplier("getMsgCount"))
              .build();
        }
      }
    }
    return getGetMsgCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountByEstimateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getMsgCountByEstimate",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.message.MsgCountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountByEstimateMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getGetMsgCountByEstimateMethod;
    if ((getGetMsgCountByEstimateMethod = InternalMessageServiceGrpc.getGetMsgCountByEstimateMethod) == null) {
      synchronized (InternalMessageServiceGrpc.class) {
        if ((getGetMsgCountByEstimateMethod = InternalMessageServiceGrpc.getGetMsgCountByEstimateMethod) == null) {
          InternalMessageServiceGrpc.getGetMsgCountByEstimateMethod = getGetMsgCountByEstimateMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getMsgCountByEstimate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.message.MsgCountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalMessageServiceMethodDescriptorSupplier("getMsgCountByEstimate"))
              .build();
        }
      }
    }
    return getGetMsgCountByEstimateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalMessageServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceStub>() {
        @java.lang.Override
        public InternalMessageServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalMessageServiceStub(channel, callOptions);
        }
      };
    return InternalMessageServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalMessageServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceBlockingStub>() {
        @java.lang.Override
        public InternalMessageServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalMessageServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalMessageServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalMessageServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalMessageServiceFutureStub>() {
        @java.lang.Override
        public InternalMessageServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalMessageServiceFutureStub(channel, callOptions);
        }
      };
    return InternalMessageServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalMessageServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getMsgCount(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMsgCountMethod(), responseObserver);
    }

    /**
     */
    public void getMsgCountByEstimate(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMsgCountByEstimateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMsgCountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>(
                  this, METHODID_GET_MSG_COUNT)))
          .addMethod(
            getGetMsgCountByEstimateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>(
                  this, METHODID_GET_MSG_COUNT_BY_ESTIMATE)))
          .build();
    }
  }

  /**
   */
  public static final class InternalMessageServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalMessageServiceStub> {
    private InternalMessageServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalMessageServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalMessageServiceStub(channel, callOptions);
    }

    /**
     */
    public void getMsgCount(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMsgCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMsgCountByEstimate(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMsgCountByEstimateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalMessageServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalMessageServiceBlockingStub> {
    private InternalMessageServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalMessageServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalMessageServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.message.MsgCountResponse getMsgCount(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMsgCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.message.MsgCountResponse getMsgCountByEstimate(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMsgCountByEstimateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalMessageServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalMessageServiceFutureStub> {
    private InternalMessageServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalMessageServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalMessageServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getMsgCount(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMsgCountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse> getMsgCountByEstimate(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMsgCountByEstimateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_MSG_COUNT = 0;
  private static final int METHODID_GET_MSG_COUNT_BY_ESTIMATE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalMessageServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalMessageServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_MSG_COUNT:
          serviceImpl.getMsgCount((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>) responseObserver);
          break;
        case METHODID_GET_MSG_COUNT_BY_ESTIMATE:
          serviceImpl.getMsgCountByEstimate((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.message.MsgCountResponse>) responseObserver);
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

  private static abstract class InternalMessageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalMessageServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.message.InternalMessageServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalMessageService");
    }
  }

  private static final class InternalMessageServiceFileDescriptorSupplier
      extends InternalMessageServiceBaseDescriptorSupplier {
    InternalMessageServiceFileDescriptorSupplier() {}
  }

  private static final class InternalMessageServiceMethodDescriptorSupplier
      extends InternalMessageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalMessageServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalMessageServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalMessageServiceFileDescriptorSupplier())
              .addMethod(getGetMsgCountMethod())
              .addMethod(getGetMsgCountByEstimateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
