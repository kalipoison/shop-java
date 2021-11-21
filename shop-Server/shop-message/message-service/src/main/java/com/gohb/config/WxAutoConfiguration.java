package com.gohb.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@EnableConfigurationProperties(value = WxProperties.class)
@Configuration
@Slf4j
public class WxAutoConfiguration {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxProperties wxProperties;

    private String wxAccessToken;


    /**
     * 这个spring实现了这个注解的规范
     * 所有bean对象创建完以后执行，在项目启动完成之前执行
     * 限制 不能有返回值也不能有参数
     */
    @PostConstruct
    public void tokenInit() {
        getAccessToken();
    }


    /**
     * 获取微信的 access_token的方法
     */
    @Scheduled(initialDelay = 7100 * 1000, fixedRate = 7100 * 1000)
    public void getAccessToken() {
        String accessTokenUrl = wxProperties.getAccessTokenUrl();
        String realUrl = String.format(accessTokenUrl, wxProperties.getAppId(), wxProperties.getAppSecret());
        String accessTokenJsonStr = restTemplate.getForObject(realUrl, String.class);
        JSONObject jsonObject = JSON.parseObject(accessTokenJsonStr);
        String accessToken = jsonObject.getString("access_token");
        if (!StringUtils.isEmpty(accessToken)) {
            // 保存起来 可以放在redis里面
            wxAccessToken = accessToken;
        } else {
            log.error("获取微信的access_token失败了");
        }

    }

    public String getWxAccessToken() {
        return wxAccessToken;
    }

    public void setWxAccessToken(String wxAccessToken) {
        this.wxAccessToken = wxAccessToken;
    }
}
