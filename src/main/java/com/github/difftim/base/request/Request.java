/**
 * Copyright (c) 2017 ZHONGHENG, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * ZHONGHENG, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with ZHONGHENG.
 */
package com.github.difftim.base.request;

/**
 * Des: 请求参数的统一对象
 * ClassName: Request
 */
public class Request<T> extends BaseRequest{
    //请求参数
    private T params;
    //版本号
    private int ver;
    //请求方法
    private String action;
    //请求服务
    private String service;

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
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
}
