package com.tongbanjie.hoop.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SpringContextHolder
 *
 * @author xu.qiang
 * @date 18/4/12
 */
public class SpringContextHolder implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    private static ApplicationContext springContext;

    /**
     * 没有获得spring上下文时的延时时间
     */
    private final static int SLEEP = 120000;

    private final static ReentrantLock LOCK = new ReentrantLock();

    private final static Condition SETTER_NOTIFY = LOCK.newCondition();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        /**
         * spring启动时，设置spring上下文
         */
        if (applicationContext == null) {
            return;
        }

        try {
            LOCK.lock();

            if (SpringContextHolder.springContext == null) {
                SpringContextHolder.springContext = applicationContext;
                SETTER_NOTIFY.signalAll();
            }

        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 获取bean
     *
     * @param targetClass
     * @return
     */
    public static <T> T getBean(Class<T> targetClass) {
        return getSpringContext().getBean(targetClass);
    }


    /**
     * 获取bean
     *
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId) {
        if(StringUtils.isEmpty(beanId)){
            return null;
        }
        return getSpringContext().getBean(beanId);
    }

    /**
     * 获得spring上下文
     *
     * @return
     */
    public static ApplicationContext getSpringContext() {

        // null，说明spring没有初始化到，sleep一段时间
        if (springContext == null) {
            try {
                LOCK.lock();
                if (springContext == null) {

                    logger.warn("没有找到spring上下文，线程将sleep", SLEEP, "毫秒");

                    SETTER_NOTIFY.await(SLEEP, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                logger.error("获取springContext error:", e);
            } finally {
                LOCK.unlock();
            }
        }

        return springContext;
    }

}
