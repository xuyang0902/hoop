package com.tongbanjie.hoop.core.spring.config;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.core.spring.HoopBootStrapStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * hoop自定义 xml解析器
 *
 * @author xu.qiang
 * @date 18/10/17
 */
public class HoopBeanDefinitionParser implements org.springframework.beans.factory.xml.BeanDefinitionParser {

    private static final Logger logger = LoggerFactory.getLogger(HoopBeanDefinitionParser.class);

    private final Class<?> beanClass;

    public HoopBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        try {
            return parse(element, parserContext, beanClass);
        } catch (ClassNotFoundException e) {
            logger.error("", e);
            return null;
        }
    }

    private BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass) throws ClassNotFoundException {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setLazyInit(false);
        beanDefinition.setAbstract(false);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope("singleton");

        BeanDefinitionRegistry registry = parserContext.getRegistry();
        if (HoopBootStrapStrategy.class.equals(beanClass)) {

            String storeModel = element.getAttribute("storeModel");
            Class clazz = HoopBootStrapStrategy.get(storeModel);
            beanDefinition.setBeanClass(clazz);

            String beanName = org.apache.commons.lang.StringUtils.uncapitalize(clazz.getSimpleName());
            if (!registry.containsBeanDefinition(beanName)) {
                registry.registerBeanDefinition(beanName, beanDefinition);
            }

        } else if (HoopClientConfig.class.equals(beanClass)) {

            String beanName = org.apache.commons.lang.StringUtils.uncapitalize(HoopClientConfig.class.getSimpleName());
            beanDefinition.setBeanClass(HoopClientConfig.class);
            if (!registry.containsBeanDefinition(beanName)) {
                registry.registerBeanDefinition(beanName, beanDefinition);
            }

            MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
            propertyValues.addPropertyValue("appName", element.getAttribute("appName"));
            propertyValues.addPropertyValue("beforTime", Integer.valueOf(element.getAttribute("beforTime")));
            propertyValues.addPropertyValue("recoverTimeInterval", Integer.valueOf(element.getAttribute("recoverTimeInterval")));
            propertyValues.addPropertyValue("maxRecoverCount", Integer.valueOf(element.getAttribute("maxRecoverCount")));
            propertyValues.addPropertyValue("removeLog", Boolean.valueOf(element.getAttribute("removeLog")));

        }

        return beanDefinition;
    }


}