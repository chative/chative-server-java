// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: account.proto

package com.github.difftim.accountgrpc;

/**
 * Protobuf type {@code pb.DeleteAccResp}
 */
public  final class DeleteAccResp extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:pb.DeleteAccResp)
    DeleteAccRespOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DeleteAccResp.newBuilder() to construct.
  private DeleteAccResp(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DeleteAccResp() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new DeleteAccResp();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DeleteAccResp(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            com.github.difftim.common.BaseResponse.Builder subBuilder = null;
            if (base_ != null) {
              subBuilder = base_.toBuilder();
            }
            base_ = input.readMessage(com.github.difftim.common.BaseResponse.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(base_);
              base_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.github.difftim.accountgrpc.AccountOuterClass.internal_static_pb_DeleteAccResp_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.github.difftim.accountgrpc.AccountOuterClass.internal_static_pb_DeleteAccResp_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.github.difftim.accountgrpc.DeleteAccResp.class, com.github.difftim.accountgrpc.DeleteAccResp.Builder.class);
  }

  public static final int BASE_FIELD_NUMBER = 1;
  private com.github.difftim.common.BaseResponse base_;
  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  public boolean hasBase() {
    return base_ != null;
  }
  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  public com.github.difftim.common.BaseResponse getBase() {
    return base_ == null ? com.github.difftim.common.BaseResponse.getDefaultInstance() : base_;
  }
  /**
   * <code>.pb.BaseResponse base = 1;</code>
   */
  public com.github.difftim.common.BaseResponseOrBuilder getBaseOrBuilder() {
    return getBase();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (base_ != null) {
      output.writeMessage(1, getBase());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (base_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getBase());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.github.difftim.accountgrpc.DeleteAccResp)) {
      return super.equals(obj);
    }
    com.github.difftim.accountgrpc.DeleteAccResp other = (com.github.difftim.accountgrpc.DeleteAccResp) obj;

    if (hasBase() != other.hasBase()) return false;
    if (hasBase()) {
      if (!getBase()
          .equals(other.getBase())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasBase()) {
      hash = (37 * hash) + BASE_FIELD_NUMBER;
      hash = (53 * hash) + getBase().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.github.difftim.accountgrpc.DeleteAccResp parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.github.difftim.accountgrpc.DeleteAccResp prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code pb.DeleteAccResp}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:pb.DeleteAccResp)
      com.github.difftim.accountgrpc.DeleteAccRespOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.github.difftim.accountgrpc.AccountOuterClass.internal_static_pb_DeleteAccResp_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.github.difftim.accountgrpc.AccountOuterClass.internal_static_pb_DeleteAccResp_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.github.difftim.accountgrpc.DeleteAccResp.class, com.github.difftim.accountgrpc.DeleteAccResp.Builder.class);
    }

    // Construct using com.github.difftim.accountgrpc.DeleteAccResp.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (baseBuilder_ == null) {
        base_ = null;
      } else {
        base_ = null;
        baseBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.github.difftim.accountgrpc.AccountOuterClass.internal_static_pb_DeleteAccResp_descriptor;
    }

    @java.lang.Override
    public com.github.difftim.accountgrpc.DeleteAccResp getDefaultInstanceForType() {
      return com.github.difftim.accountgrpc.DeleteAccResp.getDefaultInstance();
    }

    @java.lang.Override
    public com.github.difftim.accountgrpc.DeleteAccResp build() {
      com.github.difftim.accountgrpc.DeleteAccResp result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.github.difftim.accountgrpc.DeleteAccResp buildPartial() {
      com.github.difftim.accountgrpc.DeleteAccResp result = new com.github.difftim.accountgrpc.DeleteAccResp(this);
      if (baseBuilder_ == null) {
        result.base_ = base_;
      } else {
        result.base_ = baseBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.github.difftim.accountgrpc.DeleteAccResp) {
        return mergeFrom((com.github.difftim.accountgrpc.DeleteAccResp)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.github.difftim.accountgrpc.DeleteAccResp other) {
      if (other == com.github.difftim.accountgrpc.DeleteAccResp.getDefaultInstance()) return this;
      if (other.hasBase()) {
        mergeBase(other.getBase());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.github.difftim.accountgrpc.DeleteAccResp parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.github.difftim.accountgrpc.DeleteAccResp) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.github.difftim.common.BaseResponse base_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.github.difftim.common.BaseResponse, com.github.difftim.common.BaseResponse.Builder, com.github.difftim.common.BaseResponseOrBuilder> baseBuilder_;
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public boolean hasBase() {
      return baseBuilder_ != null || base_ != null;
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public com.github.difftim.common.BaseResponse getBase() {
      if (baseBuilder_ == null) {
        return base_ == null ? com.github.difftim.common.BaseResponse.getDefaultInstance() : base_;
      } else {
        return baseBuilder_.getMessage();
      }
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public Builder setBase(com.github.difftim.common.BaseResponse value) {
      if (baseBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        base_ = value;
        onChanged();
      } else {
        baseBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public Builder setBase(
        com.github.difftim.common.BaseResponse.Builder builderForValue) {
      if (baseBuilder_ == null) {
        base_ = builderForValue.build();
        onChanged();
      } else {
        baseBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public Builder mergeBase(com.github.difftim.common.BaseResponse value) {
      if (baseBuilder_ == null) {
        if (base_ != null) {
          base_ =
            com.github.difftim.common.BaseResponse.newBuilder(base_).mergeFrom(value).buildPartial();
        } else {
          base_ = value;
        }
        onChanged();
      } else {
        baseBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public Builder clearBase() {
      if (baseBuilder_ == null) {
        base_ = null;
        onChanged();
      } else {
        base_ = null;
        baseBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public com.github.difftim.common.BaseResponse.Builder getBaseBuilder() {
      
      onChanged();
      return getBaseFieldBuilder().getBuilder();
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    public com.github.difftim.common.BaseResponseOrBuilder getBaseOrBuilder() {
      if (baseBuilder_ != null) {
        return baseBuilder_.getMessageOrBuilder();
      } else {
        return base_ == null ?
            com.github.difftim.common.BaseResponse.getDefaultInstance() : base_;
      }
    }
    /**
     * <code>.pb.BaseResponse base = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.github.difftim.common.BaseResponse, com.github.difftim.common.BaseResponse.Builder, com.github.difftim.common.BaseResponseOrBuilder> 
        getBaseFieldBuilder() {
      if (baseBuilder_ == null) {
        baseBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.github.difftim.common.BaseResponse, com.github.difftim.common.BaseResponse.Builder, com.github.difftim.common.BaseResponseOrBuilder>(
                getBase(),
                getParentForChildren(),
                isClean());
        base_ = null;
      }
      return baseBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:pb.DeleteAccResp)
  }

  // @@protoc_insertion_point(class_scope:pb.DeleteAccResp)
  private static final com.github.difftim.accountgrpc.DeleteAccResp DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.github.difftim.accountgrpc.DeleteAccResp();
  }

  public static com.github.difftim.accountgrpc.DeleteAccResp getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DeleteAccResp>
      PARSER = new com.google.protobuf.AbstractParser<DeleteAccResp>() {
    @java.lang.Override
    public DeleteAccResp parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DeleteAccResp(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DeleteAccResp> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DeleteAccResp> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.github.difftim.accountgrpc.DeleteAccResp getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

