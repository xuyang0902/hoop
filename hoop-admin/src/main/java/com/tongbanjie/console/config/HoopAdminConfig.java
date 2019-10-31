package com.tongbanjie.console.config;

import com.tongbanjie.console.config.model.HoopProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 数据源配置
 *
 * @author xu.qiang
 * @date 17/11/21
 */
@Configuration
@PropertySource(value = "classpath:config/hoop-admin.properties")
public class HoopAdminConfig {


    @ConfigurationProperties(prefix = "hoop.admin")
    @Bean(name = "hoopProperties")
    public HoopProperties getHoopAdmin() {
        return new HoopProperties();
    }

}
