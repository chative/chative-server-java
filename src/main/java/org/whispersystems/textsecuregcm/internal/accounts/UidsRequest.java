// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsService.proto

package org.whispersystems.textsecuregcm.internal.accounts;

/**
 * Protobuf type {@code UidsRequest}
 */
public  final class UidsRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:UidsRequest)
    UidsRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use UidsRequest.newBuilder() to construct.
  private UidsRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private UidsRequest() {
    uids_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    appid_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new UidsRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private UidsRequest(
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
          case 10: {
            com.google.protobuf.ByteString bs = input.readBytes();
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              uids_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000001;
            }
            uids_.add(bs);
            break;
          }
          case 18: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000001;
            appid_ = bs;
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
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        uids_ = uids_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_UidsRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_UidsRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class, org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.Builder.class);
  }

  private int bitField0_;
  public static final int UIDS_FIELD_NUMBER = 1;
  private com.google.protobuf.LazyStringList uids_;
  /**
   * <code>repeated string uids = 1;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getUidsList() {
    return uids_;
  }
  /**
   * <code>repeated string uids = 1;</code>
   */
  public int getUidsCount() {
    return uids_.size();
  }
  /**
   * <code>repeated string uids = 1;</code>
   */
  public java.lang.String getUids(int index) {
    return uids_.get(index);
  }
  /**
   * <code>repeated string uids = 1;</code>
   */
  public com.google.protobuf.ByteString
      getUidsBytes(int index) {
    return uids_.getByteString(index);
  }

  public static final int APPID_FIELD_NUMBER = 2;
  private volatile java.lang.Object appid_;
  /**
   * <code>optional string appid = 2;</code>
   */
  public boolean hasAppid() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional string appid = 2;</code>
   */
  public java.lang.String getAppid() {
    java.lang.Object ref = appid_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        appid_ = s;
      }
      return s;
    }
  }
  /**
   * <code>optional string appid = 2;</code>
   */
  public com.google.protobuf.ByteString
      getAppidBytes() {
    java.lang.Object ref = appid_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      appid_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    for (int i = 0; i < uids_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, uids_.getRaw(i));
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, appid_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    {
      int dataSize = 0;
      for (int i = 0; i < uids_.size(); i++) {
        dataSize += computeStringSizeNoTag(uids_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getUidsList().size();
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, appid_);
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.accounts.UidsRequest)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.accounts.UidsRequest other = (org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) obj;

    if (!getUidsList()
        .equals(other.getUidsList())) return false;
    if (hasAppid() != other.hasAppid()) return false;
    if (hasAppid()) {
      if (!getAppid()
          .equals(other.getAppid())) return false;
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
    if (getUidsCount() > 0) {
      hash = (37 * hash) + UIDS_FIELD_NUMBER;
      hash = (53 * hash) + getUidsList().hashCode();
    }
    if (hasAppid()) {
      hash = (37 * hash) + APPID_FIELD_NUMBER;
      hash = (53 * hash) + getAppid().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest prototype) {
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
   * Protobuf type {@code UidsRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:UidsRequest)
      org.whispersystems.textsecuregcm.internal.accounts.UidsRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_UidsRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_UidsRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.class, org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.newBuilder()
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
      uids_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      appid_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.accounts.InternalAccountsServiceOuterClass.internal_static_UidsRequest_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.UidsRequest getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.UidsRequest build() {
      org.whispersystems.textsecuregcm.internal.accounts.UidsRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.accounts.UidsRequest buildPartial() {
      org.whispersystems.textsecuregcm.internal.accounts.UidsRequest result = new org.whispersystems.textsecuregcm.internal.accounts.UidsRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        uids_ = uids_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.uids_ = uids_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.appid_ = appid_;
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.accounts.UidsRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.accounts.UidsRequest other) {
      if (other == org.whispersystems.textsecuregcm.internal.accounts.UidsRequest.getDefaultInstance()) return this;
      if (!other.uids_.isEmpty()) {
        if (uids_.isEmpty()) {
          uids_ = other.uids_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureUidsIsMutable();
          uids_.addAll(other.uids_);
        }
        onChanged();
      }
      if (other.hasAppid()) {
        bitField0_ |= 0x00000002;
        appid_ = other.appid_;
        onChanged();
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
      org.whispersystems.textsecuregcm.internal.accounts.UidsRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.accounts.UidsRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private com.google.protobuf.LazyStringList uids_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureUidsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        uids_ = new com.google.protobuf.LazyStringArrayList(uids_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getUidsList() {
      return uids_.getUnmodifiableView();
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public int getUidsCount() {
      return uids_.size();
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public java.lang.String getUids(int index) {
      return uids_.get(index);
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public com.google.protobuf.ByteString
        getUidsBytes(int index) {
      return uids_.getByteString(index);
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public Builder setUids(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureUidsIsMutable();
      uids_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public Builder addUids(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureUidsIsMutable();
      uids_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public Builder addAllUids(
        java.lang.Iterable<java.lang.String> values) {
      ensureUidsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, uids_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public Builder clearUids() {
      uids_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string uids = 1;</code>
     */
    public Builder addUidsBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureUidsIsMutable();
      uids_.add(value);
      onChanged();
      return this;
    }

    private java.lang.Object appid_ = "";
    /**
     * <code>optional string appid = 2;</code>
     */
    public boolean hasAppid() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string appid = 2;</code>
     */
    public java.lang.String getAppid() {
      java.lang.Object ref = appid_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          appid_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string appid = 2;</code>
     */
    public com.google.protobuf.ByteString
        getAppidBytes() {
      java.lang.Object ref = appid_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        appid_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string appid = 2;</code>
     */
    public Builder setAppid(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      appid_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string appid = 2;</code>
     */
    public Builder clearAppid() {
      bitField0_ = (bitField0_ & ~0x00000002);
      appid_ = getDefaultInstance().getAppid();
      onChanged();
      return this;
    }
    /**
     * <code>optional string appid = 2;</code>
     */
    public Builder setAppidBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      appid_ = value;
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


    // @@protoc_insertion_point(builder_scope:UidsRequest)
  }

  // @@protoc_insertion_point(class_scope:UidsRequest)
  private static final org.whispersystems.textsecuregcm.internal.accounts.UidsRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.accounts.UidsRequest();
  }

  public static org.whispersystems.textsecuregcm.internal.accounts.UidsRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<UidsRequest>
      PARSER = new com.google.protobuf.AbstractParser<UidsRequest>() {
    @java.lang.Override
    public UidsRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new UidsRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<UidsRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<UidsRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.accounts.UidsRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

