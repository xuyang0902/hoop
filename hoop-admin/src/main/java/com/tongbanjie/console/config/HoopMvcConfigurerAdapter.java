package com.tongbanjie.console.config;

import com.tongbanjie.console.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * mvc配置适配器
 *
 * @author xu.qiang
 * @date 18/9/6
 */
@Configuration
public class HoopMvcConfigurerAdapter extends WebMvcConfigurerAdapter {


    /**
     * 资源文件
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/login") //登录页
                .excludePathPatterns("/loginIn"); //登录页
    }
}
