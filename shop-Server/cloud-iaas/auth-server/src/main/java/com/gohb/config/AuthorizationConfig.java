package com.gohb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.IOException;
import java.security.KeyPair;

/**
 * token存储
 * jwt转换器
 * 第三方应用
 * endpoint暴露
 */
@Configuration
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 因为common有这个bean对象了 我就不需要了
     *
     * @return
     */
//    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }



    /**
     * 非对称加密
     * @return
     */
//    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        ClassPathResource resource = new ClassPathResource("cxs-jwt.jks");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "cxs123".toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("cxs-jwt");
        jwtAccessTokenConverter.setKeyPair(keyPair);
        return jwtAccessTokenConverter;
    }

    /**
     * 配置第三方应用
     * password 只用在登录使用
     * 客户端授权 用户微服务之间自发的进行远程调用时，资源服务器必须要有token的情况
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("web")
                .secret(passwordEncoder.encode("web-secret"))
                .scopes("all")
                .authorizedGrantTypes("password")
                .accessTokenValiditySeconds(7200)
                .redirectUris("https://www.baidu.com")
                .and()
                .withClient("client")
                .secret(passwordEncoder.encode("client-secret"))
                .scopes("read")
                .authorizedGrantTypes("client_credentials")
                .accessTokenValiditySeconds(Integer.MAX_VALUE) // 66年
                .redirectUris("https://www.baidu.com");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager);
    }




}
