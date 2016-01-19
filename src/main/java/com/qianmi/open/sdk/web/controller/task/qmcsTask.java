package com.qianmi.open.sdk.web.controller.task;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.domain.common.QmcsMessage;
import com.qianmi.open.api.request.QmcsMessagesConfirmRequest;
import com.qianmi.open.api.request.QmcsMessagesConsumeRequest;
import com.qianmi.open.api.response.QmcsMessagesConfirmResponse;
import com.qianmi.open.api.response.QmcsMessagesConsumeResponse;
import com.qianmi.open.sdk.web.controller.BaseController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by qmopen on 16/1/19.
 * 1:全新接口消息通知机制，获取实时数据变更通知。
 * 2:消息服务使用流程:订阅消息主题>为授权用户开通消息>消费消息>确认消费。
 * 3:消息服务使用说明文档:消息服务使用说明和消息体字段说明。
 * 4:建议使用SDK开发，详见SDK使用说明。
 * 5:典型应用场景:
 * a:E充值订单充值状态变更通知;
 * b:E生活票务订单预定状态变更通知;
 * c:电商云商品、订单、物流、会员等变更类消息。
 */
@Component
public class qmcsTask extends BaseController {




    /**
     * 消费多条消息
     * 调用qianmi.qmcs.messages.consume
     * @return
     * @throws com.qianmi.open.api.ApiException
     */
    public QmcsMessagesConsumeResponse getMessagesConsumer() throws ApiException {
        QmcsMessagesConsumeRequest req = new QmcsMessagesConsumeRequest();
        req.setGroupName("group_name");
        req.setQuantity(10);
        QmcsMessagesConsumeResponse response = client.execute(req, accessToken);
        return  response;
    }


    /**
     * 确认消费消息的状态
     * 调用qianmi.qmcs.messages.confirm
     * @return
     * @throws com.qianmi.open.api.ApiException
     */
    public QmcsMessagesConfirmResponse qmcsMessagesConfir(String ids) throws ApiException {
        QmcsMessagesConfirmRequest req = new QmcsMessagesConfirmRequest();
        req.setsMessageIds(ids);
        QmcsMessagesConfirmResponse response = client.execute(req, accessToken);
        return response;
    }


        @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
        public void myTest(){
            System.out.println("进入测试");
        }


    /**
     * 通过定时任务获取消息demo
     *
     */
    @Scheduled(cron="0/30 * *  * * ? ")  //每30秒执行一次
    public void qmcsMessageTask(){
        QmcsMessagesConsumeResponse response =new QmcsMessagesConsumeResponse();
        StringBuffer ids =null;
        try {
            response= this.getMessagesConsumer();
            List<QmcsMessage> qmcsMessages =  response.getQmcsMessages();

            //处理消息内容
            System.out.println(qmcsMessages);

            //处理完消息之后需要修改消息确认状态.
            if (qmcsMessages != null && !qmcsMessages.isEmpty()) {
                for (QmcsMessage task : qmcsMessages) {
                    ids.append(task.getId()+",");
                }
                qmcsMessagesConfir(ids.substring(0, ids.length() - 1));
            }

        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
