package com.gohb.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableConfigurationProperties(value = SmsProperties.class)
@Configuration
public class SmsAutoConfiguration {


    @Autowired
    private SmsProperties smsProperties;

    /**
     * 初始化阿里的客户端
     * 阿里的不是直接给一个url 你直接请求就可以
     * 他是需要你自己写一个客户端的配置
     * 你自己调用客户客户端的配置来发请求
     *
     * @return
     */
    @Bean
    public IAcsClient iAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                smsProperties.getAccessKeyId(),
                smsProperties.getAccessKeySecret()
        );
        IAcsClient acsClient = new DefaultAcsClient(profile);
        return acsClient;
    }

}
