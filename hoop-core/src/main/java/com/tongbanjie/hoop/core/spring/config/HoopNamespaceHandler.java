package com.tongbanjie.hoop.core.spring.config;


import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.core.spring.HoopBootStrapStrategy;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * 配置文件 namespace对应解析器
 *
 * @author xu.qiang
 * @date 18/10/17
 */
public class HoopNamespaceHandler extends NamespaceHandlerSupport {


    @Override
    public void init() {
        registerBeanDefinitionParser("bootstrap", new HoopBeanDefinitionParser(HoopBootStrapStrategy.class));
        registerBeanDefinitionParser("config", new HoopBeanDefinitionParser(HoopClientConfig.class));
    }

}