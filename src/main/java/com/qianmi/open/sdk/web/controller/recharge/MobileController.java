package com.qianmi.open.sdk.web.controller.recharge;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeMobileCreateBillRequest;
import com.qianmi.open.api.request.RechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.response.RechargeMobileCreateBillResponse;
import com.qianmi.open.api.response.RechargeMobileGetItemInfoResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 话费充值sdkdemo
 *
 * 说明:话费充值根据面值选择商品有2种方式,1,自动选择最优商品(规则参阅下方),2获得所有支持商品的列表,
 * '本demo使用的是,方式1'
 *
 * PS:下单成功，请在30分钟内调用'支付接口{qianmi.elife.recharge.base.payBill}',完成支付，否则必须重新下单。
 * 本demo不含支付.
 .
 * Created by qmopen on 16/1/9.
 */
@Controller
@RequestMapping("/mobile")
public class MobileController {

    private static final String url = "gw.api.qianmi.com/api";
    private static final String appKey = "{你的APPKEY}";
    private static final String appSecret = "{你的appSecret}";
    private static final String accessToken = "{你的accessToken}";


    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        return "mobile-recharge";
    }


    /**
     * 方式1
     * 第一步,根据手机号码加上面值取得支持该号码的商品
     * 1.返回指定面值，手机号所在区域下优先级最高商品，优先级："市>省>全国，固定面值>任意充"
     * 2.在同样充值金额下，满足客户选择不同商品需求，可选择与 "查询话费直充商品列表"接口分场景使用
     * 3.可放弃此步骤，直接根据手机号码、充值金额直接生成订单(知道商品编号的前提下)。
     *
     * @throws ApiException
     */
    @RequestMapping(value = "/itemInfo")
    public Object mobileGetItemInfo(String mobileNo, String rechargeAmount,Model model) throws ApiException {

        if(mobileNo.isEmpty() || rechargeAmount.isEmpty()){
            return "mobile-recharge";
        }
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileGetItemInfoRequest req = new RechargeMobileGetItemInfoRequest();
        req.setMobileNo(mobileNo);
        req.setRechargeAmount(rechargeAmount);
        RechargeMobileGetItemInfoResponse response = client.execute(req, accessToken);
        //System.out.println(response);


        if(response.getErrorCode() != null){
            model.addAttribute("error",response.getSubMsg()+response.getSubCode());
            return "notFound";
        }else{
            model.addAttribute("data",response.getBody());
            return "order-confirm";
        }

    }

    /**
     * 方式2
     * 第一步,根据手机号码加上面值取得支持该号码的商品列表
     * 1.返回指定面值下手机号所在区域商品列表,包含任意充商品；
     * 2.在同样充值金额下，满足客户选择不同商品需求，可选择与 "查询单个话费充值商品"接口分场景使用。
     * 3.可放弃此步骤，直接根据手机号码、充值金额直接生成订单(知道商品编号的前提下)。
     *
     * @throws ApiException
     */
    @RequestMapping(value = "/itemList")
    public Object mobileGetItemInfo() throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileGetItemInfoRequest req = new RechargeMobileGetItemInfoRequest();
        req.setMobileNo("13333333333");
        req.setRechargeAmount("100");
        RechargeMobileGetItemInfoResponse response = client.execute(req, accessToken);
        return  response;
    }


    /**
     * 第二步,调用下单接口
     * 调用此接口，生成话费充值订单，并且返回订单详情
     *
     * @throws ApiException
     */
    @RequestMapping(value = "/createBill")
    public Object mobileCreateBill(String itemId , String mobileNo,String rechargeAmount) throws ApiException {
        OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
        RechargeMobileCreateBillRequest req = new RechargeMobileCreateBillRequest();
        req.setItemId(itemId);
        req.setMobileNo(mobileNo);
        req.setRechargeAmount(rechargeAmount);
        RechargeMobileCreateBillResponse response = client.execute(req, accessToken);
        return  response;
    }

}
