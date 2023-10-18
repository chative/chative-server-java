package com.github.difftim.friend;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: friend.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FriendGrpc {

  private FriendGrpc() {}

  public static final String SERVICE_NAME = "pb.Friend";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.difftim.friend.AddRequest,
      com.github.difftim.friend.AddResponse> getAddMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Add",
      requestType = com.github.difftim.friend.AddRequest.class,
      responseType = com.github.difftim.friend.AddResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.friend.AddRequest,
      com.github.difftim.friend.AddResponse> getAddMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.friend.AddRequest, com.github.difftim.friend.AddResponse> getAddMethod;
    if ((getAddMethod = FriendGrpc.getAddMethod) == null) {
      synchronized (FriendGrpc.class) {
        if ((getAddMethod = FriendGrpc.getAddMethod) == null) {
          FriendGrpc.getAddMethod = getAddMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.friend.AddRequest, com.github.difftim.friend.AddResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Add"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.AddRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.AddResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FriendMethodDescriptorSupplier("Add"))
              .build();
        }
      }
    }
    return getAddMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.friend.ListRequest,
      com.github.difftim.friend.ListResponse> getListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "List",
      requestType = com.github.difftim.friend.ListRequest.class,
      responseType = com.github.difftim.friend.ListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.friend.ListRequest,
      com.github.difftim.friend.ListResponse> getListMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.friend.ListRequest, com.github.difftim.friend.ListResponse> getListMethod;
    if ((getListMethod = FriendGrpc.getListMethod) == null) {
      synchronized (FriendGrpc.class) {
        if ((getListMethod = FriendGrpc.getListMethod) == null) {
          FriendGrpc.getListMethod = getListMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.friend.ListRequest, com.github.difftim.friend.ListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "List"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.ListRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.ListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FriendMethodDescriptorSupplier("List"))
              .build();
        }
      }
    }
    return getListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.friend.ExistRequest,
      com.github.difftim.friend.ExistResponse> getExistMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Exist",
      requestType = com.github.difftim.friend.ExistRequest.class,
      responseType = com.github.difftim.friend.ExistResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.friend.ExistRequest,
      com.github.difftim.friend.ExistResponse> getExistMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.friend.ExistRequest, com.github.difftim.friend.ExistResponse> getExistMethod;
    if ((getExistMethod = FriendGrpc.getExistMethod) == null) {
      synchronized (FriendGrpc.class) {
        if ((getExistMethod = FriendGrpc.getExistMethod) == null) {
          FriendGrpc.getExistMethod = getExistMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.friend.ExistRequest, com.github.difftim.friend.ExistResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Exist"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.ExistRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.ExistResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FriendMethodDescriptorSupplier("Exist"))
              .build();
        }
      }
    }
    return getExistMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.friend.HowToMetRequest,
      com.github.difftim.friend.HowToMetResponse> getHowToMetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HowToMet",
      requestType = com.github.difftim.friend.HowToMetRequest.class,
      responseType = com.github.difftim.friend.HowToMetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.friend.HowToMetRequest,
      com.github.difftim.friend.HowToMetResponse> getHowToMetMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.friend.HowToMetRequest, com.github.difftim.friend.HowToMetResponse> getHowToMetMethod;
    if ((getHowToMetMethod = FriendGrpc.getHowToMetMethod) == null) {
      synchronized (FriendGrpc.class) {
        if ((getHowToMetMethod = FriendGrpc.getHowToMetMethod) == null) {
          FriendGrpc.getHowToMetMethod = getHowToMetMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.friend.HowToMetRequest, com.github.difftim.friend.HowToMetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HowToMet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.HowToMetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.friend.HowToMetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FriendMethodDescriptorSupplier("HowToMet"))
              .build();
        }
      }
    }
    return getHowToMetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FriendStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FriendStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FriendStub>() {
        @java.lang.Override
        public FriendStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FriendStub(channel, callOptions);
        }
      };
    return FriendStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FriendBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FriendBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FriendBlockingStub>() {
        @java.lang.Override
        public FriendBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FriendBlockingStub(channel, callOptions);
        }
      };
    return FriendBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FriendFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FriendFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FriendFutureStub>() {
        @java.lang.Override
        public FriendFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FriendFutureStub(channel, callOptions);
        }
      };
    return FriendFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class FriendImplBase implements io.grpc.BindableService {

    /**
     */
    public void add(com.github.difftim.friend.AddRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.AddResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddMethod(), responseObserver);
    }

    /**
     */
    public void list(com.github.difftim.friend.ListRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.ListResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListMethod(), responseObserver);
    }

    /**
     */
    public void exist(com.github.difftim.friend.ExistRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.ExistResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExistMethod(), responseObserver);
    }

    /**
     * <pre>
     * rpc Ping (Ping) returns(Response);
     * </pre>
     */
    public void howToMet(com.github.difftim.friend.HowToMetRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.HowToMetResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHowToMetMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAddMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.friend.AddRequest,
                com.github.difftim.friend.AddResponse>(
                  this, METHODID_ADD)))
          .addMethod(
            getListMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.friend.ListRequest,
                com.github.difftim.friend.ListResponse>(
                  this, METHODID_LIST)))
          .addMethod(
            getExistMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.friend.ExistRequest,
                com.github.difftim.friend.ExistResponse>(
                  this, METHODID_EXIST)))
          .addMethod(
            getHowToMetMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.friend.HowToMetRequest,
                com.github.difftim.friend.HowToMetResponse>(
                  this, METHODID_HOW_TO_MET)))
          .build();
    }
  }

  /**
   */
  public static final class FriendStub extends io.grpc.stub.AbstractAsyncStub<FriendStub> {
    private FriendStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FriendStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FriendStub(channel, callOptions);
    }

    /**
     */
    public void add(com.github.difftim.friend.AddRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.AddResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void list(com.github.difftim.friend.ListRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.ListResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void exist(com.github.difftim.friend.ExistRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.ExistResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExistMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * rpc Ping (Ping) returns(Response);
     * </pre>
     */
    public void howToMet(com.github.difftim.friend.HowToMetRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.friend.HowToMetResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHowToMetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class FriendBlockingStub extends io.grpc.stub.AbstractBlockingStub<FriendBlockingStub> {
    private FriendBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FriendBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FriendBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.difftim.friend.AddResponse add(com.github.difftim.friend.AddRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.friend.ListResponse list(com.github.difftim.friend.ListRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.friend.ExistResponse exist(com.github.difftim.friend.ExistRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExistMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * rpc Ping (Ping) returns(Response);
     * </pre>
     */
    public com.github.difftim.friend.HowToMetResponse howToMet(com.github.difftim.friend.HowToMetRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHowToMetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class FriendFutureStub extends io.grpc.stub.AbstractFutureStub<FriendFutureStub> {
    private FriendFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FriendFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FriendFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.friend.AddResponse> add(
        com.github.difftim.friend.AddRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.friend.ListResponse> list(
        com.github.difftim.friend.ListRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.friend.ExistResponse> exist(
        com.github.difftim.friend.ExistRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExistMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * rpc Ping (Ping) returns(Response);
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.friend.HowToMetResponse> howToMet(
        com.github.difftim.friend.HowToMetRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHowToMetMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD = 0;
  private static final int METHODID_LIST = 1;
  private static final int METHODID_EXIST = 2;
  private static final int METHODID_HOW_TO_MET = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FriendImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FriendImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD:
          serviceImpl.add((com.github.difftim.friend.AddRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.friend.AddResponse>) responseObserver);
          break;
        case METHODID_LIST:
          serviceImpl.list((com.github.difftim.friend.ListRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.friend.ListResponse>) responseObserver);
          break;
        case METHODID_EXIST:
          serviceImpl.exist((com.github.difftim.friend.ExistRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.friend.ExistResponse>) responseObserver);
          break;
        case METHODID_HOW_TO_MET:
          serviceImpl.howToMet((com.github.difftim.friend.HowToMetRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.friend.HowToMetResponse>) responseObserver);
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

  private static abstract class FriendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FriendBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.difftim.friend.FriendProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Friend");
    }
  }

  private static final class FriendFileDescriptorSupplier
      extends FriendBaseDescriptorSupplier {
    FriendFileDescriptorSupplier() {}
  }

  private static final class FriendMethodDescriptorSupplier
      extends FriendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FriendMethodDescriptorSupplier(String methodName) {
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
      synchronized (FriendGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FriendFileDescriptorSupplier())
              .addMethod(getAddMethod())
              .addMethod(getListMethod())
              .addMethod(getExistMethod())
              .addMethod(getHowToMetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
