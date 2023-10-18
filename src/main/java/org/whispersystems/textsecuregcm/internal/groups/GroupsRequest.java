// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GroupService.proto

package org.whispersystems.textsecuregcm.internal.groups;

/**
 * Protobuf type {@code GroupsRequest}
 */
public  final class GroupsRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:GroupsRequest)
    GroupsRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GroupsRequest.newBuilder() to construct.
  private GroupsRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GroupsRequest() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GroupsRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GroupsRequest(
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
            org.whispersystems.textsecuregcm.internal.common.Step.Builder subBuilder = null;
            if (((bitField0_ & 0x00000001) != 0)) {
              subBuilder = step_.toBuilder();
            }
            step_ = input.readMessage(org.whispersystems.textsecuregcm.internal.common.Step.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(step_);
              step_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000001;
            break;
          }
          case 18: {
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder subBuilder = null;
            if (((bitField0_ & 0x00000002) != 0)) {
              subBuilder = groupInfo_.toBuilder();
            }
            groupInfo_ = input.readMessage(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(groupInfo_);
              groupInfo_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000002;
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
    return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class, org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.Builder.class);
  }

  private int bitField0_;
  public static final int STEP_FIELD_NUMBER = 1;
  private org.whispersystems.textsecuregcm.internal.common.Step step_;
  /**
   * <code>optional .Step step = 1;</code>
   */
  public boolean hasStep() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional .Step step = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.common.Step getStep() {
    return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
  }
  /**
   * <code>optional .Step step = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.common.StepOrBuilder getStepOrBuilder() {
    return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
  }

  public static final int GROUPINFO_FIELD_NUMBER = 2;
  private org.whispersystems.textsecuregcm.internal.groups.GroupsInfo groupInfo_;
  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  public boolean hasGroupInfo() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupInfo() {
    return groupInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupInfo_;
  }
  /**
   * <code>optional .GroupsInfo groupInfo = 2;</code>
   */
  public org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupInfoOrBuilder() {
    return groupInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupInfo_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (hasStep()) {
      if (!getStep().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
    }
    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeMessage(1, getStep());
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeMessage(2, getGroupInfo());
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
        .computeMessageSize(1, getStep());
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getGroupInfo());
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.groups.GroupsRequest)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.groups.GroupsRequest other = (org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) obj;

    if (hasStep() != other.hasStep()) return false;
    if (hasStep()) {
      if (!getStep()
          .equals(other.getStep())) return false;
    }
    if (hasGroupInfo() != other.hasGroupInfo()) return false;
    if (hasGroupInfo()) {
      if (!getGroupInfo()
          .equals(other.getGroupInfo())) return false;
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
    if (hasStep()) {
      hash = (37 * hash) + STEP_FIELD_NUMBER;
      hash = (53 * hash) + getStep().hashCode();
    }
    if (hasGroupInfo()) {
      hash = (37 * hash) + GROUPINFO_FIELD_NUMBER;
      hash = (53 * hash) + getGroupInfo().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest prototype) {
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
   * Protobuf type {@code GroupsRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:GroupsRequest)
      org.whispersystems.textsecuregcm.internal.groups.GroupsRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.class, org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.newBuilder()
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
        getStepFieldBuilder();
        getGroupInfoFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (stepBuilder_ == null) {
        step_ = null;
      } else {
        stepBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      if (groupInfoBuilder_ == null) {
        groupInfo_ = null;
      } else {
        groupInfoBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupServiceOuterClass.internal_static_GroupsRequest_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsRequest getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsRequest build() {
      org.whispersystems.textsecuregcm.internal.groups.GroupsRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.groups.GroupsRequest buildPartial() {
      org.whispersystems.textsecuregcm.internal.groups.GroupsRequest result = new org.whispersystems.textsecuregcm.internal.groups.GroupsRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        if (stepBuilder_ == null) {
          result.step_ = step_;
        } else {
          result.step_ = stepBuilder_.build();
        }
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        if (groupInfoBuilder_ == null) {
          result.groupInfo_ = groupInfo_;
        } else {
          result.groupInfo_ = groupInfoBuilder_.build();
        }
        to_bitField0_ |= 0x00000002;
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.groups.GroupsRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.groups.GroupsRequest other) {
      if (other == org.whispersystems.textsecuregcm.internal.groups.GroupsRequest.getDefaultInstance()) return this;
      if (other.hasStep()) {
        mergeStep(other.getStep());
      }
      if (other.hasGroupInfo()) {
        mergeGroupInfo(other.getGroupInfo());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      if (hasStep()) {
        if (!getStep().isInitialized()) {
          return false;
        }
      }
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.whispersystems.textsecuregcm.internal.groups.GroupsRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.groups.GroupsRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private org.whispersystems.textsecuregcm.internal.common.Step step_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.common.Step.Builder, org.whispersystems.textsecuregcm.internal.common.StepOrBuilder> stepBuilder_;
    /**
     * <code>optional .Step step = 1;</code>
     */
    public boolean hasStep() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.common.Step getStep() {
      if (stepBuilder_ == null) {
        return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
      } else {
        return stepBuilder_.getMessage();
      }
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public Builder setStep(org.whispersystems.textsecuregcm.internal.common.Step value) {
      if (stepBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        step_ = value;
        onChanged();
      } else {
        stepBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public Builder setStep(
        org.whispersystems.textsecuregcm.internal.common.Step.Builder builderForValue) {
      if (stepBuilder_ == null) {
        step_ = builderForValue.build();
        onChanged();
      } else {
        stepBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public Builder mergeStep(org.whispersystems.textsecuregcm.internal.common.Step value) {
      if (stepBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
            step_ != null &&
            step_ != org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance()) {
          step_ =
            org.whispersystems.textsecuregcm.internal.common.Step.newBuilder(step_).mergeFrom(value).buildPartial();
        } else {
          step_ = value;
        }
        onChanged();
      } else {
        stepBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public Builder clearStep() {
      if (stepBuilder_ == null) {
        step_ = null;
        onChanged();
      } else {
        stepBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.common.Step.Builder getStepBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getStepFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.common.StepOrBuilder getStepOrBuilder() {
      if (stepBuilder_ != null) {
        return stepBuilder_.getMessageOrBuilder();
      } else {
        return step_ == null ?
            org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
      }
    }
    /**
     * <code>optional .Step step = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.common.Step.Builder, org.whispersystems.textsecuregcm.internal.common.StepOrBuilder> 
        getStepFieldBuilder() {
      if (stepBuilder_ == null) {
        stepBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.whispersystems.textsecuregcm.internal.common.Step, org.whispersystems.textsecuregcm.internal.common.Step.Builder, org.whispersystems.textsecuregcm.internal.common.StepOrBuilder>(
                getStep(),
                getParentForChildren(),
                isClean());
        step_ = null;
      }
      return stepBuilder_;
    }

    private org.whispersystems.textsecuregcm.internal.groups.GroupsInfo groupInfo_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder> groupInfoBuilder_;
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public boolean hasGroupInfo() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo getGroupInfo() {
      if (groupInfoBuilder_ == null) {
        return groupInfo_ == null ? org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupInfo_;
      } else {
        return groupInfoBuilder_.getMessage();
      }
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public Builder setGroupInfo(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo value) {
      if (groupInfoBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        groupInfo_ = value;
        onChanged();
      } else {
        groupInfoBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public Builder setGroupInfo(
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder builderForValue) {
      if (groupInfoBuilder_ == null) {
        groupInfo_ = builderForValue.build();
        onChanged();
      } else {
        groupInfoBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public Builder mergeGroupInfo(org.whispersystems.textsecuregcm.internal.groups.GroupsInfo value) {
      if (groupInfoBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
            groupInfo_ != null &&
            groupInfo_ != org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance()) {
          groupInfo_ =
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.newBuilder(groupInfo_).mergeFrom(value).buildPartial();
        } else {
          groupInfo_ = value;
        }
        onChanged();
      } else {
        groupInfoBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public Builder clearGroupInfo() {
      if (groupInfoBuilder_ == null) {
        groupInfo_ = null;
        onChanged();
      } else {
        groupInfoBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder getGroupInfoBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getGroupInfoFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder getGroupInfoOrBuilder() {
      if (groupInfoBuilder_ != null) {
        return groupInfoBuilder_.getMessageOrBuilder();
      } else {
        return groupInfo_ == null ?
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.getDefaultInstance() : groupInfo_;
      }
    }
    /**
     * <code>optional .GroupsInfo groupInfo = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder> 
        getGroupInfoFieldBuilder() {
      if (groupInfoBuilder_ == null) {
        groupInfoBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.whispersystems.textsecuregcm.internal.groups.GroupsInfo, org.whispersystems.textsecuregcm.internal.groups.GroupsInfo.Builder, org.whispersystems.textsecuregcm.internal.groups.GroupsInfoOrBuilder>(
                getGroupInfo(),
                getParentForChildren(),
                isClean());
        groupInfo_ = null;
      }
      return groupInfoBuilder_;
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


    // @@protoc_insertion_point(builder_scope:GroupsRequest)
  }

  // @@protoc_insertion_point(class_scope:GroupsRequest)
  private static final org.whispersystems.textsecuregcm.internal.groups.GroupsRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.groups.GroupsRequest();
  }

  public static org.whispersystems.textsecuregcm.internal.groups.GroupsRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<GroupsRequest>
      PARSER = new com.google.protobuf.AbstractParser<GroupsRequest>() {
    @java.lang.Override
    public GroupsRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GroupsRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GroupsRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GroupsRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.groups.GroupsRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
