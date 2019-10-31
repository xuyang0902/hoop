package com.tongbanjie.hoop.core.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author xu.qiang
 * @date 18/4/23
 */
public class ReflectionUtil {


    /**
     * 手动更改注解的值
     * @param annotation
     * @param key
     * @param newValue
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object handler = Proxy.getInvocationHandler(annotation);

        Field f;

        f = handler.getClass().getDeclaredField("memberValues");

        f.setAccessible(true);

        Map<String, Object> memberValues;

        memberValues = (Map<String, Object>) f.get(handler);

        Object oldValue = memberValues.get(key);

        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {

            throw new IllegalArgumentException();
        }

        memberValues.put(key, newValue);

        return oldValue;
    }



    /**
     * 增强{@link AopUtils#getTargetClass(Object)}方法,实现cglib或者jdk多次代理获取目标方法
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Class<?> getTargetClass(Object proxy) throws Exception {
        while (AopUtils.isJdkDynamicProxy(proxy) || ClassUtils.isCglibProxyClass(proxy.getClass())) {
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                proxy = getJdkDynamicProxyTargetObject(proxy);
            } else {
                proxy = getCglibProxyTargetObject(proxy);
            }
        }
        return proxy.getClass();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource()
                .getTarget();
        return target;
    }
}