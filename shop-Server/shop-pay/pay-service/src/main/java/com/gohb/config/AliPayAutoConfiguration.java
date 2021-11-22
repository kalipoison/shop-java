package com.gohb.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载配置文件
 */
@Configuration
@EnableConfigurationProperties(value = AliPayProperties.class)
public class AliPayAutoConfiguration {


    public AliPayAutoConfiguration() {
        // 在构造方法里面我们可以加载我们的配置文件
        Configs.init("zfbinfo.properties");
    }

    @Autowired
    private AliPayProperties aliPayProperties;


    @Bean
    public AlipayTradeService alipayTradeService() {
        return new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    /**
     * 需要在容器中存放一个aliPay的客户端 用于后面的支付
     *
     * @return
     */
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(aliPayProperties.getGatewayUrl(),
                aliPayProperties.getAppId(),
                aliPayProperties.getMerchantPrivateKey(),
                "json",
                "UTF-8",
                aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getSignType());
    }


}
