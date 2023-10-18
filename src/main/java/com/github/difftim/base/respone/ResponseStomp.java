/**
 * Copyright (c) 2017 ZHONGHENG, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * ZHONGHENG, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with ZHONGHENG.
 */
package com.github.difftim.base.respone;

/**
 * Des:
 * ClassName: ResponseStomp
 */
public class ResponseStomp extends BaseResponse{

    //返回http代码
    private int code;
    //请求sevice
    private String service;
    //操作方法action
    private String action;
    //返回服务器时间
    private long time;
    //返回对应请求Id
    private String requestId;
    public ResponseStomp(int ver,int code,String service, String action,int status,String reason, Object data,String requestId) {
        super(ver,status,reason,data);
        this.service=service;
        this.requestId=requestId;
        this.code = code;
        this.action = action;
        this.time=System.currentTimeMillis();
    }

    public ResponseStomp(int code, String service,String action,int status,String reason, Object data,String requestId) {
        super(status,reason,data);
        this.service=service;
        this.requestId=requestId;
        this.code = code;
        this.action = action;
        this.time=System.currentTimeMillis();
    }

    public ResponseStomp(int code,String service,String action,int status, String reason, Object data) {
        super(status,reason,data);
        this.service=service;
        this.code = code;
        this.action = action;
        this.time=System.currentTimeMillis();
    }

    public ResponseStomp(int code,String service,String action,int status, Object data) {
        super(status,null,data);
        this.service=service;
        this.code = code;
        this.action = action;
        this.time=System.currentTimeMillis();
    }

    public ResponseStomp(int code,String service,String action,int status) {
        super(status,null,null);
        this.service=service;
        this.code = code;
        this.action = action;
        this.time=System.currentTimeMillis();
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
