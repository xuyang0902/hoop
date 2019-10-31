package com.tongbanjie.console.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author xu.qiang
 * @date 17/11/21
 */
@Configuration
@PropertySource(value = "classpath:config/jdbc.properties")
public class DruidConfig {


    @ConfigurationProperties(prefix = "datasource.druid")
    @Bean(name = "datasource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource getDataSource() {
        return new DruidDataSource();
    }

    /**
     * 注解事务
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    // 指定环境下prod 开启监控
    @Bean
    @Profile("prod")
    public FilterRegistrationBean druidFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WebStatFilter());
        Map<String, String> intParams = new HashMap<String, String>();
        intParams.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        registration.setName("DruidWebStatFilter");
        registration.setUrlPatterns(Collections.singleton("/*"));
        registration.setInitParameters(intParams);
        return registration;
    }

    //数据源监控
    @Bean
    @Profile("prod")
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(new StatViewServlet());
        registration.setName("druid");
        registration.setUrlMappings(Collections.singleton("/druid/*"));
        //自定义添加初始化参数
        Map<String, String> intParams = new HashMap<String, String>();
        intParams.put("loginUsername", "druid");
        intParams.put("loginPassword", "druid");
        registration.setName("DruidWebStatFilter");
        registration.setInitParameters(intParams);
        return registration;
    }

}
