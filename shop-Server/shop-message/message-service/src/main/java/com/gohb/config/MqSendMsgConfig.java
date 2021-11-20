package com.gohb.config;

import com.gohb.constant.QueueConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqSendMsgConfig {

    // 短信的配置
    @Bean
    public Queue sendSmsQueue() {
        return new Queue(QueueConstant.PHONE_SEND_QUEUE);
    }


    @Bean
    public DirectExchange sendSmsEx() {
        return new DirectExchange(QueueConstant.PHONE_SEND_EX);
    }

    @Bean
    public Binding sendSmsBind() {
        return BindingBuilder.bind(sendSmsQueue()).to(sendSmsEx()).with(QueueConstant.PHONE_SEND_KEY);
    }

    // 微信的配置

    @Bean
    public Queue WxMsgQueue() {
        return new Queue(QueueConstant.WECHAT_SEND_QUEUE);
    }


    @Bean
    public DirectExchange WxMsgEx() {
        return new DirectExchange(QueueConstant.WECHAT_SEND_EX);
    }

    @Bean
    public Binding WxMsgBind() {
        return BindingBuilder.bind(WxMsgQueue()).to(WxMsgEx()).with(QueueConstant.WECHAT_SEND_KEY);
    }


}

