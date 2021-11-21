package com.gohb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx")
public class WxProperties {

    private String appId;

    private String appSecret;

    private String accessTokenUrl;

    private String sendMsgUrl;


}
