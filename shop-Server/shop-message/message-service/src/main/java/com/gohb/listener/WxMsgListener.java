package com.gohb.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.gohb.config.WxAutoConfiguration;
import com.gohb.config.WxProperties;
import com.gohb.constant.QueueConstant;
import com.gohb.model.WxMsgModel;
import com.gohb.service.SmsLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
public class WxMsgListener {


    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private WxAutoConfiguration wxAutoConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsLogService smsLogService;


    /**
     * 处理发微信公众号的消息
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = QueueConstant.WECHAT_SEND_QUEUE, concurrency = "3-5")
    public void wxMsgHandler(Message message, Channel channel) {
        // 拿到消息
        String msgStr = new String(message.getBody());
        WxMsgModel wxMsgModel = JSON.parseObject(msgStr, WxMsgModel.class);
        try {
            realSendWxMsg(wxMsgModel);
            // 成功了 记录数据库
            smsLogService.saveWxMsg(wxMsgModel);
            // 签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("发送微信公众号消息失败");
        }

    }

    /**
     * 真正发微信消息的方法
     *
     * @param wxMsgModel
     */
    private void realSendWxMsg(WxMsgModel wxMsgModel) {
        // 往一个地址发一个请求
        String sendMsgUrl = wxProperties.getSendMsgUrl();
        String wxAccessToken = wxAutoConfiguration.getWxAccessToken();
        String realSendUrl = String.format(sendMsgUrl, wxAccessToken);
        // 发一个post请求
        String result = restTemplate.postForObject(realSendUrl, wxMsgModel, String.class);
        log.error("发送微信公众号消息" + result);

    }


}
