package com.tongbanjie.hoop.api.exception;

/**
 * hoop 一阶段明确失败的异常，框架碰到该类异常直接全部回滚
 * @author xu.qiang
 * @date 18/10/30
 */
public class RollbackException extends RuntimeException {


    private static final long serialVersionUID = 7103202639509880672L;

    public RollbackException() {
    }

    public RollbackException(String message) {
        super(message);
    }


}
