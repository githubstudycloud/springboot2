package com.example.test.service;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

@Service
public class NacosConfigService {
    private static final Logger logger = LoggerFactory.getLogger(NacosConfigService.class);

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.nacos.config.file-extension}")
    private String fileExtension;

    @PostConstruct
    public void init() {
        // 添加配置监听器，当配置变更时会自动调用
        try {
            ConfigService configService = nacosConfigManager.getConfigService();
            String dataId = applicationName + "." + fileExtension;
            String group = nacosConfigManager.getNacosConfigProperties().getGroup();
            
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null; // 使用默认执行器
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    logger.info("配置已更新: {}", configInfo);
                    // 这里不需要手动刷新，Spring Cloud Alibaba Nacos Config会自动刷新
                    logger.info("配置已自动刷新应用上下文");
                }
            });
            
            logger.info("已为配置 {}/{} 添加监听器", group, dataId);
        } catch (NacosException e) {
            logger.error("添加Nacos配置监听器失败", e);
        }
    }

    /**
     * 手动获取当前配置内容
     */
    public String getCurrentConfig() {
        try {
            ConfigService configService = nacosConfigManager.getConfigService();
            String dataId = applicationName + "." + fileExtension;
            String group = nacosConfigManager.getNacosConfigProperties().getGroup();
            
            return configService.getConfig(dataId, group, 5000);
        } catch (NacosException e) {
            logger.error("从Nacos获取配置失败", e);
            return "获取配置失败: " + e.getMessage();
        }
    }
}
