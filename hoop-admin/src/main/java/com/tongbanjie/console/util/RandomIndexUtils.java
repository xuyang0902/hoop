package com.tongbanjie.console.util;

/**
 * 随机数组下标获取utils
 *
 * @author xu.qiang
 * @date 2018/9/6
 */
public class RandomIndexUtils {


    public static int getRandomIndex(int totalSize) {
        return (int) (Math.random() * (totalSize - 1));
    }

    public static String getRandomUrl(String urls) {
        String[] ursList = urls.split(",");
        return ursList[RandomIndexUtils.getRandomIndex(ursList.length)];
    }

}
