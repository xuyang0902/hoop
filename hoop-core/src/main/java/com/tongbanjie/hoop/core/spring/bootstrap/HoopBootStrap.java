package com.tongbanjie.hoop.core.spring.bootstrap;

import com.tongbanjie.hoop.core.interceptor.CoordinatorAspect;
import com.tongbanjie.hoop.core.interceptor.HoopTransactionAspect;
import com.tongbanjie.hoop.core.recover.bootstrap.HoopRecoverBootStrap;
import com.tongbanjie.hoop.core.transaction.HoopCoreTransactionManager;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;


/**
 * hoop事务引擎存储 抽象启动类
 *
 * @author xu.qiang
 * @date 18/8/30
 */
public abstract class HoopBootStrap {

    /**
     * 注册hoop的bean
     *
     * @param registry
     */
    protected void registerHoopBean(BeanDefinitionRegistry registry) {

        //必要检查
        this.checkNecessary(registry);

        //注册存储引擎
        GenericBeanDefinition storageEngine = this.registerHoopStorageEngine(registry);

        //注册事务管理器
        GenericBeanDefinition transactionManager = this.registerHoopTransactionManager(registry, storageEngine);

        //注册hoop拦截器配置
        this.registerHoopInterceptor(registry, transactionManager);

        //注册hoop恢复启动器
        this.registerHoopRecoverBootStrap(registry, storageEngine, transactionManager);

        //注册其他的bean
        registerExt(registry);

    }

    /**
     * 检查必须项目
     *
     * @param registry
     */
    protected void checkNecessary(BeanDefinitionRegistry registry) {
        BeanDefinition HoopClientConfig = registry.getBeanDefinition("hoopClientConfig");
        if (HoopClientConfig == null) {
            throw new RuntimeException("HoopStorageBean必须配置>>HoopClientConfig");
        }
    }

    /**
     * 存储引擎
     *
     * @param registry
     */
    protected abstract GenericBeanDefinition registerHoopStorageEngine(BeanDefinitionRegistry registry);

    /**
     * 事务管理器
     *
     * @param registry
     * @param transactionReposity 事务存储引擎
     * @return
     */
    protected GenericBeanDefinition registerHoopTransactionManager(BeanDefinitionRegistry registry, GenericBeanDefinition transactionReposity) {
        //hoop事务管理器
        GenericBeanDefinition hoopCoreTransactionManager = new GenericBeanDefinition();
        hoopCoreTransactionManager.setBeanClass(HoopCoreTransactionManager.class);
        hoopCoreTransactionManager.setLazyInit(false);
        hoopCoreTransactionManager.setAbstract(false);
        hoopCoreTransactionManager.setAutowireCandidate(true);
        hoopCoreTransactionManager.setScope("singleton");
        MutablePropertyValues propertyValues1 = new MutablePropertyValues();
        propertyValues1.addPropertyValue("transactionRepositry", transactionReposity);
        hoopCoreTransactionManager.setPropertyValues(propertyValues1);
        registry.registerBeanDefinition("hoopCoreTransactionManager", hoopCoreTransactionManager);

        return hoopCoreTransactionManager;
    }

    /**
     * 注册hoop拦截器
     *
     * @param registry
     * @param transactionManager
     */
    protected void registerHoopInterceptor(BeanDefinitionRegistry registry, GenericBeanDefinition transactionManager) {
        BeanDefinition HoopClientConfig = registry.getBeanDefinition("hoopClientConfig");

        //hoop拦截器配置
        GenericBeanDefinition transactionAspect = new GenericBeanDefinition();
        transactionAspect.setBeanClass(HoopTransactionAspect.class);
        transactionAspect.setLazyInit(false);
        transactionAspect.setAbstract(false);
        transactionAspect.setAutowireCandidate(true);
        transactionAspect.setScope("singleton");
        MutablePropertyValues propertyValues2 = new MutablePropertyValues();
        propertyValues2.addPropertyValue("transactionManager", transactionManager);
        transactionAspect.setPropertyValues(propertyValues2);
        registry.registerBeanDefinition("hoopTransactionAspect", transactionAspect);

        GenericBeanDefinition coordinatorAspect = new GenericBeanDefinition();
        coordinatorAspect.setBeanClass(CoordinatorAspect.class);
        coordinatorAspect.setLazyInit(false);
        coordinatorAspect.setAbstract(false);
        coordinatorAspect.setAutowireCandidate(true);
        coordinatorAspect.setScope("singleton");
        MutablePropertyValues propertyValues3 = new MutablePropertyValues();
        propertyValues3.addPropertyValue("transactionManager", transactionManager);
        coordinatorAspect.setPropertyValues(propertyValues3);
        registry.registerBeanDefinition("coordinatorAspect", coordinatorAspect);
    }

    /**
     * 注册hoop事务恢复启动器
     *
     * @param registry
     * @param storageEngine
     */
    protected void registerHoopRecoverBootStrap(BeanDefinitionRegistry registry, GenericBeanDefinition storageEngine,
                                                GenericBeanDefinition transactionManager) {

        BeanDefinition HoopClientConfig = registry.getBeanDefinition("hoopClientConfig");

        //hoop启动器  依赖hoop的配置，事务存储器
        GenericBeanDefinition hoopBootStrap = new GenericBeanDefinition();
        hoopBootStrap.setBeanClass(HoopRecoverBootStrap.class);
        hoopBootStrap.setLazyInit(false);
        hoopBootStrap.setAbstract(false);
        hoopBootStrap.setAutowireCandidate(true);
        hoopBootStrap.setScope("singleton");
        hoopBootStrap.setInitMethodName("init");
        MutablePropertyValues propertyValues4 = new MutablePropertyValues();
        propertyValues4.addPropertyValue("hoopClientConfig", HoopClientConfig);
        propertyValues4.addPropertyValue("transactionRepositry", storageEngine);
        propertyValues4.addPropertyValue("transactionManager", transactionManager);
        hoopBootStrap.setPropertyValues(propertyValues4);
        registry.registerBeanDefinition("hoopRecoverBootStrap", hoopBootStrap);

    }


    /**
     * 注册其他信息
     *
     * @param registry
     */
    protected void registerExt(BeanDefinitionRegistry registry) {
        //
        GenericBeanDefinition springContextHolder = new GenericBeanDefinition();
        springContextHolder.setBeanClass(SpringContextHolder.class);
        springContextHolder.setLazyInit(false);
        springContextHolder.setAbstract(false);
        springContextHolder.setAutowireCandidate(true);
        springContextHolder.setScope("singleton");
        registry.registerBeanDefinition("springContextHolder", springContextHolder);
    }

}