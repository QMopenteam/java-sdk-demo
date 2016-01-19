package com.qianmi.open.sdk.web.common;

import java.util.Properties;

/**
 * Created by qmopen on 16/1/9.
 */
public final class Constants {

    private Constants() {}

    public static final String API_URL = "http://gw.api.qianmi.com/api";

    /**
     * open相关的配置信息
     */
    public static Properties props;

    public static void loadProps(Properties props) {
        Constants.props = props;
    }

    public static String getProperty(final String key) {
        return props.getProperty(key);
    }

    public static void setProperty(final String key, final String value) {
        props.put(key, value);
    }
}
