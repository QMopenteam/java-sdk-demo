package com.qianmi.open.sdk.web.common;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * @description:
 * @author: qmopen
 * @date: 14-3-12 下午2:07
 */
public class PropertyConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        logger.warn("========================加载配置文件开始==================");
        super.processProperties(beanFactory, props);
        Constants.loadProps(props);
        logger.warn("========================加载配置文件结束===================");
    }
}
