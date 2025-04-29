package com.example.common.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Swagger配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 是否启用Swagger
     */
    private Boolean enabled = true;

    /**
     * 项目应用名
     */
    private String applicationName;

    /**
     * 项目版本信息
     */
    private String applicationVersion;

    /**
     * 项目描述信息
     */
    private String applicationDescription;

    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl;

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 接口调试地址
     */
    private String tryHost;

    /**
     * 联系人信息
     */
    @Data
    public static class Contact {
        /**
         * 联系人
         */
        private String name;

        /**
         * 联系人URL
         */
        private String url;

        /**
         * 联系人邮箱
         */
        private String email;
    }
}
