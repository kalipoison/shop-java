package com.gohb.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gohb.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 存储jwt到redis的路由
 */
@Configuration
public class RouterConfig {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 我们使用代码方式的路由 存储jwt到redis
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("auth-server-router", r -> r.path("/oauth/**")
                        .filters(f -> f.modifyResponseBody(String.class, String.class, (exchanges, s) -> {
                            // 1.拿到request  判断登录
                            String path = exchanges.getRequest().getURI().getPath();
                            if (GatewayConstant.LOGIN_PATH.equals(path)) {
                                // 说明是登录的 拿到响应值
                                JSONObject jsonObject = JSON.parseObject(s);
                                String accessToken = jsonObject.getString("access_token");
                                if (!StringUtils.isEmpty(accessToken)) {
                                    // 说明登录成功 存入redis 做一个有状态的标识
                                    Long expiresIn = jsonObject.getLong("expires_in");
                                    redisTemplate.opsForValue()
                                            .set(GatewayConstant.JWT_PREFIX + accessToken,
                                                    "",
                                                    Duration.ofSeconds(expiresIn));
                                }
                            }
                            return Mono.just(s);
                        })).uri("lb://auth-server"))
                .build();
    }


}
