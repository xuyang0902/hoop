package com.tongbanjie.hoop.api.exception;

/**
 * hoop一阶段超时异常
 * @author xu.qiang
 * @date 18/8/30
 */
public class HoopTimeOutException extends RuntimeException {


    private static final long serialVersionUID = -2945270565263386842L;

    public HoopTimeOutException() {
    }

    public HoopTimeOutException(String message) {
        super(message);
    }

}
