package com.tongbanjie.hoop.core.spring.bootstrap;

import com.tongbanjie.hoop.storage.mysql.repository.JdbcTransactionReposity;
import com.tongbanjie.hoop.storage.mysql.repository.jdbc.dao.BranchTransactionDaoImpl;
import com.tongbanjie.hoop.storage.mysql.repository.jdbc.dao.GlobalTransactionDaoImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;


/**
 * hoop数据库存储事务单据需要配置的bean
 *
 * @author xu.qiang
 * @date 18/8/30
 */
public class HoopMysqlStorageBootStrap extends HoopBootStrap implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        // register java bean
        super.registerHoopBean(registry);
    }

    @Override
    protected void checkNecessary(BeanDefinitionRegistry registry) {

        super.checkNecessary(registry);

        BeanDefinition jdbcTemplate = registry.getBeanDefinition("jdbcTemplate");
        if (jdbcTemplate == null) {
            throw new RuntimeException("HoopMysqlStorageBootStrap必须配置>>jdbcTemplate");
        }

    }

    @Override
    protected GenericBeanDefinition registerHoopStorageEngine(BeanDefinitionRegistry registry) {

        //mysql存储db的bean配置  <!-- hoop事务记录引擎 -->
        GenericBeanDefinition globalTransactionDao = new GenericBeanDefinition();
        globalTransactionDao.setBeanClass(GlobalTransactionDaoImpl.class);
        globalTransactionDao.setLazyInit(false);
        globalTransactionDao.setAbstract(false);
        globalTransactionDao.setAutowireCandidate(true);
        globalTransactionDao.setScope("singleton");
        globalTransactionDao.setDependsOn(new String[]{"jdbcTemplate"});
        registry.registerBeanDefinition("globalTransactionDaoImpl", globalTransactionDao);

        GenericBeanDefinition branchTransactionDao = new GenericBeanDefinition();
        branchTransactionDao.setBeanClass(BranchTransactionDaoImpl.class);
        branchTransactionDao.setLazyInit(false);
        branchTransactionDao.setAbstract(false);
        branchTransactionDao.setAutowireCandidate(true);
        branchTransactionDao.setScope("singleton");
        branchTransactionDao.setDependsOn(new String[]{"jdbcTemplate"});
        registry.registerBeanDefinition("branchTransactionDaoImpl", branchTransactionDao);

        GenericBeanDefinition jdbcTransactionReposity = new GenericBeanDefinition();
        jdbcTransactionReposity.setBeanClass(JdbcTransactionReposity.class);
        jdbcTransactionReposity.setLazyInit(false);
        jdbcTransactionReposity.setAbstract(false);
        jdbcTransactionReposity.setAutowireCandidate(true);
        jdbcTransactionReposity.setScope("singleton");
        MutablePropertyValues propertyValues0 = new MutablePropertyValues();
        propertyValues0.addPropertyValue("globalTransactionDao", globalTransactionDao);
        propertyValues0.addPropertyValue("branchTransactionDao", branchTransactionDao);
        propertyValues0.addPropertyValue("hoopClientConfig", registry.getBeanDefinition("hoopClientConfig"));
        jdbcTransactionReposity.setPropertyValues(propertyValues0);
        registry.registerBeanDefinition("jdbcTransactionReposity", jdbcTransactionReposity);

        return jdbcTransactionReposity;
    }

}