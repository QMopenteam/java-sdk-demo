package com.qianmi.open.sdk.web.controller.recharge;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.OrderDetailInfo;
import com.qianmi.open.api.request.RechargeMobileCreateBillRequest;
import com.qianmi.open.api.request.RechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.response.RechargeMobileCreateBillResponse;
import com.qianmi.open.api.response.RechargeMobileGetItemInfoResponse;
import com.qianmi.open.sdk.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 话费充值sdkdemo
 * PS:下单成功，请在30分钟内调用'支付接口{qianmi.elife.recharge.base.payBill}',完成支付，否则必须重新下单。
 *
 * Created by qmopen on 16/1/9.
 */
@Controller
@RequestMapping("/mobile")
public class MobileController extends BaseController {

    /**
     * 下单分为两步：
     * 1. 查询可用商品，调用qianmi.elife.recharge.mobile.getItemInfo接口
     * 2. 下单，调用qianmi.elife.recharge.mobile.createBill接口
     * @throws ApiException
     */
    @RequestMapping(value = "/createBill")
    public Object mobileCreateBill(String mobileNo, String rechargeAmount, Model model) throws ApiException {
        String itemId = "";
        // 获取商品编号
        RechargeMobileGetItemInfoRequest req = new RechargeMobileGetItemInfoRequest();
        req.setMobileNo(mobileNo);
        req.setRechargeAmount(rechargeAmount);
        RechargeMobileGetItemInfoResponse response = client.execute(req, accessToken);
        if (!response.isSuccess()) {
            handleError(response);
            return "create-bill-fail";
        }
        // 下单
        RechargeMobileCreateBillRequest createBillRequest = new RechargeMobileCreateBillRequest();
        createBillRequest.setItemId(response.getMobileItemInfo().getItemId());
        createBillRequest.setMobileNo(mobileNo);
        createBillRequest.setRechargeAmount(rechargeAmount);
        RechargeMobileCreateBillResponse createBillResponse = client.execute(createBillRequest, accessToken);
        if (!createBillResponse.isSuccess()) {
            // 处理错误信息
            handleError(response);
            return "create-bill-fail";
        }
        // 将订单信息保存到本地
        saveOrderToDb(createBillResponse.getOrderDetailInfo());

        model.addAttribute("data", response.getMobileItemInfo());
        model.addAttribute("billId", createBillResponse.getOrderDetailInfo().getBillId());
        return "order-confirm";
    }

    private void handleError(QianmiResponse response) {
        System.out.println(response.getSubCode() + ":" + response.getSubMsg());
        // handle the error
    }

    private void saveOrderToDb(OrderDetailInfo orderDetailInfo) {
        // save bill to database
    }

}
