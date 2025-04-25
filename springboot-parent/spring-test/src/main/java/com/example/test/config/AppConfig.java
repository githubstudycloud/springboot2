package com.example.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private String name;
    private String description;
    private Database database;
    
    @Data
    public static class Database {
        private String url;
        private String username;
        private String password;
        private int maxPoolSize;
    }
}
