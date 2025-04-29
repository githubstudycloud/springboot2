package com.example.common.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger配置
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        // 创建OpenAPI对象
        OpenAPI openAPI = new OpenAPI();
        
        // 设置基本信息
        openAPI.info(createInfo());
        
        // 设置调试地址
        if (StringUtils.hasText(swaggerProperties.getTryHost())) {
            List<Server> servers = new ArrayList<>();
            servers.add(new Server().url(swaggerProperties.getTryHost()));
            openAPI.servers(servers);
        }
        
        return openAPI;
    }

    /**
     * 创建API信息
     */
    private Info createInfo() {
        // 创建联系人信息
        Contact contact = new Contact()
                .name(swaggerProperties.getContact().getName())
                .url(swaggerProperties.getContact().getUrl())
                .email(swaggerProperties.getContact().getEmail());
        
        // 创建API描述信息
        return new Info()
                .title(swaggerProperties.getApplicationName())
                .description(swaggerProperties.getApplicationDescription())
                .version(swaggerProperties.getApplicationVersion())
                .contact(contact)
                .termsOfService(swaggerProperties.getTermsOfServiceUrl())
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
