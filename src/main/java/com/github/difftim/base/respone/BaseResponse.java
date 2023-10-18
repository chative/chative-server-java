package com.github.difftim.base.respone;


import com.google.gson.Gson;
import org.slf4j.Logger;

public class BaseResponse {

    static final private Gson gson = new Gson();
    private int ver = 1;
    private int status;
    private String reason;
    private Object data;

    public BaseResponse(int ver, int status, String reason, Object data) {
        this.ver = ver;
        this.status = status;
        this.reason = reason;
        this.data = data;
    }

    public BaseResponse(int status, String reason, Object data) {
        this.status = status;
        this.reason = reason;
        this.data = data;
    }

    static public BaseResponse err(STATUS code, String description, Logger logger) {
        if (logger != null) {
            logger.error(description);
        }
        return new BaseResponse(1, code.getState(), description, null);
    }

    static public BaseResponse ok() {
        return ok(null);
    }

    static public BaseResponse ok(Object data) {
        return new BaseResponse(1, STATUS.OK.getState(), "OK", data);
    }

    static public BaseResponse ok(Object data, Logger logger) {
        if (logger != null) {
            logger.info(gson.toJson(data));
        }
        return ok(data);
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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
        OTHER_ERROR(99);
        private int mState = 0;

        STATUS(int value) {
            mState = value;
        }

        /**
         * @return 枚举变量实际返回值
         */
        public int getState() {
            return mState;
        }

    }
}
