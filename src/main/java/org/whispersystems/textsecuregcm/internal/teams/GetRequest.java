// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalTeamsService.proto

package org.whispersystems.textsecuregcm.internal.teams;

/**
 * Protobuf type {@code GetRequest}
 */
public  final class GetRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:GetRequest)
    GetRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GetRequest.newBuilder() to construct.
  private GetRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GetRequest() {
    teams_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    appid_ = "";
    pid_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GetRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GetRequest(
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
              teams_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000001;
            }
            teams_.add(bs);
            break;
          }
          case 18: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000001;
            appid_ = bs;
            break;
          }
          case 26: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000002;
            pid_ = bs;
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
        teams_ = teams_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.internal_static_GetRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.internal_static_GetRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.teams.GetRequest.class, org.whispersystems.textsecuregcm.internal.teams.GetRequest.Builder.class);
  }

  private int bitField0_;
  public static final int TEAMS_FIELD_NUMBER = 1;
  private com.google.protobuf.LazyStringList teams_;
  /**
   * <code>repeated string teams = 1;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getTeamsList() {
    return teams_;
  }
  /**
   * <code>repeated string teams = 1;</code>
   */
  public int getTeamsCount() {
    return teams_.size();
  }
  /**
   * <code>repeated string teams = 1;</code>
   */
  public java.lang.String getTeams(int index) {
    return teams_.get(index);
  }
  /**
   * <code>repeated string teams = 1;</code>
   */
  public com.google.protobuf.ByteString
      getTeamsBytes(int index) {
    return teams_.getByteString(index);
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

  public static final int PID_FIELD_NUMBER = 3;
  private volatile java.lang.Object pid_;
  /**
   * <code>optional string pid = 3;</code>
   */
  public boolean hasPid() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional string pid = 3;</code>
   */
  public java.lang.String getPid() {
    java.lang.Object ref = pid_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        pid_ = s;
      }
      return s;
    }
  }
  /**
   * <code>optional string pid = 3;</code>
   */
  public com.google.protobuf.ByteString
      getPidBytes() {
    java.lang.Object ref = pid_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      pid_ = b;
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
    for (int i = 0; i < teams_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, teams_.getRaw(i));
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, appid_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, pid_);
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
      for (int i = 0; i < teams_.size(); i++) {
        dataSize += computeStringSizeNoTag(teams_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getTeamsList().size();
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, appid_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, pid_);
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.teams.GetRequest)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.teams.GetRequest other = (org.whispersystems.textsecuregcm.internal.teams.GetRequest) obj;

    if (!getTeamsList()
        .equals(other.getTeamsList())) return false;
    if (hasAppid() != other.hasAppid()) return false;
    if (hasAppid()) {
      if (!getAppid()
          .equals(other.getAppid())) return false;
    }
    if (hasPid() != other.hasPid()) return false;
    if (hasPid()) {
      if (!getPid()
          .equals(other.getPid())) return false;
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
    if (getTeamsCount() > 0) {
      hash = (37 * hash) + TEAMS_FIELD_NUMBER;
      hash = (53 * hash) + getTeamsList().hashCode();
    }
    if (hasAppid()) {
      hash = (37 * hash) + APPID_FIELD_NUMBER;
      hash = (53 * hash) + getAppid().hashCode();
    }
    if (hasPid()) {
      hash = (37 * hash) + PID_FIELD_NUMBER;
      hash = (53 * hash) + getPid().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.teams.GetRequest prototype) {
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
   * Protobuf type {@code GetRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:GetRequest)
      org.whispersystems.textsecuregcm.internal.teams.GetRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.internal_static_GetRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.internal_static_GetRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.teams.GetRequest.class, org.whispersystems.textsecuregcm.internal.teams.GetRequest.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.teams.GetRequest.newBuilder()
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
      teams_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      appid_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      pid_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.teams.InternalTeamsServiceOuterClass.internal_static_GetRequest_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.teams.GetRequest getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.teams.GetRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.teams.GetRequest build() {
      org.whispersystems.textsecuregcm.internal.teams.GetRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.teams.GetRequest buildPartial() {
      org.whispersystems.textsecuregcm.internal.teams.GetRequest result = new org.whispersystems.textsecuregcm.internal.teams.GetRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        teams_ = teams_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.teams_ = teams_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.appid_ = appid_;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.pid_ = pid_;
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.teams.GetRequest) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.teams.GetRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.teams.GetRequest other) {
      if (other == org.whispersystems.textsecuregcm.internal.teams.GetRequest.getDefaultInstance()) return this;
      if (!other.teams_.isEmpty()) {
        if (teams_.isEmpty()) {
          teams_ = other.teams_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureTeamsIsMutable();
          teams_.addAll(other.teams_);
        }
        onChanged();
      }
      if (other.hasAppid()) {
        bitField0_ |= 0x00000002;
        appid_ = other.appid_;
        onChanged();
      }
      if (other.hasPid()) {
        bitField0_ |= 0x00000004;
        pid_ = other.pid_;
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
      org.whispersystems.textsecuregcm.internal.teams.GetRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.teams.GetRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private com.google.protobuf.LazyStringList teams_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureTeamsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        teams_ = new com.google.protobuf.LazyStringArrayList(teams_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getTeamsList() {
      return teams_.getUnmodifiableView();
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public int getTeamsCount() {
      return teams_.size();
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public java.lang.String getTeams(int index) {
      return teams_.get(index);
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public com.google.protobuf.ByteString
        getTeamsBytes(int index) {
      return teams_.getByteString(index);
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public Builder setTeams(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTeamsIsMutable();
      teams_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public Builder addTeams(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTeamsIsMutable();
      teams_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public Builder addAllTeams(
        java.lang.Iterable<java.lang.String> values) {
      ensureTeamsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, teams_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public Builder clearTeams() {
      teams_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string teams = 1;</code>
     */
    public Builder addTeamsBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTeamsIsMutable();
      teams_.add(value);
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

    private java.lang.Object pid_ = "";
    /**
     * <code>optional string pid = 3;</code>
     */
    public boolean hasPid() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string pid = 3;</code>
     */
    public java.lang.String getPid() {
      java.lang.Object ref = pid_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          pid_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string pid = 3;</code>
     */
    public com.google.protobuf.ByteString
        getPidBytes() {
      java.lang.Object ref = pid_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        pid_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string pid = 3;</code>
     */
    public Builder setPid(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      pid_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string pid = 3;</code>
     */
    public Builder clearPid() {
      bitField0_ = (bitField0_ & ~0x00000004);
      pid_ = getDefaultInstance().getPid();
      onChanged();
      return this;
    }
    /**
     * <code>optional string pid = 3;</code>
     */
    public Builder setPidBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      pid_ = value;
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


    // @@protoc_insertion_point(builder_scope:GetRequest)
  }

  // @@protoc_insertion_point(class_scope:GetRequest)
  private static final org.whispersystems.textsecuregcm.internal.teams.GetRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.teams.GetRequest();
  }

  public static org.whispersystems.textsecuregcm.internal.teams.GetRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<GetRequest>
      PARSER = new com.google.protobuf.AbstractParser<GetRequest>() {
    @java.lang.Override
    public GetRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GetRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GetRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.teams.GetRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

