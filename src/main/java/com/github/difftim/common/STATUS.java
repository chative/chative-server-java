// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: commonV3.proto

package com.github.difftim.common;

/**
 * Protobuf enum {@code pb.STATUS}
 */
public enum STATUS
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>OK = 0;</code>
   */
  OK(0),
  /**
   * <code>INVALID_PARAMETER = 1;</code>
   */
  INVALID_PARAMETER(1),
  /**
   * <code>NO_PERMISSION = 2;</code>
   */
  NO_PERMISSION(2),
  /**
   * <code>NO_SUCH_GROUP = 3;</code>
   */
  NO_SUCH_GROUP(3),
  /**
   * <code>NO_SUCH_GROUP_MEMBER = 4;</code>
   */
  NO_SUCH_GROUP_MEMBER(4),
  /**
   * <code>INVALID_TOKEN = 5;</code>
   */
  INVALID_TOKEN(5),
  /**
   * <code>SERVER_INTERNAL_ERROR = 6;</code>
   */
  SERVER_INTERNAL_ERROR(6),
  /**
   * <code>NO_SUCH_GROUP_ANNOUNCEMENT = 7;</code>
   */
  NO_SUCH_GROUP_ANNOUNCEMENT(7),
  /**
   * <code>GROUP_EXISTS = 8;</code>
   */
  GROUP_EXISTS(8),
  /**
   * <code>NO_SUCH_FILE = 9;</code>
   */
  NO_SUCH_FILE(9),
  /**
   * <code>GROUP_IS_FULL_OR_EXCEEDS = 10;</code>
   */
  GROUP_IS_FULL_OR_EXCEEDS(10),
  /**
   * <code>NO_SUCH_USER = 11;</code>
   */
  NO_SUCH_USER(11),
  /**
   * <code>RATE_LIMIT_EXCEEDED = 12;</code>
   */
  RATE_LIMIT_EXCEEDED(12),
  /**
   * <code>INVALID_INVITER = 13;</code>
   */
  INVALID_INVITER(13),
  /**
   * <code>USER_IS_DISABLED = 14;</code>
   */
  USER_IS_DISABLED(14),
  /**
   * <code>PUID_IS_REGISTERING = 15;</code>
   */
  PUID_IS_REGISTERING(15),
  /**
   * <code>NUMBER_IS_BINDING_OTHER_PUID = 16;</code>
   */
  NUMBER_IS_BINDING_OTHER_PUID(16),
  /**
   * <code>TEAM_HAS_MEMBERS = 17;</code>
   */
  TEAM_HAS_MEMBERS(17),
  /**
   * <code>VOTE_IS_CLOSED = 18;</code>
   */
  VOTE_IS_CLOSED(18),
  /**
   * <code>NO_SUCH_GROUP_PIN = 19;</code>
   */
  NO_SUCH_GROUP_PIN(19),
  /**
   * <code>USER_EMAIL_EXIST = 20;</code>
   */
  USER_EMAIL_EXIST(20),
  /**
   * <code>USER_OKTAID_EXIST = 21;</code>
   */
  USER_OKTAID_EXIST(21),
  /**
   * <code>GROUP_PIN_CONTENT_TOO_LONG = 22;</code>
   */
  GROUP_PIN_CONTENT_TOO_LONG(22),
  /**
   * <code>OTHER_ERROR = 99;</code>
   */
  OTHER_ERROR(99),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>OK = 0;</code>
   */
  public static final int OK_VALUE = 0;
  /**
   * <code>INVALID_PARAMETER = 1;</code>
   */
  public static final int INVALID_PARAMETER_VALUE = 1;
  /**
   * <code>NO_PERMISSION = 2;</code>
   */
  public static final int NO_PERMISSION_VALUE = 2;
  /**
   * <code>NO_SUCH_GROUP = 3;</code>
   */
  public static final int NO_SUCH_GROUP_VALUE = 3;
  /**
   * <code>NO_SUCH_GROUP_MEMBER = 4;</code>
   */
  public static final int NO_SUCH_GROUP_MEMBER_VALUE = 4;
  /**
   * <code>INVALID_TOKEN = 5;</code>
   */
  public static final int INVALID_TOKEN_VALUE = 5;
  /**
   * <code>SERVER_INTERNAL_ERROR = 6;</code>
   */
  public static final int SERVER_INTERNAL_ERROR_VALUE = 6;
  /**
   * <code>NO_SUCH_GROUP_ANNOUNCEMENT = 7;</code>
   */
  public static final int NO_SUCH_GROUP_ANNOUNCEMENT_VALUE = 7;
  /**
   * <code>GROUP_EXISTS = 8;</code>
   */
  public static final int GROUP_EXISTS_VALUE = 8;
  /**
   * <code>NO_SUCH_FILE = 9;</code>
   */
  public static final int NO_SUCH_FILE_VALUE = 9;
  /**
   * <code>GROUP_IS_FULL_OR_EXCEEDS = 10;</code>
   */
  public static final int GROUP_IS_FULL_OR_EXCEEDS_VALUE = 10;
  /**
   * <code>NO_SUCH_USER = 11;</code>
   */
  public static final int NO_SUCH_USER_VALUE = 11;
  /**
   * <code>RATE_LIMIT_EXCEEDED = 12;</code>
   */
  public static final int RATE_LIMIT_EXCEEDED_VALUE = 12;
  /**
   * <code>INVALID_INVITER = 13;</code>
   */
  public static final int INVALID_INVITER_VALUE = 13;
  /**
   * <code>USER_IS_DISABLED = 14;</code>
   */
  public static final int USER_IS_DISABLED_VALUE = 14;
  /**
   * <code>PUID_IS_REGISTERING = 15;</code>
   */
  public static final int PUID_IS_REGISTERING_VALUE = 15;
  /**
   * <code>NUMBER_IS_BINDING_OTHER_PUID = 16;</code>
   */
  public static final int NUMBER_IS_BINDING_OTHER_PUID_VALUE = 16;
  /**
   * <code>TEAM_HAS_MEMBERS = 17;</code>
   */
  public static final int TEAM_HAS_MEMBERS_VALUE = 17;
  /**
   * <code>VOTE_IS_CLOSED = 18;</code>
   */
  public static final int VOTE_IS_CLOSED_VALUE = 18;
  /**
   * <code>NO_SUCH_GROUP_PIN = 19;</code>
   */
  public static final int NO_SUCH_GROUP_PIN_VALUE = 19;
  /**
   * <code>USER_EMAIL_EXIST = 20;</code>
   */
  public static final int USER_EMAIL_EXIST_VALUE = 20;
  /**
   * <code>USER_OKTAID_EXIST = 21;</code>
   */
  public static final int USER_OKTAID_EXIST_VALUE = 21;
  /**
   * <code>GROUP_PIN_CONTENT_TOO_LONG = 22;</code>
   */
  public static final int GROUP_PIN_CONTENT_TOO_LONG_VALUE = 22;
  /**
   * <code>OTHER_ERROR = 99;</code>
   */
  public static final int OTHER_ERROR_VALUE = 99;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static STATUS valueOf(int value) {
    return forNumber(value);
  }

  public static STATUS forNumber(int value) {
    switch (value) {
      case 0: return OK;
      case 1: return INVALID_PARAMETER;
      case 2: return NO_PERMISSION;
      case 3: return NO_SUCH_GROUP;
      case 4: return NO_SUCH_GROUP_MEMBER;
      case 5: return INVALID_TOKEN;
      case 6: return SERVER_INTERNAL_ERROR;
      case 7: return NO_SUCH_GROUP_ANNOUNCEMENT;
      case 8: return GROUP_EXISTS;
      case 9: return NO_SUCH_FILE;
      case 10: return GROUP_IS_FULL_OR_EXCEEDS;
      case 11: return NO_SUCH_USER;
      case 12: return RATE_LIMIT_EXCEEDED;
      case 13: return INVALID_INVITER;
      case 14: return USER_IS_DISABLED;
      case 15: return PUID_IS_REGISTERING;
      case 16: return NUMBER_IS_BINDING_OTHER_PUID;
      case 17: return TEAM_HAS_MEMBERS;
      case 18: return VOTE_IS_CLOSED;
      case 19: return NO_SUCH_GROUP_PIN;
      case 20: return USER_EMAIL_EXIST;
      case 21: return USER_OKTAID_EXIST;
      case 22: return GROUP_PIN_CONTENT_TOO_LONG;
      case 99: return OTHER_ERROR;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<STATUS>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      STATUS> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<STATUS>() {
          public STATUS findValueByNumber(int number) {
            return STATUS.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.github.difftim.common.CommonProto.getDescriptor().getEnumTypes().get(0);
  }

  private static final STATUS[] VALUES = values();

  public static STATUS valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private STATUS(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:pb.STATUS)
}

