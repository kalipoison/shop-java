package com.gohb.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class OpenFeignInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            // 有请求 1. 是我们的前端的请求  2. 可能是其他系统的回调那么有request 但是没有token
            HttpServletRequest request = requestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                String authorization = request.getHeader("Authorization");
                if (!StringUtils.isEmpty(authorization)) {
                    // 往新的请求里面放 做一个传递
                    template.header("Authorization", authorization);
                    return;
                }
            }
        }
        // 都是没有token的 比如mq的自发调用 其他应用的回调
        // 设置一个永久的token 保证他可以正常调用其他服务
        template.header("Authorization", "bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImV4cCI6Mzc2MTU1NjcwNiwianRpIjoiNjc3YTA0YzMtMDZhMC00YTZkLTlmZDgtNDkzMzVmN2MwNDY4IiwiY2xpZW50X2lkIjoiY2xpZW50In0.Kqa83oWTtIfnBFFjWueDX444MA2l9PQfRyNDfFmfkqXIftUwZCHQRVq4hJkYR2l8o5H8i1FkjCJzO6gkf8WtedtOBtyeqaV_HNcuwA4hkvgh59TcicKWs6ar_fFlzOlVZ8uGRNM-CWZpcMchYfeHLDfyVBEgFv3CMZHZ82gUcVwyi5vSHre20QKo6CVgRqTwvLKfchSmJd46fz49vJBG9mY9xb6FMnjwDRaCXUXhGVLbsxMeRTW5yUsa7UdLYnkpK8Nlk8nNYKN_O1KH7u_1-9QJVCI5N8qZGGb-51KaPtjjtBJ-NitMyGVboS5wCd5sCtyPOlkHUdyNvW_CAQWP4g");
    }

}
