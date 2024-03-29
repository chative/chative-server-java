// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GroupService.proto

package org.whispersystems.textsecuregcm.internal.groups;

/**
 * Protobuf type {@code GroupsCreateRequest}
 */
public  final class GroupsCreateRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:GroupsCreateRequest)
    GroupsCreateRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GroupsCreateRequest.newBuilder() to construct.
  private GroupsCreateRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GroupsCreateRequest() {
    members_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GroupsCreateRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GroupsCreateRequest(
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
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder subBuilder = null;
            if (((bitField0_ & 0x00000001) != 0)) {
              subBuilder = groupsInfo_.toBuilder();
            }
            groupsInfo_ = input.readMessage(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(groupsInfo_);
              groupsInfo_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000001;
            break;
          }
          case 18: {
            com.google.protobuf.ByteString bs = input.readBytes();
            if (!((mutable_bitField0_ & 0x00000002) != 0)) {
              members_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000002;
            }
            members_.add(bs);
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
      if (((mutable_bitField0_ & 0x00000002) != 0)) {
        members_ = members_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsCreateRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsCreateRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.class, org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.Builder.class);
  }

  private int bitField0_;
  public static final int GROUPSINFO_FIELD_NUMBER = 1;
  private org.whispersystems.textsecuregcm.internal.groups.GroupsInfo groupsInfo_;
  /**
   * <code>required .GroupsInfo groupsInfo = 1;</code>
   */
  public boolean hasGroupsInfo() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>required .GroupsInfo groupsInfo = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupsInfo() {
    return groupsInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupsInfo_;
  }
  /**
   * <code>required .GroupsInfo groupsInfo = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupsInfoOrBuilder() {
    return groupsInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupsInfo_;
  }

  public static final int MEMBERS_FIELD_NUMBER = 2;
  private com.google.protobuf.LazyStringList members_;
  /**
   * <code>repeated string members = 2;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getMembersList() {
    return members_;
  }
  /**
   * <code>repeated string members = 2;</code>
   */
  public int getMembersCount() {
    return members_.size();
  }
  /**
   * <code>repeated string members = 2;</code>
   */
  public java.lang.String getMembers(int index) {
    return members_.get(index);
  }
  /**
   * <code>repeated string members = 2;</code>
   */
  public com.google.protobuf.ByteString
      getMembersBytes(int index) {
    return members_.getByteString(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasGroupsInfo()) {
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
      output.writeMessage(1, getGroupsInfo());
    }
    for (int i = 0; i < members_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, members_.getRaw(i));
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
        .computeMessageSize(1, getGroupsInfo());
    }
    {
      int dataSize = 0;
      for (int i = 0; i < members_.size(); i++) {
        dataSize += computeStringSizeNoTag(members_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getMembersList().size();
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest other = (org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) obj;

    if (hasGroupsInfo() != other.hasGroupsInfo()) return false;
    if (hasGroupsInfo()) {
      if (!getGroupsInfo()
          .equals(other.getGroupsInfo())) return false;
    }
    if (!getMembersList()
        .equals(other.getMembersList())) return false;
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
    if (hasGroupsInfo()) {
      hash = (37 * hash) + GROUPSINFO_FIELD_NUMBER;
      hash = (53 * hash) + getGroupsInfo().hashCode();
    }
    if (getMembersCount() > 0) {
      hash = (37 * hash) + MEMBERS_FIELD_NUMBER;
      hash = (53 * hash) + getMembersList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest prototype) {
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
   * Protobuf type {@code GroupsCreateRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:GroupsCreateRequest)
      org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsCreateRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsCreateRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.class, org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.newBuilder()
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
        getGroupsInfoFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (groupsInfoBuilder_ == null) {
        groupsInfo_ = null;
      } else {
        groupsInfoBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      members_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsCreateRequest_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest build() {
      org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest buildPartial() {
      org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest result = new org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        if (groupsInfoBuilder_ == null) {
          result.groupsInfo_ = groupsInfo_;
        } else {
          result.groupsInfo_ = groupsInfoBuilder_.build();
        }
        to_bitField0_ |= 0x00000001;
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        members_ = members_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000002);
      }
      result.members_ = members_;
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest other) {
      if (other == org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest.getDefaultInstance()) return this;
      if (other.hasGroupsInfo()) {
        mergeGroupsInfo(other.getGroupsInfo());
      }
      if (!other.members_.isEmpty()) {
        if (members_.isEmpty()) {
          members_ = other.members_;
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          ensureMembersIsMutable();
          members_.addAll(other.members_);
        }
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      if (!hasGroupsInfo()) {
        return false;
      }
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private org.whispersystems.textsecuregcm.internal.groups.GroupsInfo groupsInfo_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder> groupsInfoBuilder_;
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public boolean hasGroupsInfo() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupsInfo() {
      if (groupsInfoBuilder_ == null) {
        return groupsInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupsInfo_;
      } else {
        return groupsInfoBuilder_.getMessage();
      }
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public Builder setGroupsInfo(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo value) {
      if (groupsInfoBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        groupsInfo_ = value;
        onChanged();
      } else {
        groupsInfoBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public Builder setGroupsInfo(
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder builderForValue) {
      if (groupsInfoBuilder_ == null) {
        groupsInfo_ = builderForValue.build();
        onChanged();
      } else {
        groupsInfoBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public Builder mergeGroupsInfo(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo value) {
      if (groupsInfoBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
            groupsInfo_ != null &&
            groupsInfo_ != org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance()) {
          groupsInfo_ =
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.newBuilder(groupsInfo_).mergeFrom(value).buildPartial();
        } else {
          groupsInfo_ = value;
        }
        onChanged();
      } else {
        groupsInfoBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public Builder clearGroupsInfo() {
      if (groupsInfoBuilder_ == null) {
        groupsInfo_ = null;
        onChanged();
      } else {
        groupsInfoBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder getGroupsInfoBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getGroupsInfoFieldBuilder().getBuilder();
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupsInfoOrBuilder() {
      if (groupsInfoBuilder_ != null) {
        return groupsInfoBuilder_.getMessageOrBuilder();
      } else {
        return groupsInfo_ == null ?
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupsInfo_;
      }
    }
    /**
     * <code>required .GroupsInfo groupsInfo = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder> 
        getGroupsInfoFieldBuilder() {
      if (groupsInfoBuilder_ == null) {
        groupsInfoBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder>(
                getGroupsInfo(),
                getParentForChildren(),
                isClean());
        groupsInfo_ = null;
      }
      return groupsInfoBuilder_;
    }

    private com.google.protobuf.LazyStringList members_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureMembersIsMutable() {
      if (!((bitField0_ & 0x00000002) != 0)) {
        members_ = new com.google.protobuf.LazyStringArrayList(members_);
        bitField0_ |= 0x00000002;
       }
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getMembersList() {
      return members_.getUnmodifiableView();
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public int getMembersCount() {
      return members_.size();
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public java.lang.String getMembers(int index) {
      return members_.get(index);
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public com.google.protobuf.ByteString
        getMembersBytes(int index) {
      return members_.getByteString(index);
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public Builder setMembers(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureMembersIsMutable();
      members_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public Builder addMembers(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureMembersIsMutable();
      members_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public Builder addAllMembers(
        java.lang.Iterable<java.lang.String> values) {
      ensureMembersIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, members_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public Builder clearMembers() {
      members_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string members = 2;</code>
     */
    public Builder addMembersBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureMembersIsMutable();
      members_.add(value);
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


    // @@protoc_insertion_point(builder_scope:GroupsCreateRequest)
  }

  // @@protoc_insertion_point(class_scope:GroupsCreateRequest)
  private static final org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest();
  }

  public static org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<GroupsCreateRequest>
      PARSER = new com.google.protobuf.AbstractParser<GroupsCreateRequest>() {
    @java.lang.Override
    public GroupsCreateRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GroupsCreateRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GroupsCreateRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GroupsCreateRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.groups.GroupsCreateRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

