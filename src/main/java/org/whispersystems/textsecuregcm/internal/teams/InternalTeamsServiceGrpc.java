package org.whispersystems.textsecuregcm.internal.teams;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalTeamsService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalTeamsServiceGrpc {

  private InternalTeamsServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalTeamsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get",
      requestType = org.whispersystems.textsecuregcm.internal.teams.GetRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.teams.GetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest, org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetMethod;
    if ((getGetMethod = InternalTeamsServiceGrpc.getGetMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getGetMethod = InternalTeamsServiceGrpc.getGetMethod) == null) {
          InternalTeamsServiceGrpc.getGetMethod = getGetMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.GetRequest, org.whispersystems.textsecuregcm.internal.teams.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("get"))
              .build();
        }
      }
    }
    return getGetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetAllMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getAll",
      requestType = org.whispersystems.textsecuregcm.internal.common.Step.class,
      responseType = org.whispersystems.textsecuregcm.internal.teams.GetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetAllMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetAllMethod;
    if ((getGetAllMethod = InternalTeamsServiceGrpc.getGetAllMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getGetAllMethod = InternalTeamsServiceGrpc.getGetAllMethod) == null) {
          InternalTeamsServiceGrpc.getGetAllMethod = getGetAllMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.teams.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getAll"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("getAll"))
              .build();
        }
      }
    }
    return getGetAllMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "join",
      requestType = org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getJoinMethod;
    if ((getJoinMethod = InternalTeamsServiceGrpc.getJoinMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getJoinMethod = InternalTeamsServiceGrpc.getJoinMethod) == null) {
          InternalTeamsServiceGrpc.getJoinMethod = getJoinMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "join"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("join"))
              .build();
        }
      }
    }
    return getJoinMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "leave",
      requestType = org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getLeaveMethod;
    if ((getLeaveMethod = InternalTeamsServiceGrpc.getLeaveMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getLeaveMethod = InternalTeamsServiceGrpc.getLeaveMethod) == null) {
          InternalTeamsServiceGrpc.getLeaveMethod = getLeaveMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "leave"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("leave"))
              .build();
        }
      }
    }
    return getLeaveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetTreeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getTree",
      requestType = org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.teams.GetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest,
      org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetTreeMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest, org.whispersystems.textsecuregcm.internal.teams.GetResponse> getGetTreeMethod;
    if ((getGetTreeMethod = InternalTeamsServiceGrpc.getGetTreeMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getGetTreeMethod = InternalTeamsServiceGrpc.getGetTreeMethod) == null) {
          InternalTeamsServiceGrpc.getGetTreeMethod = getGetTreeMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest, org.whispersystems.textsecuregcm.internal.teams.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getTree"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("getTree"))
              .build();
        }
      }
    }
    return getGetTreeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateOrUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createOrUpdate",
      requestType = org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateOrUpdateMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getCreateOrUpdateMethod;
    if ((getCreateOrUpdateMethod = InternalTeamsServiceGrpc.getCreateOrUpdateMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getCreateOrUpdateMethod = InternalTeamsServiceGrpc.getCreateOrUpdateMethod) == null) {
          InternalTeamsServiceGrpc.getCreateOrUpdateMethod = getCreateOrUpdateMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createOrUpdate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("createOrUpdate"))
              .build();
        }
      }
    }
    return getCreateOrUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDeleteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "delete",
      requestType = org.whispersystems.textsecuregcm.internal.teams.GetRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDeleteMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.teams.GetRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getDeleteMethod;
    if ((getDeleteMethod = InternalTeamsServiceGrpc.getDeleteMethod) == null) {
      synchronized (InternalTeamsServiceGrpc.class) {
        if ((getDeleteMethod = InternalTeamsServiceGrpc.getDeleteMethod) == null) {
          InternalTeamsServiceGrpc.getDeleteMethod = getDeleteMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.teams.GetRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "delete"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.teams.GetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTeamsServiceMethodDescriptorSupplier("delete"))
              .build();
        }
      }
    }
    return getDeleteMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalTeamsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceStub>() {
        @java.lang.Override
        public InternalTeamsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTeamsServiceStub(channel, callOptions);
        }
      };
    return InternalTeamsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalTeamsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceBlockingStub>() {
        @java.lang.Override
        public InternalTeamsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTeamsServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalTeamsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalTeamsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTeamsServiceFutureStub>() {
        @java.lang.Override
        public InternalTeamsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTeamsServiceFutureStub(channel, callOptions);
        }
      };
    return InternalTeamsServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalTeamsServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void get(org.whispersystems.textsecuregcm.internal.teams.GetRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.common.Step request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllMethod(), responseObserver);
    }

    /**
     */
    public void join(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getJoinMethod(), responseObserver);
    }

    /**
     */
    public void leave(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLeaveMethod(), responseObserver);
    }

    /**
     */
    public void getTree(org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTreeMethod(), responseObserver);
    }

    /**
     */
    public void createOrUpdate(org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateOrUpdateMethod(), responseObserver);
    }

    /**
     */
    public void delete(org.whispersystems.textsecuregcm.internal.teams.GetRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.GetRequest,
                org.whispersystems.textsecuregcm.internal.teams.GetResponse>(
                  this, METHODID_GET)))
          .addMethod(
            getGetAllMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Step,
                org.whispersystems.textsecuregcm.internal.teams.GetResponse>(
                  this, METHODID_GET_ALL)))
          .addMethod(
            getJoinMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_JOIN)))
          .addMethod(
            getLeaveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_LEAVE)))
          .addMethod(
            getGetTreeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest,
                org.whispersystems.textsecuregcm.internal.teams.GetResponse>(
                  this, METHODID_GET_TREE)))
          .addMethod(
            getCreateOrUpdateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CREATE_OR_UPDATE)))
          .addMethod(
            getDeleteMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.teams.GetRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_DELETE)))
          .build();
    }
  }

  /**
   */
  public static final class InternalTeamsServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalTeamsServiceStub> {
    private InternalTeamsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTeamsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTeamsServiceStub(channel, callOptions);
    }

    /**
     */
    public void get(org.whispersystems.textsecuregcm.internal.teams.GetRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.common.Step request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void join(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getJoinMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void leave(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTree(org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTreeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createOrUpdate(org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateOrUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void delete(org.whispersystems.textsecuregcm.internal.teams.GetRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalTeamsServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalTeamsServiceBlockingStub> {
    private InternalTeamsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTeamsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTeamsServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.teams.GetResponse get(org.whispersystems.textsecuregcm.internal.teams.GetRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.teams.GetResponse getAll(org.whispersystems.textsecuregcm.internal.common.Step request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse join(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getJoinMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse leave(org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLeaveMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.teams.GetResponse getTree(org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTreeMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse createOrUpdate(org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrUpdateMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse delete(org.whispersystems.textsecuregcm.internal.teams.GetRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalTeamsServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalTeamsServiceFutureStub> {
    private InternalTeamsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTeamsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTeamsServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.teams.GetResponse> get(
        org.whispersystems.textsecuregcm.internal.teams.GetRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.teams.GetResponse> getAll(
        org.whispersystems.textsecuregcm.internal.common.Step request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> join(
        org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getJoinMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> leave(
        org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.teams.GetResponse> getTree(
        org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTreeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> createOrUpdate(
        org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateOrUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> delete(
        org.whispersystems.textsecuregcm.internal.teams.GetRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET = 0;
  private static final int METHODID_GET_ALL = 1;
  private static final int METHODID_JOIN = 2;
  private static final int METHODID_LEAVE = 3;
  private static final int METHODID_GET_TREE = 4;
  private static final int METHODID_CREATE_OR_UPDATE = 5;
  private static final int METHODID_DELETE = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalTeamsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalTeamsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET:
          serviceImpl.get((org.whispersystems.textsecuregcm.internal.teams.GetRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse>) responseObserver);
          break;
        case METHODID_GET_ALL:
          serviceImpl.getAll((org.whispersystems.textsecuregcm.internal.common.Step) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse>) responseObserver);
          break;
        case METHODID_JOIN:
          serviceImpl.join((org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_LEAVE:
          serviceImpl.leave((org.whispersystems.textsecuregcm.internal.teams.JoinLeaveRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GET_TREE:
          serviceImpl.getTree((org.whispersystems.textsecuregcm.internal.teams.GetTreeRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.teams.GetResponse>) responseObserver);
          break;
        case METHODID_CREATE_OR_UPDATE:
          serviceImpl.createOrUpdate((org.whispersystems.textsecuregcm.internal.teams.CreateOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_DELETE:
          serviceImpl.delete((org.whispersystems.textsecuregcm.internal.teams.GetRequest) request,
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

  private static abstract class InternalTeamsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalTeamsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalTeamsService");
    }
  }

  private static final class InternalTeamsServiceFileDescriptorSupplier
      extends InternalTeamsServiceBaseDescriptorSupplier {
    InternalTeamsServiceFileDescriptorSupplier() {}
  }

  private static final class InternalTeamsServiceMethodDescriptorSupplier
      extends InternalTeamsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalTeamsServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalTeamsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalTeamsServiceFileDescriptorSupplier())
              .addMethod(getGetMethod())
              .addMethod(getGetAllMethod())
              .addMethod(getJoinMethod())
              .addMethod(getLeaveMethod())
              .addMethod(getGetTreeMethod())
              .addMethod(getCreateOrUpdateMethod())
              .addMethod(getDeleteMethod())
              .build();
        }
      }
    }
    return result;
  }
}
