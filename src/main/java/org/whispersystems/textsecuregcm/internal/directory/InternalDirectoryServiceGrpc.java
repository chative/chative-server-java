package org.whispersystems.textsecuregcm.internal.directory;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalDirectoryService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalDirectoryServiceGrpc {

  private InternalDirectoryServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalDirectoryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendGetContactsMsgMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendGetContactsMsg",
      requestType = org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendGetContactsMsgMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getSendGetContactsMsgMethod;
    if ((getSendGetContactsMsgMethod = InternalDirectoryServiceGrpc.getSendGetContactsMsgMethod) == null) {
      synchronized (InternalDirectoryServiceGrpc.class) {
        if ((getSendGetContactsMsgMethod = InternalDirectoryServiceGrpc.getSendGetContactsMsgMethod) == null) {
          InternalDirectoryServiceGrpc.getSendGetContactsMsgMethod = getSendGetContactsMsgMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendGetContactsMsg"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalDirectoryServiceMethodDescriptorSupplier("sendGetContactsMsg"))
              .build();
        }
      }
    }
    return getSendGetContactsMsgMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getFixMeetingVersionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fixMeetingVersion",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getFixMeetingVersionMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getFixMeetingVersionMethod;
    if ((getFixMeetingVersionMethod = InternalDirectoryServiceGrpc.getFixMeetingVersionMethod) == null) {
      synchronized (InternalDirectoryServiceGrpc.class) {
        if ((getFixMeetingVersionMethod = InternalDirectoryServiceGrpc.getFixMeetingVersionMethod) == null) {
          InternalDirectoryServiceGrpc.getFixMeetingVersionMethod = getFixMeetingVersionMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fixMeetingVersion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalDirectoryServiceMethodDescriptorSupplier("fixMeetingVersion"))
              .build();
        }
      }
    }
    return getFixMeetingVersionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalDirectoryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceStub>() {
        @java.lang.Override
        public InternalDirectoryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalDirectoryServiceStub(channel, callOptions);
        }
      };
    return InternalDirectoryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalDirectoryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceBlockingStub>() {
        @java.lang.Override
        public InternalDirectoryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalDirectoryServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalDirectoryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalDirectoryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalDirectoryServiceFutureStub>() {
        @java.lang.Override
        public InternalDirectoryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalDirectoryServiceFutureStub(channel, callOptions);
        }
      };
    return InternalDirectoryServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalDirectoryServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendGetContactsMsg(org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendGetContactsMsgMethod(), responseObserver);
    }

    /**
     */
    public void fixMeetingVersion(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFixMeetingVersionMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendGetContactsMsgMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_SEND_GET_CONTACTS_MSG)))
          .addMethod(
            getFixMeetingVersionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_FIX_MEETING_VERSION)))
          .build();
    }
  }

  /**
   */
  public static final class InternalDirectoryServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalDirectoryServiceStub> {
    private InternalDirectoryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalDirectoryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalDirectoryServiceStub(channel, callOptions);
    }

    /**
     */
    public void sendGetContactsMsg(org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendGetContactsMsgMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fixMeetingVersion(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFixMeetingVersionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalDirectoryServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalDirectoryServiceBlockingStub> {
    private InternalDirectoryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalDirectoryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalDirectoryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse sendGetContactsMsg(org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendGetContactsMsgMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse fixMeetingVersion(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFixMeetingVersionMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalDirectoryServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalDirectoryServiceFutureStub> {
    private InternalDirectoryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalDirectoryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalDirectoryServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> sendGetContactsMsg(
        org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendGetContactsMsgMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> fixMeetingVersion(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFixMeetingVersionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_GET_CONTACTS_MSG = 0;
  private static final int METHODID_FIX_MEETING_VERSION = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalDirectoryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalDirectoryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_GET_CONTACTS_MSG:
          serviceImpl.sendGetContactsMsg((org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_FIX_MEETING_VERSION:
          serviceImpl.fixMeetingVersion((org.whispersystems.textsecuregcm.internal.common.Empty) request,
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

  private static abstract class InternalDirectoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalDirectoryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.directory.InternalDirectoryServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalDirectoryService");
    }
  }

  private static final class InternalDirectoryServiceFileDescriptorSupplier
      extends InternalDirectoryServiceBaseDescriptorSupplier {
    InternalDirectoryServiceFileDescriptorSupplier() {}
  }

  private static final class InternalDirectoryServiceMethodDescriptorSupplier
      extends InternalDirectoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalDirectoryServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalDirectoryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalDirectoryServiceFileDescriptorSupplier())
              .addMethod(getSendGetContactsMsgMethod())
              .addMethod(getFixMeetingVersionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
