package com.gohb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@EnableOpenApi // 开启swagger3的功能
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    /**
     * 创建swagger文档
     *
     * @return
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30).
                apiInfo(getApiInfo()).
                select().apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())).build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact(swaggerProperties.getName(), swaggerProperties.getUrl(), swaggerProperties.getEmail());
        return new ApiInfoBuilder().
                contact(contact).
                title(swaggerProperties.getTitle()).
                description(swaggerProperties.getDescription()).
                termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl()).
                license(swaggerProperties.getLicense()).
                licenseUrl(swaggerProperties.getLicenseUrl()).
                build();
    }
}