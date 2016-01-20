package com.qianmi.open.sdk.web.controller;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.domain.common.QmcsMessage;
import com.qianmi.open.api.request.QmcsMessagesConfirmRequest;
import com.qianmi.open.api.request.QmcsMessagesConsumeRequest;
import com.qianmi.open.api.request.QmcsUserPermitRequest;
import com.qianmi.open.api.response.QmcsMessagesConsumeResponse;
import com.qianmi.open.api.response.QmcsUserPermitResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息服务
 */
@Controller
public class QmcsController extends BaseController {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    /**
     * 开通消息服务，调用qianmi.qmcs.user.permit接口
     * @param model
     * @return
     * @throws ApiException
     */
    @RequestMapping("/qmcs/start")
    public String startQmcs(Model model) throws ApiException {
        QmcsUserPermitRequest request = new QmcsUserPermitRequest();
        QmcsUserPermitResponse response = client.execute(request, accessToken);
        if (response.isSuccess()) {
            // 启动定时任务，定时消费消息
            startConsume();
            return "qmcs-success";
        } else {
            model.addAttribute("errMsg", response.getErrorCode() + ": " + response.getMsg());
            return "qmcs-fail";
        }
    }

    private void startConsume() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    // 调用消费消息接口，指定消费数量为100
                    QmcsMessagesConsumeRequest consumeRequest = new QmcsMessagesConsumeRequest();
                    consumeRequest.setQuantity(100);
                    QmcsMessagesConsumeResponse consumeResponse = client.execute(consumeRequest, accessToken);
                    if (CollectionUtils.isEmpty(consumeResponse.getQmcsMessages())) {
                        return;
                    }
                    final List<QmcsMessage> qmcsMessages = consumeResponse.getQmcsMessages();
                    // 启动线程处理消息
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            handleMessages(qmcsMessages);
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        };
        // 启动定时任务，每10秒钟消费一次
        new Timer("qmcs-timer", true).schedule(task, 10 * 1000, 10 * 1000);
    }

    /**
     * 处理消息
     * @param qmcsMessages
     */
    private void handleMessages(List<QmcsMessage> qmcsMessages) {
        // 处理消息，并将处理完成的消息id加入到handledMessageIds
        StringBuilder handledMessageIds = new StringBuilder();
        boolean handleComplete;
        for (QmcsMessage message : qmcsMessages) {
            handleComplete = false;
            // 处理订单充值消息
            if ("qianmi_elife_rechargeStateChange".equals(message.getTopic())) {
                // 处理消息内容
                if (handleOrder(message.getContent())) {
                    handleComplete = true;
                }
            }
            // 处理票务消息
            else if ("qianmi_elife_ticketStateChange".equals(message.getTopic())) {
                // TODO
            }
            // 只加入已处理完成的消息id
            if (handleComplete) {
                handledMessageIds.append(message.getId()).append(",");
            }
        }
        // 对已处理完的消息进行消费确认
        try {
            QmcsMessagesConfirmRequest confirmRequest = new QmcsMessagesConfirmRequest();
            confirmRequest.setsMessageIds(handledMessageIds.deleteCharAt(handledMessageIds.length() - 1).toString());
            client.execute(confirmRequest, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private boolean handleOrder(String content) {
        // TODO 处理消息内容，更新本地订单充值状态，根据充值状态做相应处理
        return true;
    }
}
