package com.tongbanjie.hoop.api.exception;

/**
 * hoop一阶段事务状态未知异常
 * <p>
 * hoop拿到这个异常后，不会直接执行二阶段的操作，会走补偿 根据事务最终状态来确定二阶段的走向
 *
 * @author xu.qiang
 * @date 18/9/17
 */
public class SuspendException extends RuntimeException {

    private static final long serialVersionUID = -4747326336093274886L;

    public SuspendException() {
    }

    public SuspendException(String message) {
        super(message);
    }

}

