package com.example.test.controller;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.example.test.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RefreshScope
public class ConfigTestController {
    private static final Logger logger = LoggerFactory.getLogger(ConfigTestController.class);

    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    private NacosConfigManager nacosConfigManager;
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${spring.cloud.nacos.config.file-extension}")
    private String fileExtension;

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("name", appConfig.getName());
        config.put("description", appConfig.getDescription());
        config.put("database", appConfig.getDatabase());
        return config;
    }
    
    @GetMapping("/nacos-info")
    public Map<String, Object> getNacosInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", applicationName);
        info.put("configService", nacosConfigManager.getConfigService().getClass().getName());
        try {
            // 获取当前配置内容
            String configContent = nacosConfigManager.getConfigService().getConfig(
                    applicationName + "." + fileExtension, 
                    nacosConfigManager.getNacosConfigProperties().getGroup(),
                    5000
            );
            info.put("currentConfig", configContent);
        } catch (NacosException e) {
            logger.error("Failed to get config from Nacos", e);
            info.put("error", e.getMessage());
        }
        return info;
    }
    
    @GetMapping("/refresh-now")
    public Map<String, String> refreshNow() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Configuration has been refreshed automatically by Nacos");
        result.put("note", "With Nacos, manual refresh using /actuator/refresh endpoint is not needed");
        return result;
    }
}
