package com.tongbanjie.hoop.api.exception;

/**
 * HoopException异常
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public class HoopException extends RuntimeException {


    private static final long serialVersionUID = -2945270565263386842L;


    public HoopException() {
    }

    public HoopException(String message) {
        super(message);
    }

}
