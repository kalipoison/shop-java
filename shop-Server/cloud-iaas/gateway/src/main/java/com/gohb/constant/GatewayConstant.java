package com.gohb.constant;

import java.util.Arrays;
import java.util.List;

public interface GatewayConstant {

    /**
     * 放行的路径
     */
    List<String> ALLOW_URL = Arrays.asList("/oauth/token");

    /**
     * 登录的路径
     */
    String LOGIN_PATH = "/oauth/token";


    /**
     * 请求头的token的key
     */
    String AUTHORIZATION = "Authorization";

    /**
     * 存入redis的前缀
     */
    String JWT_PREFIX = "jwt:token:";
}

