package com.example.module.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    private static final Map<Integer, String> statusMap = new HashMap<Integer, String>();

    static {
        statusMap.put(1001, "success");
        statusMap.put(1002, "没有登录");
        statusMap.put(1010, "账号密码不匹配或账号不存在");

        statusMap.put(2014,"账号尚未注册");
        statusMap.put(2015,"账号已存在");

        statusMap.put(4003,"没有权限");
        statusMap.put(4004,"链接超时");
    }

    public static String getMsg(Integer code) {
        return statusMap.get(code);

    }
}
