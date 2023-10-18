package org.whispersystems.textsecuregcm.internal.invitation;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalAccountsInvitationService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalAccountsInvitationServiceGrpc {

  private InternalAccountsInvitationServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalAccountsInvitationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest,
      org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> getGetAllMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getAll",
      requestType = org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest,
      org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> getGetAllMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest, org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> getGetAllMethod;
    if ((getGetAllMethod = InternalAccountsInvitationServiceGrpc.getGetAllMethod) == null) {
      synchronized (InternalAccountsInvitationServiceGrpc.class) {
        if ((getGetAllMethod = InternalAccountsInvitationServiceGrpc.getGetAllMethod) == null) {
          InternalAccountsInvitationServiceGrpc.getGetAllMethod = getGetAllMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest, org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getAll"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsInvitationServiceMethodDescriptorSupplier("getAll"))
              .build();
        }
      }
    }
    return getGetAllMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenerateInvitationCodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "generateInvitationCode",
      requestType = org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenerateInvitationCodeMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGenerateInvitationCodeMethod;
    if ((getGenerateInvitationCodeMethod = InternalAccountsInvitationServiceGrpc.getGenerateInvitationCodeMethod) == null) {
      synchronized (InternalAccountsInvitationServiceGrpc.class) {
        if ((getGenerateInvitationCodeMethod = InternalAccountsInvitationServiceGrpc.getGenerateInvitationCodeMethod) == null) {
          InternalAccountsInvitationServiceGrpc.getGenerateInvitationCodeMethod = getGenerateInvitationCodeMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "generateInvitationCode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsInvitationServiceMethodDescriptorSupplier("generateInvitationCode"))
              .build();
        }
      }
    }
    return getGenerateInvitationCodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "update",
      requestType = org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateMethod;
    if ((getUpdateMethod = InternalAccountsInvitationServiceGrpc.getUpdateMethod) == null) {
      synchronized (InternalAccountsInvitationServiceGrpc.class) {
        if ((getUpdateMethod = InternalAccountsInvitationServiceGrpc.getUpdateMethod) == null) {
          InternalAccountsInvitationServiceGrpc.getUpdateMethod = getUpdateMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "update"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsInvitationServiceMethodDescriptorSupplier("update"))
              .build();
        }
      }
    }
    return getUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateInvitationByEmailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateInvitationByEmail",
      requestType = org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateInvitationByEmailMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getUpdateInvitationByEmailMethod;
    if ((getUpdateInvitationByEmailMethod = InternalAccountsInvitationServiceGrpc.getUpdateInvitationByEmailMethod) == null) {
      synchronized (InternalAccountsInvitationServiceGrpc.class) {
        if ((getUpdateInvitationByEmailMethod = InternalAccountsInvitationServiceGrpc.getUpdateInvitationByEmailMethod) == null) {
          InternalAccountsInvitationServiceGrpc.getUpdateInvitationByEmailMethod = getUpdateInvitationByEmailMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateInvitationByEmail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalAccountsInvitationServiceMethodDescriptorSupplier("updateInvitationByEmail"))
              .build();
        }
      }
    }
    return getUpdateInvitationByEmailMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalAccountsInvitationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceStub>() {
        @java.lang.Override
        public InternalAccountsInvitationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsInvitationServiceStub(channel, callOptions);
        }
      };
    return InternalAccountsInvitationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalAccountsInvitationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceBlockingStub>() {
        @java.lang.Override
        public InternalAccountsInvitationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsInvitationServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalAccountsInvitationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalAccountsInvitationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalAccountsInvitationServiceFutureStub>() {
        @java.lang.Override
        public InternalAccountsInvitationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalAccountsInvitationServiceFutureStub(channel, callOptions);
        }
      };
    return InternalAccountsInvitationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalAccountsInvitationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllMethod(), responseObserver);
    }

    /**
     */
    public void generateInvitationCode(org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateInvitationCodeMethod(), responseObserver);
    }

    /**
     */
    public void update(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateMethod(), responseObserver);
    }

    /**
     */
    public void updateInvitationByEmail(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateInvitationByEmailMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetAllMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest,
                org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse>(
                  this, METHODID_GET_ALL)))
          .addMethod(
            getGenerateInvitationCodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GENERATE_INVITATION_CODE)))
          .addMethod(
            getUpdateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_UPDATE)))
          .addMethod(
            getUpdateInvitationByEmailMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_UPDATE_INVITATION_BY_EMAIL)))
          .build();
    }
  }

  /**
   */
  public static final class InternalAccountsInvitationServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalAccountsInvitationServiceStub> {
    private InternalAccountsInvitationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsInvitationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsInvitationServiceStub(channel, callOptions);
    }

    /**
     */
    public void getAll(org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void generateInvitationCode(org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateInvitationCodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void update(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateInvitationByEmail(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateInvitationByEmailMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalAccountsInvitationServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalAccountsInvitationServiceBlockingStub> {
    private InternalAccountsInvitationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsInvitationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsInvitationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse getAll(org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse generateInvitationCode(org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateInvitationCodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse update(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse updateInvitationByEmail(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateInvitationByEmailMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalAccountsInvitationServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalAccountsInvitationServiceFutureStub> {
    private InternalAccountsInvitationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalAccountsInvitationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalAccountsInvitationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse> getAll(
        org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> generateInvitationCode(
        org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateInvitationCodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> update(
        org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> updateInvitationByEmail(
        org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateInvitationByEmailMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ALL = 0;
  private static final int METHODID_GENERATE_INVITATION_CODE = 1;
  private static final int METHODID_UPDATE = 2;
  private static final int METHODID_UPDATE_INVITATION_BY_EMAIL = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalAccountsInvitationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalAccountsInvitationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ALL:
          serviceImpl.getAll((org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.invitation.InvitationResponse>) responseObserver);
          break;
        case METHODID_GENERATE_INVITATION_CODE:
          serviceImpl.generateInvitationCode((org.whispersystems.textsecuregcm.internal.invitation.GenerateInvitationCode) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_UPDATE:
          serviceImpl.update((org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_UPDATE_INVITATION_BY_EMAIL:
          serviceImpl.updateInvitationByEmail((org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest) request,
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

  private static abstract class InternalAccountsInvitationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalAccountsInvitationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalAccountsInvitationService");
    }
  }

  private static final class InternalAccountsInvitationServiceFileDescriptorSupplier
      extends InternalAccountsInvitationServiceBaseDescriptorSupplier {
    InternalAccountsInvitationServiceFileDescriptorSupplier() {}
  }

  private static final class InternalAccountsInvitationServiceMethodDescriptorSupplier
      extends InternalAccountsInvitationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalAccountsInvitationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalAccountsInvitationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalAccountsInvitationServiceFileDescriptorSupplier())
              .addMethod(getGetAllMethod())
              .addMethod(getGenerateInvitationCodeMethod())
              .addMethod(getUpdateMethod())
              .addMethod(getUpdateInvitationByEmailMethod())
              .build();
        }
      }
    }
    return result;
  }
}
