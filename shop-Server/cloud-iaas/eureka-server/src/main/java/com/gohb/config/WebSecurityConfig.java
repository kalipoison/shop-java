package com.gohb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * spring-security 自定义配置
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置 http 请求
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭csrf攻击
        http.csrf().disable();
        // 放行一个监控的路径 /actuator/**
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        // 调用父类的方法 把http的请求配置进去
        super.configure(http);
    }
}
