package com.springapp.mvc;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeMobileGetItemListRequest;
import com.qianmi.open.api.response.RechargeMobileGetItemListResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/index")
public class HelloController {

    private static final String url = "http://172.19.65.14:8080/api";
    private static final String appKey = "10000543";
    private static final String appSecret = "Ze0RMHXkCKtOCpNSQYbAiR7nBlT8ChdJ";
    private static final String FORMAT = "json";
    private static final String VERSION = "1.1";
    private static final String accessToken = "558bb67731c34db5b1892c7528b98021";


	@RequestMapping(method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Hello world!");
		return "mobile-recharge";
	}

    /**
     * 第一步,根据手机号码取得支持该号码的商品列表
     * @throws com.qianmi.open.api.ApiException
     */
    @RequestMapping(value = "account")
    public void mobileGetItemList() throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileGetItemListRequest req = new RechargeMobileGetItemListRequest();
        req.setMobileNo("15888888888");
        req.setRechargeAmount("50");
        RechargeMobileGetItemListResponse response = client.execute(req, accessToken);
    }
}