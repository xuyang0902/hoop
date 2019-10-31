package com.tongbanjie.hoop.api.utils;

import java.util.UUID;

/**
 * uuID工具类
 *
 * @author xu.qiang
 * @date 18/8/13
 */
public class UUIDUtil {

    public static String getUUId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        String uuId = UUIDUtil.getUUId();
        System.out.println(uuId);
    }

}