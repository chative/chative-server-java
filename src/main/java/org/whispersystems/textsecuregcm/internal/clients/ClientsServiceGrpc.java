package org.whispersystems.textsecuregcm.internal.clients;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: clientService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ClientsServiceGrpc {

  private ClientsServiceGrpc() {}

  public static final String SERVICE_NAME = "ClientsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getVersionInfo",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoMethod;
    if ((getGetVersionInfoMethod = ClientsServiceGrpc.getGetVersionInfoMethod) == null) {
      synchronized (ClientsServiceGrpc.class) {
        if ((getGetVersionInfoMethod = ClientsServiceGrpc.getGetVersionInfoMethod) == null) {
          ClientsServiceGrpc.getGetVersionInfoMethod = getGetVersionInfoMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getVersionInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ClientsServiceMethodDescriptorSupplier("getVersionInfo"))
              .build();
        }
      }
    }
    return getGetVersionInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest,
      org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoByVerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getVersionInfoByVer",
      requestType = org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest,
      org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoByVerMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest, org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getGetVersionInfoByVerMethod;
    if ((getGetVersionInfoByVerMethod = ClientsServiceGrpc.getGetVersionInfoByVerMethod) == null) {
      synchronized (ClientsServiceGrpc.class) {
        if ((getGetVersionInfoByVerMethod = ClientsServiceGrpc.getGetVersionInfoByVerMethod) == null) {
          ClientsServiceGrpc.getGetVersionInfoByVerMethod = getGetVersionInfoByVerMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest, org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getVersionInfoByVer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ClientsServiceMethodDescriptorSupplier("getVersionInfoByVer"))
              .build();
        }
      }
    }
    return getGetVersionInfoByVerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest,
      org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> getGetVersionInfoByUidMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getVersionInfoByUid",
      requestType = org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest,
      org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> getGetVersionInfoByUidMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest, org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> getGetVersionInfoByUidMethod;
    if ((getGetVersionInfoByUidMethod = ClientsServiceGrpc.getGetVersionInfoByUidMethod) == null) {
      synchronized (ClientsServiceGrpc.class) {
        if ((getGetVersionInfoByUidMethod = ClientsServiceGrpc.getGetVersionInfoByUidMethod) == null) {
          ClientsServiceGrpc.getGetVersionInfoByUidMethod = getGetVersionInfoByUidMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest, org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getVersionInfoByUid"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ClientsServiceMethodDescriptorSupplier("getVersionInfoByUid"))
              .build();
        }
      }
    }
    return getGetVersionInfoByUidMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ClientsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ClientsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ClientsServiceStub>() {
        @java.lang.Override
        public ClientsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ClientsServiceStub(channel, callOptions);
        }
      };
    return ClientsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ClientsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ClientsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ClientsServiceBlockingStub>() {
        @java.lang.Override
        public ClientsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ClientsServiceBlockingStub(channel, callOptions);
        }
      };
    return ClientsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ClientsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ClientsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ClientsServiceFutureStub>() {
        @java.lang.Override
        public ClientsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ClientsServiceFutureStub(channel, callOptions);
        }
      };
    return ClientsServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ClientsServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getVersionInfo(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetVersionInfoMethod(), responseObserver);
    }

    /**
     */
    public void getVersionInfoByVer(org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetVersionInfoByVerMethod(), responseObserver);
    }

    /**
     */
    public void getVersionInfoByUid(org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetVersionInfoByUidMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetVersionInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>(
                  this, METHODID_GET_VERSION_INFO)))
          .addMethod(
            getGetVersionInfoByVerMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest,
                org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>(
                  this, METHODID_GET_VERSION_INFO_BY_VER)))
          .addMethod(
            getGetVersionInfoByUidMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest,
                org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse>(
                  this, METHODID_GET_VERSION_INFO_BY_UID)))
          .build();
    }
  }

  /**
   */
  public static final class ClientsServiceStub extends io.grpc.stub.AbstractAsyncStub<ClientsServiceStub> {
    private ClientsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ClientsServiceStub(channel, callOptions);
    }

    /**
     */
    public void getVersionInfo(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetVersionInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getVersionInfoByVer(org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetVersionInfoByVerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getVersionInfoByUid(org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetVersionInfoByUidMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ClientsServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ClientsServiceBlockingStub> {
    private ClientsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ClientsServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse getVersionInfo(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetVersionInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse getVersionInfoByVer(org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetVersionInfoByVerMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse getVersionInfoByUid(org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetVersionInfoByUidMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ClientsServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ClientsServiceFutureStub> {
    private ClientsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ClientsServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getVersionInfo(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetVersionInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse> getVersionInfoByVer(
        org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetVersionInfoByVerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse> getVersionInfoByUid(
        org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetVersionInfoByUidMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_VERSION_INFO = 0;
  private static final int METHODID_GET_VERSION_INFO_BY_VER = 1;
  private static final int METHODID_GET_VERSION_INFO_BY_UID = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ClientsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ClientsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_VERSION_INFO:
          serviceImpl.getVersionInfo((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>) responseObserver);
          break;
        case METHODID_GET_VERSION_INFO_BY_VER:
          serviceImpl.getVersionInfoByVer((org.whispersystems.textsecuregcm.internal.clients.ClientVersionRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientVersionResponse>) responseObserver);
          break;
        case METHODID_GET_VERSION_INFO_BY_UID:
          serviceImpl.getVersionInfoByUid((org.whispersystems.textsecuregcm.internal.clients.ClientQueryRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.clients.ClientInfoResponse>) responseObserver);
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

  private static abstract class ClientsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ClientsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.clients.ClientService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ClientsService");
    }
  }

  private static final class ClientsServiceFileDescriptorSupplier
      extends ClientsServiceBaseDescriptorSupplier {
    ClientsServiceFileDescriptorSupplier() {}
  }

  private static final class ClientsServiceMethodDescriptorSupplier
      extends ClientsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ClientsServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ClientsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ClientsServiceFileDescriptorSupplier())
              .addMethod(getGetVersionInfoMethod())
              .addMethod(getGetVersionInfoByVerMethod())
              .addMethod(getGetVersionInfoByUidMethod())
              .build();
        }
      }
    }
    return result;
  }
}
