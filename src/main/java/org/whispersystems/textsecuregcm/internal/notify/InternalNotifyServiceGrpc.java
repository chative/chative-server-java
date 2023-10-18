package org.whispersystems.textsecuregcm.internal.notify;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalNotifyService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalNotifyServiceGrpc {

  private InternalNotifyServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalNotifyService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendNotifyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendNotify",
      requestType = org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendNotifyMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendNotifyMethod;
    if ((getSendNotifyMethod = InternalNotifyServiceGrpc.getSendNotifyMethod) == null) {
      synchronized (InternalNotifyServiceGrpc.class) {
        if ((getSendNotifyMethod = InternalNotifyServiceGrpc.getSendNotifyMethod) == null) {
          InternalNotifyServiceGrpc.getSendNotifyMethod = getSendNotifyMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendNotify"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalNotifyServiceMethodDescriptorSupplier("sendNotify"))
              .build();
        }
      }
    }
    return getSendNotifyMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalNotifyServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceStub>() {
        @java.lang.Override
        public InternalNotifyServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalNotifyServiceStub(channel, callOptions);
        }
      };
    return InternalNotifyServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalNotifyServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceBlockingStub>() {
        @java.lang.Override
        public InternalNotifyServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalNotifyServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalNotifyServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalNotifyServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalNotifyServiceFutureStub>() {
        @java.lang.Override
        public InternalNotifyServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalNotifyServiceFutureStub(channel, callOptions);
        }
      };
    return InternalNotifyServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalNotifyServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendNotify(org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendNotifyMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendNotifyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_SEND_NOTIFY)))
          .build();
    }
  }

  /**
   */
  public static final class InternalNotifyServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalNotifyServiceStub> {
    private InternalNotifyServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalNotifyServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalNotifyServiceStub(channel, callOptions);
    }

    /**
     */
    public void sendNotify(org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendNotifyMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalNotifyServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalNotifyServiceBlockingStub> {
    private InternalNotifyServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalNotifyServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalNotifyServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse sendNotify(org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendNotifyMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalNotifyServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalNotifyServiceFutureStub> {
    private InternalNotifyServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalNotifyServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalNotifyServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> sendNotify(
        org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendNotifyMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_NOTIFY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalNotifyServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalNotifyServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_NOTIFY:
          serviceImpl.sendNotify((org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest) request,
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

  private static abstract class InternalNotifyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalNotifyServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.notify.InternalNotifyServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalNotifyService");
    }
  }

  private static final class InternalNotifyServiceFileDescriptorSupplier
      extends InternalNotifyServiceBaseDescriptorSupplier {
    InternalNotifyServiceFileDescriptorSupplier() {}
  }

  private static final class InternalNotifyServiceMethodDescriptorSupplier
      extends InternalNotifyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalNotifyServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalNotifyServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalNotifyServiceFileDescriptorSupplier())
              .addMethod(getSendNotifyMethod())
              .build();
        }
      }
    }
    return result;
  }
}
