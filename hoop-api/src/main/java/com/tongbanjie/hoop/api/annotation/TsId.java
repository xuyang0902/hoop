package com.tongbanjie.hoop.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式全局事务开启指定的唯一id
 *
 * 建议业务方一定需要传入这个参数。方便定义TransactionHook 在事务异常的时候，推动 commit|rollback
 * 如果业务方没有传入tsId，那么就没必要定义TransactionHook 在事务异常时（及时是网络的异常） hoop默认选择回滚
 *
 * @author xu.qiang
 * @date 18/8/20
 * @see com.tongbanjie.hoop.api.utils.TsIdBuilder#build(String, String)
 * @see com.tongbanjie.hoop.api.utils.TsIdBuilder#getTsIdPair(String)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface TsId {

}
