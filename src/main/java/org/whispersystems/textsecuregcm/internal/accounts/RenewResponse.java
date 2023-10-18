// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

/**
 * Protobuf type {@code RenewResponse}
 */
public  final class RenewResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:RenewResponse)
    RenewResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use RenewResponse.newBuilder() to construct.
  private RenewResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private RenewResponse() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new RenewResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private RenewResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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
          case 8: {
            bitField0_ |= 0x00000001;
            vcode_ = input.readUInt32();
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
    return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_RenewResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_RenewResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.class, org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.Builder.class);
  }

  private int bitField0_;
  public static final int VCODE_FIELD_NUMBER = 1;
  private int vcode_;
  /**
   * <code>required uint32 vcode = 1;</code>
   */
  public boolean hasVcode() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>required uint32 vcode = 1;</code>
   */
  public int getVcode() {
    return vcode_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasVcode()) {
      memoizedIsInitialized = 0;
      return false;
    }
    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeUInt32(1, vcode_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, vcode_);
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.accounts.RenewResponse)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.accounts.RenewResponse other = (org.whispersystems.textsecuregcm.internal.accounts.RenewResponse) obj;

    if (hasVcode() != other.hasVcode()) return false;
    if (hasVcode()) {
      if (getVcode()
          != other.getVcode()) return false;
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
    if (hasVcode()) {
      hash = (37 * hash) + VCODE_FIELD_NUMBER;
      hash = (53 * hash) + getVcode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.accounts.RenewResponse prototype) {
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
   * Protobuf type {@code RenewResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:RenewResponse)
      org.whispersystems.textsecuregcm.internal.accounts.RenewResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_RenewResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_RenewResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.class, org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.newBuilder()
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
      vcode_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_RenewResponse_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.RenewResponse getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.RenewResponse build() {
      org.whispersystems.textsecuregcm.internal.accounts.RenewResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.RenewResponse buildPartial() {
      org.whispersystems.textsecuregcm.internal.accounts.RenewResponse result = new org.whispersystems.textsecuregcm.internal.accounts.RenewResponse(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.vcode_ = vcode_;
        to_bitField0_ |= 0x00000001;
      }
      result.bitField0_ = to_bitField0_;
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.accounts.RenewResponse) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.accounts.RenewResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.accounts.RenewResponse other) {
      if (other == org.whispersystems.textsecuregcm.internal.accounts.RenewResponse.getDefaultInstance()) return this;
      if (other.hasVcode()) {
        setVcode(other.getVcode());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      if (!hasVcode()) {
        return false;
      }
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.whispersystems.textsecuregcm.internal.accounts.RenewResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.accounts.RenewResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int vcode_ ;
    /**
     * <code>required uint32 vcode = 1;</code>
     */
    public boolean hasVcode() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required uint32 vcode = 1;</code>
     */
    public int getVcode() {
      return vcode_;
    }
    /**
     * <code>required uint32 vcode = 1;</code>
     */
    public Builder setVcode(int value) {
      bitField0_ |= 0x00000001;
      vcode_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required uint32 vcode = 1;</code>
     */
    public Builder clearVcode() {
      bitField0_ = (bitField0_ & ~0x00000001);
      vcode_ = 0;
      onChanged();
      return this;
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


    // @@protoc_insertion_point(builder_scope:RenewResponse)
  }

  // @@protoc_insertion_point(class_scope:RenewResponse)
  private static final org.whispersystems.textsecuregcm.internal.accounts.RenewResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.accounts.RenewResponse();
  }

  public static org.whispersystems.textsecuregcm.internal.accounts.RenewResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<RenewResponse>
      PARSER = new com.google.protobuf.AbstractParser<RenewResponse>() {
    @java.lang.Override
    public RenewResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new RenewResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<RenewResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<RenewResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.accounts.RenewResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

