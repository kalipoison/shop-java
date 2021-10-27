package com.gohb.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gohb.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * jwt的校验
 */
@Configuration
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 1.判断是否为登录的 如果是登录 就直接放行
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 拿到request URL
        String path = exchange.getRequest().getURI().getPath();
        if (GatewayConstant.ALLOW_URL.contains(path)) {
            // 放行
            return chain.filter(exchange);
        }
        // 需要检查头部的token
        List<String> tokenList = exchange.getRequest().getHeaders().get(GatewayConstant.AUTHORIZATION);
        if (!CollectionUtils.isEmpty(tokenList)) {
            String token = tokenList.get(0);
            if (!StringUtils.isEmpty(token)) {
                // 截取token  bearer xxxx
                String jwt = token.replaceAll("bearer ", "");
                if (!StringUtils.isEmpty(jwt)) {
                    // 和redis做匹配 我们需要把jwt存入redis  并不是做共享session ，只是要做一个有状态的标识
                    Boolean hasKey = redisTemplate.hasKey(GatewayConstant.JWT_PREFIX + jwt);
                    if (hasKey) {
                        // 如果有 那么就放行
                        return chain.filter(exchange);
                    }
                }
            }
        }
        // 代码走到这里就需要拦截 401 前端就会自动帮我们跳到登录
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        HashMap<String, Object> data = new HashMap<>(4);
        data.put("code", 401);
        data.put("msg", "未授权");
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer wrap = dataBufferFactory.wrap(bytes);
        return response.writeWith(Mono.just(wrap));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
