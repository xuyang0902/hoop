package com.tongbanjie.hoop.core.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author xu.qiang
 * @date 18/12/29
 */
public class HoopMethodUtil {


    /**
     * 使用了代理 代理对象找不到注解的时候 根据target对象找注解
     *
     * @param pjp
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Method getMethodByAnnotation(ProceedingJoinPoint pjp, Class<T> annotationClass) {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        if (method.getAnnotation(annotationClass) == null) {
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }


}
