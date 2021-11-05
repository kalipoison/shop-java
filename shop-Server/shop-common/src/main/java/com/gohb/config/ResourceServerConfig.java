package com.gohb.config;

import cn.hutool.core.io.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyPair;

/**
 * 统一对jwt解析
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启方法级的验证
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 做公钥的解析
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        ClassPathResource resource = new ClassPathResource("publicKey.txt");
        String publicKey = null;
        try{
            publicKey = FileUtil.readString(resource.getFile(), Charset.defaultCharset());
        } catch (IOException e){
            e.printStackTrace();
        }
        jwtAccessTokenConverter.setVerifierKey(publicKey);
        return jwtAccessTokenConverter;
    }

    /**
     * 设置资源
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore());
    }

    /**
     * 放行路径 所有都依赖common
     * 放行swagger路径
     * http设置
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 因为使用jwt, 不需要session
        http.sessionManagement().disable();
        http.authorizeRequests().antMatchers(
                "/v2/api-docs",
                "/v3/api-docs",
                "/swagger-resources/configuration/ui",  //用来获取支持的动作
                "/swagger-resources",                   //用来获取api-docs的URI
                "/swagger-resources/configuration/security",//安全选项
                "/webjars/**",
                "/swagger-ui/**",
                "/druid/**",
                "/actuator/**",
                "/payNotify/**" // 支付宝调用我们 相当于就是正常的客户端请求服务器 要放行这个接口
        ).permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .headers()
                .cacheControl(); // 控制请求头的缓存
        super.configure(http);
    }
}
