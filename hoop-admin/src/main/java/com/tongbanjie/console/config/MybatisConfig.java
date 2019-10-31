package com.tongbanjie.console.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * mybatis配置
 *
 * @author xu.qiang
 * @date 17/11/21
 */
@Configuration
@AutoConfigureAfter(DataSource.class)
public class MybatisConfig {


    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/*.xml"));
        sessionFactory.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-configuration.xml"));
        return sessionFactory;
    }


}
