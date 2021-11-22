package com.gohb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "ailipay")
public class AliPayProperties {
    /**
     * 网关地址
     */
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    /**
     * 商户appId
     */
    private String appId;
    /**
     * 商户私钥
     */
    private String merchantPrivateKey;
    /**
     * 阿里支付的公钥
     */
    private String alipayPublicKey;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 返回地址
     */
    private String returnUrl;
    /**
     * 加密方式
     */
    private String signType = "RSA2";


}
