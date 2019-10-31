package com.tongbanjie.hoop.api.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式分支事务注解
 *
 * @author xu.qiang
 * @date 18/4/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BranchT {

    /**
     * 分支事务服务的应用服务名称
     *
     * @return
     */
    String app() default "defualt_app";

    /**
     * 二阶段执行的beanId  默认取branch的beanId，指定的话 取指定的beanId
     */
    String beanId() default "";

    /**
     * confirm方法
     */
    String confirm() default "doConfirm";

    /**
     * cancel方法
     */
    String cancel() default "doCancel";

    /**
     * 二阶段 执行顺序，越小越先执行
     */
    int order() default -1;

    /**
     * 版本号  默认1.0版本
     */
    String version() default "1.0";
}
