package com.github.difftim.register;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: register.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RegiterGrpc {

  private RegiterGrpc() {}

  public static final String SERVICE_NAME = "pb.Regiter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.difftim.register.SendSMSRequest,
      com.github.difftim.register.SendSMSResponse> getSendSMSMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendSMS",
      requestType = com.github.difftim.register.SendSMSRequest.class,
      responseType = com.github.difftim.register.SendSMSResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.register.SendSMSRequest,
      com.github.difftim.register.SendSMSResponse> getSendSMSMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.register.SendSMSRequest, com.github.difftim.register.SendSMSResponse> getSendSMSMethod;
    if ((getSendSMSMethod = RegiterGrpc.getSendSMSMethod) == null) {
      synchronized (RegiterGrpc.class) {
        if ((getSendSMSMethod = RegiterGrpc.getSendSMSMethod) == null) {
          RegiterGrpc.getSendSMSMethod = getSendSMSMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.register.SendSMSRequest, com.github.difftim.register.SendSMSResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendSMS"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.register.SendSMSRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.register.SendSMSResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RegiterMethodDescriptorSupplier("SendSMS"))
              .build();
        }
      }
    }
    return getSendSMSMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.difftim.register.VerifySMSRequest,
      com.github.difftim.register.VerifySMSResponse> getVerifySMSMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifySMS",
      requestType = com.github.difftim.register.VerifySMSRequest.class,
      responseType = com.github.difftim.register.VerifySMSResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.difftim.register.VerifySMSRequest,
      com.github.difftim.register.VerifySMSResponse> getVerifySMSMethod() {
    io.grpc.MethodDescriptor<com.github.difftim.register.VerifySMSRequest, com.github.difftim.register.VerifySMSResponse> getVerifySMSMethod;
    if ((getVerifySMSMethod = RegiterGrpc.getVerifySMSMethod) == null) {
      synchronized (RegiterGrpc.class) {
        if ((getVerifySMSMethod = RegiterGrpc.getVerifySMSMethod) == null) {
          RegiterGrpc.getVerifySMSMethod = getVerifySMSMethod =
              io.grpc.MethodDescriptor.<com.github.difftim.register.VerifySMSRequest, com.github.difftim.register.VerifySMSResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifySMS"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.register.VerifySMSRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.difftim.register.VerifySMSResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RegiterMethodDescriptorSupplier("VerifySMS"))
              .build();
        }
      }
    }
    return getVerifySMSMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegiterStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegiterStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegiterStub>() {
        @java.lang.Override
        public RegiterStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegiterStub(channel, callOptions);
        }
      };
    return RegiterStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegiterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegiterBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegiterBlockingStub>() {
        @java.lang.Override
        public RegiterBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegiterBlockingStub(channel, callOptions);
        }
      };
    return RegiterBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegiterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegiterFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegiterFutureStub>() {
        @java.lang.Override
        public RegiterFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegiterFutureStub(channel, callOptions);
        }
      };
    return RegiterFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class RegiterImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendSMS(com.github.difftim.register.SendSMSRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.register.SendSMSResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendSMSMethod(), responseObserver);
    }

    /**
     */
    public void verifySMS(com.github.difftim.register.VerifySMSRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.register.VerifySMSResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifySMSMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendSMSMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.register.SendSMSRequest,
                com.github.difftim.register.SendSMSResponse>(
                  this, METHODID_SEND_SMS)))
          .addMethod(
            getVerifySMSMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.difftim.register.VerifySMSRequest,
                com.github.difftim.register.VerifySMSResponse>(
                  this, METHODID_VERIFY_SMS)))
          .build();
    }
  }

  /**
   */
  public static final class RegiterStub extends io.grpc.stub.AbstractAsyncStub<RegiterStub> {
    private RegiterStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegiterStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegiterStub(channel, callOptions);
    }

    /**
     */
    public void sendSMS(com.github.difftim.register.SendSMSRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.register.SendSMSResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendSMSMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifySMS(com.github.difftim.register.VerifySMSRequest request,
        io.grpc.stub.StreamObserver<com.github.difftim.register.VerifySMSResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifySMSMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RegiterBlockingStub extends io.grpc.stub.AbstractBlockingStub<RegiterBlockingStub> {
    private RegiterBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegiterBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegiterBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.difftim.register.SendSMSResponse sendSMS(com.github.difftim.register.SendSMSRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendSMSMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.difftim.register.VerifySMSResponse verifySMS(com.github.difftim.register.VerifySMSRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifySMSMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RegiterFutureStub extends io.grpc.stub.AbstractFutureStub<RegiterFutureStub> {
    private RegiterFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegiterFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegiterFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.register.SendSMSResponse> sendSMS(
        com.github.difftim.register.SendSMSRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendSMSMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.difftim.register.VerifySMSResponse> verifySMS(
        com.github.difftim.register.VerifySMSRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifySMSMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_SMS = 0;
  private static final int METHODID_VERIFY_SMS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegiterImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegiterImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_SMS:
          serviceImpl.sendSMS((com.github.difftim.register.SendSMSRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.register.SendSMSResponse>) responseObserver);
          break;
        case METHODID_VERIFY_SMS:
          serviceImpl.verifySMS((com.github.difftim.register.VerifySMSRequest) request,
              (io.grpc.stub.StreamObserver<com.github.difftim.register.VerifySMSResponse>) responseObserver);
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

  private static abstract class RegiterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegiterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.difftim.register.Register.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Regiter");
    }
  }

  private static final class RegiterFileDescriptorSupplier
      extends RegiterBaseDescriptorSupplier {
    RegiterFileDescriptorSupplier() {}
  }

  private static final class RegiterMethodDescriptorSupplier
      extends RegiterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegiterMethodDescriptorSupplier(String methodName) {
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
      synchronized (RegiterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegiterFileDescriptorSupplier())
              .addMethod(getSendSMSMethod())
              .addMethod(getVerifySMSMethod())
              .build();
        }
      }
    }
    return result;
  }
}
