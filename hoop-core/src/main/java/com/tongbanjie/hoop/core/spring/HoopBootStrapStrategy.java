package com.tongbanjie.hoop.core.spring;


import com.tongbanjie.hoop.core.spring.bootstrap.HoopMysqlStorageBootStrap;

/**
 * hoop启动策略
 *
 * @author xu.qiang
 * @date 18/10/17
 */
public class HoopBootStrapStrategy {


    public static Class get(String storageModel) {

        if ("mysql".equals(storageModel)) {

            return HoopMysqlStorageBootStrap.class;
        }

        /*
         * 支持插件式编程，core中 在这里加入配置即可
         */

        throw new RuntimeException("not support except mysql yet");

    }


}
