package com.gohb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    private String accessKeyId;

    private String AccessKeySecret;

    private String version;


}
