/**
 * Copyright (c) 2017 ZHONGHENG, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * ZHONGHENG, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with ZHONGHENG.
 */
package com.github.difftim.base.respone;


/**
 * Des: 返回值工厂类
 * ClassName: ResponseFactory
 */
public class ResponseStompFactory {


    public static ResponseStomp createOk(int ver,String service,String action,int status,Object data,String requestId){
        return new ResponseStomp(ver,HttpResponseStatus.OK.code(),service,action,status, null, data,requestId);
    }

    public static ResponseStomp createBad(int ver,int code,String service,String action,int status,String reason,String requestId){
        return new ResponseStomp(ver,code,service,action,status, reason, null,requestId);
    }

    public static ResponseStomp createOk(String service,String action,int status,Object data,String requestId){
        return new ResponseStomp(HttpResponseStatus.OK.code(),service,action,status, null, data,requestId);
    }

    public static ResponseStomp createOk(String service,String action,int status,Object data){
        return new ResponseStomp(HttpResponseStatus.OK.code(), service,action, status,data);
    }

    public static ResponseStomp createBad(String service,String action,int status,String reason,String requestId){
        return new ResponseStomp(HttpResponseStatus.OK.code(),service,action,status, reason,null,requestId);
    }

    public static ResponseStomp createBad(String service,String action,int status,String reason){
        return new ResponseStomp(HttpResponseStatus.OK.code(),service,action, status,reason,null);
    }

    public static ResponseStomp createBad(int code,String service,String action,int status,String reason,String requestId){
        return new ResponseStomp(code,service,action,status, reason,null,requestId);
    }

}
