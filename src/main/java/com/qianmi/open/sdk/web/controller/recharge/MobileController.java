package com.qianmi.open.sdk.web.controller.recharge;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeMobileCreateBillRequest;
import com.qianmi.open.api.request.RechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.request.RechargeMobileGetItemListRequest;
import com.qianmi.open.api.response.RechargeMobileCreateBillResponse;
import com.qianmi.open.api.response.RechargeMobileGetItemInfoResponse;
import com.qianmi.open.api.response.RechargeMobileGetItemListResponse;

/**
 * 话费充值
 * Created by qmopen on 16/1/9.
 */

public class MobileController {

    private static final String url = "http://172.19.65.14:8080/api";
    private static final String appKey = "10000543";
    private static final String appSecret = "Ze0RMHXkCKtOCpNSQYbAiR7nBlT8ChdJ";
    private static final String FORMAT = "json";
    private static final String VERSION = "1.1";
    private static final String accessToken = "558bb67731c34db5b1892c7528b98021";

    private static final String SERVER_URL_PRODUCT = "http://172.17.9.98:8080/api";
    private static final String APP_KEY_PRODUCT = "sys10000001";
    private static final String SECRET_PRODUCT =  "sNRPWD7pbiDzQEQxL8CrawcffU6neoxk";
    private static final String ACCESS_TOKEN_PRODUCT = "2121349ff6654a7f9679ad87f4bfb73a";


    /**
     * 第一步,根据手机号码取得支持该号码的商品列表
     * @throws ApiException
     */
    public void mobileGetItemList() throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileGetItemListRequest req = new RechargeMobileGetItemListRequest();
        req.setMobileNo("15888888888");
        req.setRechargeAmount("50");
        RechargeMobileGetItemListResponse response = client.execute(req, accessToken);
    }

    /**
     * 第二步,根据选择的面值获取商品详情
     * @throws ApiException
     */
    public void mobileGetItemInfo() throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileGetItemInfoRequest req = new RechargeMobileGetItemInfoRequest();
        req.setMobileNo("13333333333");
        req.setRechargeAmount("100");
        RechargeMobileGetItemInfoResponse response = client.execute(req, accessToken);
    }

    /**
     * 第三步,下单
     * @throws ApiException
     */
    public void mobileCreateBill() throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileCreateBillRequest req = new RechargeMobileCreateBillRequest();
        req.setItemId("1400600");
        req.setMobileNo("13333333333");
        req.setRechargeAmount("50");
        RechargeMobileCreateBillResponse response = client.execute(req, accessToken);
    }

    public static void main(String[] args) {
        try {
            new MobileController().mobileGetItemList();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
