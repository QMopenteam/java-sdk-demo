package com.qianmi.open.sdk.web.common;

import java.util.Properties;

/**
 * Created by qmopen on 16/1/9.
 */
public class Constants {

    /**
     * open相关的配置信息
     */
    public static Properties openConfig;

    public static void loadUbgwConfig(Properties props) {
        openConfig = props;
    }

    public static String getProperty(final String key) {
        return openConfig.getProperty(key);
    }
}
