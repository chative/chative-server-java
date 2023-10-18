package org.difft.domain;

import java.util.HashMap;

public class BaseResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String VERSION_TAG = "ver";

    /**
     * 返回内容
     */
    public static final String STATUS_TAG = "status";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 失败原因
     */
    public static final String REASON_TAG = "reason";

    /**
     * 角色
     */
    public enum Role {
        CREATOR(1),
        ASSIGNEE(2),
        FOLLOW(3);
        private final int value;

        Role(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 状态类型
     */
    public enum Type {
        /**
         * 成功
         */
        OK(0),
        /**
         * 参数
         */
        INVALID_PARAMETER(1),
        /**
         * 没有权限
         */
        NO_PERMISSION(2),
        /**
         * 没有群
         */
        NO_SUCH_GROUP(3),
        /**
         * 没有群成员
         */
        NO_SUCH_GROUP_MEMBER(4),
        /**
         * 参数
         */
        INVALID_TOKEN(5),
        /**
         * 服务端异常
         */
        SERVER_INTERNAL_ERROR(6),
        /**
         * 无群公告
         */
        NO_SUCH_GROUP_ANNOUNCEMENT(7),
        /**
         * group
         */
        GROUP_EXISTS(8),
        /**
         * 没有文件
         */
        NO_SUCH_FILE(9),
        /**
         * 群成员达到上线
         */
        GROUP_IS_FULE_OR_EXCEEDS(10),
        /**
         * 没有群任务
         */
        NO_SUCH_GROUP_TASK(11),
        /**
         * 终态任务不允许操作
         */
        OPERATE_ERROR(12),

        /**
         * 投票不存在
         */
        VOTE_IS_CLOSED(2),
        /**
         * 其他原因
         */
        OTHER_ERROR(99);
        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 初始化一个新创建的 VoteResult 对象，使其表示一个空消息。
     */
    public BaseResult() {
    }

    /**
     * 初始化一个新创建的 VoteResult 对象
     *
     * @param type 状态类型
     * @param msg  返回内容
     */
    public BaseResult(Type type, String msg) {
        super.put(VERSION_TAG, 1);
        super.put(STATUS_TAG, type.value);
        super.put(REASON_TAG, msg);
    }

    /**
     * 初始化一个新创建的 VoteResult 对象
     *
     * @param type 状态类型
     * @param msg  返回内容
     * @param data 数据对象
     */
    public BaseResult(Type type, String msg, Object data) {
        super.put(VERSION_TAG, 1);
        super.put(STATUS_TAG, type.value);
        super.put(REASON_TAG, msg);
        super.put(DATA_TAG, data);
    }

    /**
     * 方便链式调用
     *
     * @param key   键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public BaseResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static BaseResult success() {
        return BaseResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static BaseResult OK(Object data) {
        return BaseResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static BaseResult success(String msg) {
        return BaseResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static BaseResult success(String msg, Object data) {
        return new BaseResult(Type.OK, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static BaseResult error() {
        return BaseResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static BaseResult error(String msg) {
        return BaseResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static BaseResult error(String msg, Object data) {
        return new BaseResult(Type.OTHER_ERROR, msg, data);
    }


}
