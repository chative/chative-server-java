// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InternalAccountsInvitationService.proto

package org.whispersystems.textsecuregcm.internal.invitation;

/**
 * Protobuf type {@code InvitationRequest}
 */
public  final class InvitationRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:InvitationRequest)
    InvitationRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use InvitationRequest.newBuilder() to construct.
  private InvitationRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private InvitationRequest() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new InvitationRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private InvitationRequest(
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
            org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder subBuilder = null;
            if (((bitField0_ & 0x00000002) != 0)) {
              subBuilder = invitation_.toBuilder();
            }
            invitation_ = input.readMessage(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(invitation_);
              invitation_ = subBuilder.buildPartial();
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
    return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.internal_static_InvitationRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.internal_static_InvitationRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.class, org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.Builder.class);
  }

  private int bitField0_;
  public static final int STEP_FIELD_NUMBER = 1;
  private org.whispersystems.textsecuregcm.internal.common.Step step_;
  /**
   * <code>required .Step step = 1;</code>
   */
  public boolean hasStep() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>required .Step step = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.common.Step getStep() {
    return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
  }
  /**
   * <code>required .Step step = 1;</code>
   */
  public org.whispersystems.textsecuregcm.internal.common.StepOrBuilder getStepOrBuilder() {
    return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
  }

  public static final int INVITATION_FIELD_NUMBER = 2;
  private org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest invitation_;
  /**
   * <code>optional .InvitationUpdateRequest invitation = 2;</code>
   */
  public boolean hasInvitation() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional .InvitationUpdateRequest invitation = 2;</code>
   */
  public org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest getInvitation() {
    return invitation_ == null ? org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance() : invitation_;
  }
  /**
   * <code>optional .InvitationUpdateRequest invitation = 2;</code>
   */
  public org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequestOrBuilder getInvitationOrBuilder() {
    return invitation_ == null ? org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance() : invitation_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasStep()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!getStep().isInitialized()) {
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
      output.writeMessage(1, getStep());
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeMessage(2, getInvitation());
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
        .computeMessageSize(2, getInvitation());
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
    if (!(obj instanceof org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest)) {
      return super.equals(obj);
    }
    org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest other = (org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest) obj;

    if (hasStep() != other.hasStep()) return false;
    if (hasStep()) {
      if (!getStep()
          .equals(other.getStep())) return false;
    }
    if (hasInvitation() != other.hasInvitation()) return false;
    if (hasInvitation()) {
      if (!getInvitation()
          .equals(other.getInvitation())) return false;
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
    if (hasInvitation()) {
      hash = (37 * hash) + INVITATION_FIELD_NUMBER;
      hash = (53 * hash) + getInvitation().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parseFrom(
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
  public static Builder newBuilder(org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest prototype) {
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
   * Protobuf type {@code InvitationRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:InvitationRequest)
      org.whispersystems.textsecuregcm.internal.invitation.InvitationRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.internal_static_InvitationRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.internal_static_InvitationRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.class, org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.Builder.class);
    }

    // Construct using org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.newBuilder()
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
        getInvitationFieldBuilder();
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
      if (invitationBuilder_ == null) {
        invitation_ = null;
      } else {
        invitationBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.whispersystems.textsecuregcm.internal.invitation.InternalAccountsInvitationServiceOuterClass.internal_static_InvitationRequest_descriptor;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest getDefaultInstanceForType() {
      return org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest build() {
      org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest buildPartial() {
      org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest result = new org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest(this);
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
        if (invitationBuilder_ == null) {
          result.invitation_ = invitation_;
        } else {
          result.invitation_ = invitationBuilder_.build();
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
      if (other instanceof org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest) {
        return mergeFrom((org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest other) {
      if (other == org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest.getDefaultInstance()) return this;
      if (other.hasStep()) {
        mergeStep(other.getStep());
      }
      if (other.hasInvitation()) {
        mergeInvitation(other.getInvitation());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      if (!hasStep()) {
        return false;
      }
      if (!getStep().isInitialized()) {
        return false;
      }
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest) e.getUnfinishedMessage();
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
     * <code>required .Step step = 1;</code>
     */
    public boolean hasStep() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required .Step step = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.common.Step getStep() {
      if (stepBuilder_ == null) {
        return step_ == null ? org.whispersystems.textsecuregcm.internal.common.Step.getDefaultInstance() : step_;
      } else {
        return stepBuilder_.getMessage();
      }
    }
    /**
     * <code>required .Step step = 1;</code>
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
     * <code>required .Step step = 1;</code>
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
     * <code>required .Step step = 1;</code>
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
     * <code>required .Step step = 1;</code>
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
     * <code>required .Step step = 1;</code>
     */
    public org.whispersystems.textsecuregcm.internal.common.Step.Builder getStepBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getStepFieldBuilder().getBuilder();
    }
    /**
     * <code>required .Step step = 1;</code>
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
     * <code>required .Step step = 1;</code>
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

    private org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest invitation_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequestOrBuilder> invitationBuilder_;
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public boolean hasInvitation() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest getInvitation() {
      if (invitationBuilder_ == null) {
        return invitation_ == null ? org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance() : invitation_;
      } else {
        return invitationBuilder_.getMessage();
      }
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public Builder setInvitation(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest value) {
      if (invitationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        invitation_ = value;
        onChanged();
      } else {
        invitationBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public Builder setInvitation(
        org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder builderForValue) {
      if (invitationBuilder_ == null) {
        invitation_ = builderForValue.build();
        onChanged();
      } else {
        invitationBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public Builder mergeInvitation(org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest value) {
      if (invitationBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
            invitation_ != null &&
            invitation_ != org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance()) {
          invitation_ =
            org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.newBuilder(invitation_).mergeFrom(value).buildPartial();
        } else {
          invitation_ = value;
        }
        onChanged();
      } else {
        invitationBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public Builder clearInvitation() {
      if (invitationBuilder_ == null) {
        invitation_ = null;
        onChanged();
      } else {
        invitationBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder getInvitationBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getInvitationFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    public org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequestOrBuilder getInvitationOrBuilder() {
      if (invitationBuilder_ != null) {
        return invitationBuilder_.getMessageOrBuilder();
      } else {
        return invitation_ == null ?
            org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.getDefaultInstance() : invitation_;
      }
    }
    /**
     * <code>optional .InvitationUpdateRequest invitation = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequestOrBuilder> 
        getInvitationFieldBuilder() {
      if (invitationBuilder_ == null) {
        invitationBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequest.Builder, org.whispersystems.textsecuregcm.internal.invitation.InvitationUpdateRequestOrBuilder>(
                getInvitation(),
                getParentForChildren(),
                isClean());
        invitation_ = null;
      }
      return invitationBuilder_;
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


    // @@protoc_insertion_point(builder_scope:InvitationRequest)
  }

  // @@protoc_insertion_point(class_scope:InvitationRequest)
  private static final org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest();
  }

  public static org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<InvitationRequest>
      PARSER = new com.google.protobuf.AbstractParser<InvitationRequest>() {
    @java.lang.Override
    public InvitationRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new InvitationRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<InvitationRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<InvitationRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.whispersystems.textsecuregcm.internal.invitation.InvitationRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

