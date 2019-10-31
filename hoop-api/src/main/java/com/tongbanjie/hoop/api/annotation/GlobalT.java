package com.tongbanjie.hoop.api.annotation;


import com.tongbanjie.hoop.api.constants.Env;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.exception.SuspendException;
import com.tongbanjie.hoop.api.recovery.TransactionHook;

import java.lang.annotation.*;

/**
 * 分布式全局事务注解
 *
 * @author xu.qiang
 * @date 18/4/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalT {

    /**
     * 事务类型 【默认tcc模式】
     */
    HoopType hoopType() default HoopType.TCC;

    /**
     * tcc模式
     * 1、tcc模式 默认try阶段 没有异常，那么直接执行所有参与者的commit
     * 2、tcc模式 如果try阶段 存在和银行交互|支付公司交互 那么必然存在对方返回 处理中的处理码，那么tcc框架如何实现呢？
     * 3、tcc模式 如果try阶段 存在rpc网络异常如何处理？
     *
     * @see SuspendException
     */
    Class<? extends TransactionHook> transactionHook();

    /**
     * 运行环境
     *
     * @return
     */
    String env() default Env.PROD;

    /**
     * 2阶段是否异步提交
     * 如果选择异步提交，需要注意：并发量特别大，Hoop只确保一阶段正常执行，二阶段的任务可能会丢失 最终会保证执行完（二阶段异步线程队列为1W）
     *
     * @return
     */
    boolean asynC() default false;

    /**
     * 分布式事务超时时间「相当于一阶段的执行时间超过6秒的话，二阶段不提交」
     * 单位：秒
     * 默认值：6秒
     */
    int timeout() default 6;


    /**
     * 需要回滚的异常  一阶段明确失败了，可以把这类异常扔出来
     *
     * @return
     */
    Class<? extends Throwable>[] rollbackFor() default {RollbackException.class};

}
