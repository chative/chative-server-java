package org.whispersystems.textsecuregcm.internal.timedtask;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: InternalTimedTaskService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InternalTimedTaskServiceGrpc {

  private InternalTimedTaskServiceGrpc() {}

  public static final String SERVICE_NAME = "InternalTimedTaskService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "clearExpiredMsg",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgMethod;
    if ((getClearExpiredMsgMethod = InternalTimedTaskServiceGrpc.getClearExpiredMsgMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getClearExpiredMsgMethod = InternalTimedTaskServiceGrpc.getClearExpiredMsgMethod) == null) {
          InternalTimedTaskServiceGrpc.getClearExpiredMsgMethod = getClearExpiredMsgMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "clearExpiredMsg"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("clearExpiredMsg"))
              .build();
        }
      }
    }
    return getClearExpiredMsgMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgForRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "clearExpiredMsgForRequest",
      requestType = org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgForRequestMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearExpiredMsgForRequestMethod;
    if ((getClearExpiredMsgForRequestMethod = InternalTimedTaskServiceGrpc.getClearExpiredMsgForRequestMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getClearExpiredMsgForRequestMethod = InternalTimedTaskServiceGrpc.getClearExpiredMsgForRequestMethod) == null) {
          InternalTimedTaskServiceGrpc.getClearExpiredMsgForRequestMethod = getClearExpiredMsgForRequestMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "clearExpiredMsgForRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("clearExpiredMsgForRequest"))
              .build();
        }
      }
    }
    return getClearExpiredMsgForRequestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearInactiveGroupsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "clearInactiveGroups",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearInactiveGroupsMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getClearInactiveGroupsMethod;
    if ((getClearInactiveGroupsMethod = InternalTimedTaskServiceGrpc.getClearInactiveGroupsMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getClearInactiveGroupsMethod = InternalTimedTaskServiceGrpc.getClearInactiveGroupsMethod) == null) {
          InternalTimedTaskServiceGrpc.getClearInactiveGroupsMethod = getClearInactiveGroupsMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "clearInactiveGroups"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("clearInactiveGroups"))
              .build();
        }
      }
    }
    return getClearInactiveGroupsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getMessageNotReceivedRemindMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "messageNotReceivedRemind",
      requestType = org.whispersystems.textsecuregcm.internal.common.Empty.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getMessageNotReceivedRemindMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getMessageNotReceivedRemindMethod;
    if ((getMessageNotReceivedRemindMethod = InternalTimedTaskServiceGrpc.getMessageNotReceivedRemindMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getMessageNotReceivedRemindMethod = InternalTimedTaskServiceGrpc.getMessageNotReceivedRemindMethod) == null) {
          InternalTimedTaskServiceGrpc.getMessageNotReceivedRemindMethod = getMessageNotReceivedRemindMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.common.Empty, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "messageNotReceivedRemind"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("messageNotReceivedRemind"))
              .build();
        }
      }
    }
    return getMessageNotReceivedRemindMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveAccountRemindMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "inactiveAccountRemind",
      requestType = org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveAccountRemindMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getInactiveAccountRemindMethod;
    if ((getInactiveAccountRemindMethod = InternalTimedTaskServiceGrpc.getInactiveAccountRemindMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getInactiveAccountRemindMethod = InternalTimedTaskServiceGrpc.getInactiveAccountRemindMethod) == null) {
          InternalTimedTaskServiceGrpc.getInactiveAccountRemindMethod = getInactiveAccountRemindMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "inactiveAccountRemind"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("inactiveAccountRemind"))
              .build();
        }
      }
    }
    return getInactiveAccountRemindMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getHiddenNotActiveAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "hiddenNotActiveAccount",
      requestType = org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getHiddenNotActiveAccountMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getHiddenNotActiveAccountMethod;
    if ((getHiddenNotActiveAccountMethod = InternalTimedTaskServiceGrpc.getHiddenNotActiveAccountMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getHiddenNotActiveAccountMethod = InternalTimedTaskServiceGrpc.getHiddenNotActiveAccountMethod) == null) {
          InternalTimedTaskServiceGrpc.getHiddenNotActiveAccountMethod = getHiddenNotActiveAccountMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "hiddenNotActiveAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("hiddenNotActiveAccount"))
              .build();
        }
      }
    }
    return getHiddenNotActiveAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGroupCycleRemindMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "groupCycleRemind",
      requestType = org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGroupCycleRemindMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getGroupCycleRemindMethod;
    if ((getGroupCycleRemindMethod = InternalTimedTaskServiceGrpc.getGroupCycleRemindMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getGroupCycleRemindMethod = InternalTimedTaskServiceGrpc.getGroupCycleRemindMethod) == null) {
          InternalTimedTaskServiceGrpc.getGroupCycleRemindMethod = getGroupCycleRemindMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "groupCycleRemind"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("groupCycleRemind"))
              .build();
        }
      }
    }
    return getGroupCycleRemindMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getNotifyMergeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "notifyMerge",
      requestType = org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest.class,
      responseType = org.whispersystems.textsecuregcm.internal.common.BaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest,
      org.whispersystems.textsecuregcm.internal.common.BaseResponse> getNotifyMergeMethod() {
    io.grpc.MethodDescriptor<org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse> getNotifyMergeMethod;
    if ((getNotifyMergeMethod = InternalTimedTaskServiceGrpc.getNotifyMergeMethod) == null) {
      synchronized (InternalTimedTaskServiceGrpc.class) {
        if ((getNotifyMergeMethod = InternalTimedTaskServiceGrpc.getNotifyMergeMethod) == null) {
          InternalTimedTaskServiceGrpc.getNotifyMergeMethod = getNotifyMergeMethod =
              io.grpc.MethodDescriptor.<org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest, org.whispersystems.textsecuregcm.internal.common.BaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "notifyMerge"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.whispersystems.textsecuregcm.internal.common.BaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InternalTimedTaskServiceMethodDescriptorSupplier("notifyMerge"))
              .build();
        }
      }
    }
    return getNotifyMergeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InternalTimedTaskServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceStub>() {
        @java.lang.Override
        public InternalTimedTaskServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTimedTaskServiceStub(channel, callOptions);
        }
      };
    return InternalTimedTaskServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InternalTimedTaskServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceBlockingStub>() {
        @java.lang.Override
        public InternalTimedTaskServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTimedTaskServiceBlockingStub(channel, callOptions);
        }
      };
    return InternalTimedTaskServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InternalTimedTaskServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InternalTimedTaskServiceFutureStub>() {
        @java.lang.Override
        public InternalTimedTaskServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InternalTimedTaskServiceFutureStub(channel, callOptions);
        }
      };
    return InternalTimedTaskServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InternalTimedTaskServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public void clearExpiredMsg(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClearExpiredMsgMethod(), responseObserver);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public void clearExpiredMsgForRequest(org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClearExpiredMsgForRequestMethod(), responseObserver);
    }

    /**
     * <pre>
     *  * 次设备relink：30天以上未活跃
     *  rpc clearInactiveDevices(Empty) returns (BaseResponse);
     *  * groups过期: 30天以上没有活动
     * </pre>
     */
    public void clearInactiveGroups(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClearInactiveGroupsMethod(), responseObserver);
    }

    /**
     * <pre>
     *  * 未接收消息邮件提醒
     * </pre>
     */
    public void messageNotReceivedRemind(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMessageNotReceivedRemindMethod(), responseObserver);
    }

    /**
     * <pre>
     *  * 非活跃用户邮件提醒
     * </pre>
     */
    public void inactiveAccountRemind(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInactiveAccountRemindMethod(), responseObserver);
    }

    /**
     * <pre>
     *   * 隐藏不活跃用户
     * </pre>
     */
    public void hiddenNotActiveAccount(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHiddenNotActiveAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * * 群周期性提醒
     * </pre>
     */
    public void groupCycleRemind(org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGroupCycleRemindMethod(), responseObserver);
    }

    /**
     */
    public void notifyMerge(org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getNotifyMergeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getClearExpiredMsgMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CLEAR_EXPIRED_MSG)))
          .addMethod(
            getClearExpiredMsgForRequestMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CLEAR_EXPIRED_MSG_FOR_REQUEST)))
          .addMethod(
            getClearInactiveGroupsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_CLEAR_INACTIVE_GROUPS)))
          .addMethod(
            getMessageNotReceivedRemindMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.common.Empty,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_MESSAGE_NOT_RECEIVED_REMIND)))
          .addMethod(
            getInactiveAccountRemindMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_INACTIVE_ACCOUNT_REMIND)))
          .addMethod(
            getHiddenNotActiveAccountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_HIDDEN_NOT_ACTIVE_ACCOUNT)))
          .addMethod(
            getGroupCycleRemindMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_GROUP_CYCLE_REMIND)))
          .addMethod(
            getNotifyMergeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest,
                org.whispersystems.textsecuregcm.internal.common.BaseResponse>(
                  this, METHODID_NOTIFY_MERGE)))
          .build();
    }
  }

  /**
   */
  public static final class InternalTimedTaskServiceStub extends io.grpc.stub.AbstractAsyncStub<InternalTimedTaskServiceStub> {
    private InternalTimedTaskServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTimedTaskServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTimedTaskServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public void clearExpiredMsg(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClearExpiredMsgMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public void clearExpiredMsgForRequest(org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClearExpiredMsgForRequestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *  * 次设备relink：30天以上未活跃
     *  rpc clearInactiveDevices(Empty) returns (BaseResponse);
     *  * groups过期: 30天以上没有活动
     * </pre>
     */
    public void clearInactiveGroups(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClearInactiveGroupsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *  * 未接收消息邮件提醒
     * </pre>
     */
    public void messageNotReceivedRemind(org.whispersystems.textsecuregcm.internal.common.Empty request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getMessageNotReceivedRemindMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *  * 非活跃用户邮件提醒
     * </pre>
     */
    public void inactiveAccountRemind(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInactiveAccountRemindMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *   * 隐藏不活跃用户
     * </pre>
     */
    public void hiddenNotActiveAccount(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHiddenNotActiveAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * * 群周期性提醒
     * </pre>
     */
    public void groupCycleRemind(org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGroupCycleRemindMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void notifyMerge(org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest request,
        io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getNotifyMergeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InternalTimedTaskServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InternalTimedTaskServiceBlockingStub> {
    private InternalTimedTaskServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTimedTaskServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTimedTaskServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse clearExpiredMsg(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClearExpiredMsgMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse clearExpiredMsgForRequest(org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClearExpiredMsgForRequestMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *  * 次设备relink：30天以上未活跃
     *  rpc clearInactiveDevices(Empty) returns (BaseResponse);
     *  * groups过期: 30天以上没有活动
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse clearInactiveGroups(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClearInactiveGroupsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *  * 未接收消息邮件提醒
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse messageNotReceivedRemind(org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMessageNotReceivedRemindMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *  * 非活跃用户邮件提醒
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse inactiveAccountRemind(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInactiveAccountRemindMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *   * 隐藏不活跃用户
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse hiddenNotActiveAccount(org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHiddenNotActiveAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * * 群周期性提醒
     * </pre>
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse groupCycleRemind(org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGroupCycleRemindMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.whispersystems.textsecuregcm.internal.common.BaseResponse notifyMerge(org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getNotifyMergeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InternalTimedTaskServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InternalTimedTaskServiceFutureStub> {
    private InternalTimedTaskServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InternalTimedTaskServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InternalTimedTaskServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> clearExpiredMsg(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClearExpiredMsgMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *  * 清理通知消息：30天以上
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> clearExpiredMsgForRequest(
        org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClearExpiredMsgForRequestMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *  * 次设备relink：30天以上未活跃
     *  rpc clearInactiveDevices(Empty) returns (BaseResponse);
     *  * groups过期: 30天以上没有活动
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> clearInactiveGroups(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClearInactiveGroupsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *  * 未接收消息邮件提醒
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> messageNotReceivedRemind(
        org.whispersystems.textsecuregcm.internal.common.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getMessageNotReceivedRemindMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *  * 非活跃用户邮件提醒
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> inactiveAccountRemind(
        org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInactiveAccountRemindMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *   * 隐藏不活跃用户
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> hiddenNotActiveAccount(
        org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHiddenNotActiveAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * * 群周期性提醒
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> groupCycleRemind(
        org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGroupCycleRemindMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.whispersystems.textsecuregcm.internal.common.BaseResponse> notifyMerge(
        org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getNotifyMergeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CLEAR_EXPIRED_MSG = 0;
  private static final int METHODID_CLEAR_EXPIRED_MSG_FOR_REQUEST = 1;
  private static final int METHODID_CLEAR_INACTIVE_GROUPS = 2;
  private static final int METHODID_MESSAGE_NOT_RECEIVED_REMIND = 3;
  private static final int METHODID_INACTIVE_ACCOUNT_REMIND = 4;
  private static final int METHODID_HIDDEN_NOT_ACTIVE_ACCOUNT = 5;
  private static final int METHODID_GROUP_CYCLE_REMIND = 6;
  private static final int METHODID_NOTIFY_MERGE = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InternalTimedTaskServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InternalTimedTaskServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CLEAR_EXPIRED_MSG:
          serviceImpl.clearExpiredMsg((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_CLEAR_EXPIRED_MSG_FOR_REQUEST:
          serviceImpl.clearExpiredMsgForRequest((org.whispersystems.textsecuregcm.internal.timedtask.ExpiredMsgRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_CLEAR_INACTIVE_GROUPS:
          serviceImpl.clearInactiveGroups((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_MESSAGE_NOT_RECEIVED_REMIND:
          serviceImpl.messageNotReceivedRemind((org.whispersystems.textsecuregcm.internal.common.Empty) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_INACTIVE_ACCOUNT_REMIND:
          serviceImpl.inactiveAccountRemind((org.whispersystems.textsecuregcm.internal.timedtask.InactiveRemindRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_HIDDEN_NOT_ACTIVE_ACCOUNT:
          serviceImpl.hiddenNotActiveAccount((org.whispersystems.textsecuregcm.internal.timedtask.InactiveRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_GROUP_CYCLE_REMIND:
          serviceImpl.groupCycleRemind((org.whispersystems.textsecuregcm.internal.timedtask.GroupRemindRequest) request,
              (io.grpc.stub.StreamObserver<org.whispersystems.textsecuregcm.internal.common.BaseResponse>) responseObserver);
          break;
        case METHODID_NOTIFY_MERGE:
          serviceImpl.notifyMerge((org.whispersystems.textsecuregcm.internal.timedtask.NotifyRequest) request,
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

  private static abstract class InternalTimedTaskServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InternalTimedTaskServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.whispersystems.textsecuregcm.internal.timedtask.InternalTimedTaskServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InternalTimedTaskService");
    }
  }

  private static final class InternalTimedTaskServiceFileDescriptorSupplier
      extends InternalTimedTaskServiceBaseDescriptorSupplier {
    InternalTimedTaskServiceFileDescriptorSupplier() {}
  }

  private static final class InternalTimedTaskServiceMethodDescriptorSupplier
      extends InternalTimedTaskServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InternalTimedTaskServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (InternalTimedTaskServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InternalTimedTaskServiceFileDescriptorSupplier())
              .addMethod(getClearExpiredMsgMethod())
              .addMethod(getClearExpiredMsgForRequestMethod())
              .addMethod(getClearInactiveGroupsMethod())
              .addMethod(getMessageNotReceivedRemindMethod())
              .addMethod(getInactiveAccountRemindMethod())
              .addMethod(getHiddenNotActiveAccountMethod())
              .addMethod(getGroupCycleRemindMethod())
              .addMethod(getNotifyMergeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
