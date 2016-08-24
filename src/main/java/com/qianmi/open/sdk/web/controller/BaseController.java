package com.qianmi.open.sdk.web.controller;

import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.sdk.web.common.Constants;

/**
 * Created by qmopen on 16/1/9.
 */
public class BaseController {

    private static final String API_URL = "http://gw.api.qianmi.com/api";

    protected static final String APP_KEY = Constants.getProperty("appKey").trim();
    protected static final String APP_SECRET = Constants.getProperty("appSecret").trim();

    protected static String accessToken = Constants.getProperty("accessToken").trim();
    // 创建一个客户端，默认连接超时时间为3秒，请求超时时间为15秒。
    protected static final OpenClient client = new DefaultOpenClient(API_URL, APP_KEY, APP_SECRET);

}
