package com.gohb.listener;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.rabbitmq.client.Channel;
import com.gohb.config.SmsProperties;
import com.gohb.constant.QueueConstant;
import com.gohb.model.AliSmsModel;
import com.gohb.service.SmsLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SmsListener {


    @Autowired
    private IAcsClient iAcsClient;

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private SmsLogService smsLogService;


    /**
     * 处理发消息的监听
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = QueueConstant.PHONE_SEND_QUEUE, concurrency = "3-5")
    public void smsHandler(Message message, Channel channel) {
        // 拿到消息
        String msgStr = new String(message.getBody());
        AliSmsModel aliSmsModel = JSON.parseObject(msgStr, AliSmsModel.class);
        // 发短信了
        try {
            CommonResponse commonResponse = realSendMsg(aliSmsModel);
            // 成功了
            log.info("短信发送成功");
            // 记录数据库
            smsLogService.saveMsg(aliSmsModel, commonResponse);
            // 签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("发送短信失败");
        }
    }

    /**
     * 真正发短信的方法
     *
     * @param aliSmsModel
     */
    private CommonResponse realSendMsg(AliSmsModel aliSmsModel) throws ClientException {
        CommonRequest commonRequest = new CommonRequest();
        // 基本参数
        commonRequest.setVersion(smsProperties.getVersion());
        commonRequest.setMethod(MethodType.POST);
        commonRequest.setRegionId("cn-hangzhou");
        commonRequest.setAction("SendSms");
        commonRequest.setDomain("dysmsapi.aliyuncs.com");
        // 给哪个手机号发
        commonRequest.putQueryParameter("PhoneNumbers", aliSmsModel.getPhoneNumbers());
        commonRequest.putQueryParameter("SignName", aliSmsModel.getSignName());
        commonRequest.putQueryParameter("TemplateCode", aliSmsModel.getTemplateCode());
        commonRequest.putQueryParameter("TemplateParam", aliSmsModel.getTemplateParam());
        // 发请求
        CommonResponse commonResponse = iAcsClient.getCommonResponse(commonRequest);
        System.out.println(commonResponse.getData());
        System.out.println(commonResponse.getHttpStatus());
        return commonResponse;
    }


}
