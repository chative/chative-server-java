package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class BaseResponse {

  public enum STATUS {
    OK(0),
    INVALID_PARAMETER(1),
    NO_PERMISSION(2),
    NO_SUCH_GROUP(3),
    NO_SUCH_GROUP_MEMBER(4),
    INVALID_TOKEN(5),
    SERVER_INTERNAL_ERROR(6),
    NO_SUCH_GROUP_ANNOUNCEMENT(7),
    GROUP_EXISTS(8),
    NO_SUCH_FILE(9),
    GROUP_IS_FULL_OR_EXCEEDS(10),
    NO_SUCH_USER(11),
    INVALID_FILE(12),
    FILE_VERIFICATION_ERROR(13),
    USER_IS_DISABLED(14),
    RATE_LIMIT_EXCEEDED(15),
    INVALID_INVITER(16),
    NO_SUCH_GROUP_PIN(19),
    USER_EMAIL_EXIST(20),
    USER_OKTAID_EXIST(21),
    GROUP_PIN_CONTENT_TOO_LONG(22),
    ALREADY_BOUND(23),
    EMAIL_OCCUPIED(24),
    EMAIL_SEND_CODE_FAILED(25),
    EMAIL_VERIFICATION_CODE_ERROR(26),
    EMAIL_VERIFICATION_CODE_ERROR_MANY(27),
    INVITATION_CODE_ALREADY_USED(28),
    GroupLinkInvalidInviteCode(10120), // 群组邀请链接Code无效
    GroupLinkInviteDisabled(10121), // 群组邀请链接已关闭
    GroupOnlyAdminAddMem(10122), // Only moderators can add members
    GroupDisbanded(10123), // Failed to join the group. This group has already been disbanded.	入群失败，此群已经被解散
    GroupLinkInviteInvalid(10124), // Failed to join the group. This group is invalid.	入群失败，此群无效
    OTHER_ERROR(99);



    private int  mState=0;
    STATUS(int value)
    {
      mState=value;
    }
    /**
     * @return 枚举变量实际返回值
     */
    public int getState()
    {
      return mState;
    }

  }

  static final private ObjectMapper objectMapper = new ObjectMapper();

  @JsonProperty
  private int ver;

  @JsonProperty
  private int status;

  @JsonProperty
  private String reason;

  @JsonProperty
  private Object data;

  public BaseResponse(int ver, int status, String reason, Object data) {
    this.ver = ver;
    this.status = status;
    this.reason = reason;
    this.data = data;
  }

  @Deprecated
  static public void err(STATUS code, String description, Logger logger) {
    logger.error(description);
    throw new WebApplicationException(Response.status(200).entity(new BaseResponse(1, code.getState(), description, null)).build());
  }

  static public void err(int httpCode, STATUS code, String description, Logger logger, Object data) {
    logger.error(description);
    throw new WebApplicationException(Response.status(httpCode).entity(new BaseResponse(1, code.getState(), description, data)).build());
  }

  static public BaseResponse ok() {
    return ok(null);
  }

  static public BaseResponse ok(Object data) {
    return new BaseResponse(1, STATUS.OK.getState(), "OK", data);
  }

  static public BaseResponse ok(Object data, Logger logger) {
    try {
      logger.info(objectMapper.writeValueAsString(data));
    } catch (IOException e) {
      logger.error("Failed to parse object", e);
    }
    return ok(data);
  }

  public String getReason() {
    return reason;
  }

  public int getStatus() {
    return status;
  }
}
