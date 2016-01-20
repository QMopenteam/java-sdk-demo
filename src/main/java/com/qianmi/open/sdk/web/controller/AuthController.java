package com.qianmi.open.sdk.web.controller;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.response.TokenResponse;
import com.qianmi.open.api.tool.util.OAuthUtils;
import com.qianmi.open.api.tool.util.QianmiContext;
import com.qianmi.open.sdk.web.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用授权
 */
@Controller
public class AuthController extends BaseController {

    /**
     * 显示授权页面
     * @param model
     * @return
     */
    @RequestMapping("/auth")
    public String showPage(Model model) {
        model.addAttribute("appKey", APP_KEY);
        return "auth";
    }

    /**
     * 接收回调地址中的code参数用来获取token
     * @param code
     * @return
     */
    @RequestMapping("/callback")
    public String getToken(String code, Model model) {
        try {
            QianmiContext context = OAuthUtils.getToken(APP_KEY, APP_SECRET, code);
            TokenResponse response = context.getTokenResponse();
            if (response.isSuccess()) {
                updateToken(response);
                startRefreshToken();
            } else {
                model.addAttribute("errMsg", response.getErrorCode() + ": " + response.getMsg());
                return "auth-fail";
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return "auth-success";
    }

    private void startRefreshToken() {
        // 刷新Token任务
        TimerTask refreshTokenTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    QianmiContext context = OAuthUtils.refreshToken(APP_KEY, APP_SECRET, Constants.getProperty("refreshToken"));
                    updateToken(context.getTokenResponse());
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        };
        // 每天7点刷新Token，每隔12个小时刷新一次
        new Timer("refresh-token-timer", true).schedule(refreshTokenTask, getFirstTime(), 12 * 60 * 60 * 1000);
    }

    /**
     * 获取第一次执行任务的时间
     * @return
     */
    private Date getFirstTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        if (date.after(new Date())) {
            return date;
        }
        calendar.add(Calendar.HOUR, 12);
        return calendar.getTime();
    }

    private void updateToken(TokenResponse response) {
        // 设置refreshToken
        Constants.setProperty("refreshToken", response.getRefreshToken());
        // 更新Token
        accessToken = response.getAccessToken();
    }

}
