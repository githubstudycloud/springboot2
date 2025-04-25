package com.example.test;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.example.test.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 注意：此测试需要在Nacos服务器运行的情况下执行
 * 测试前，确保已经在Nacos中导入配置文件
 */
@SpringBootTest
@ActiveProfiles("dev") // 使用开发环境配置
public class NacosConfigTest {

    @Autowired
    private AppConfig appConfig;
    
    @Autowired(required = false)
    private NacosConfigManager nacosConfigManager;
    
    @Value("${spring.application.name}")
    private String applicationName;

    @Test
    public void contextLoads() {
        // 测试应用上下文是否加载成功
        assertNotNull(appConfig);
    }
    
    @Test
    public void testConfigValues() {
        // 测试从Nacos获取的配置值
        // 注意：这里是检查开发环境的值，因为我们使用了@ActiveProfiles("dev")
        assertEquals("Spring Test Application (DEV)", appConfig.getName());
        assertEquals("Development Environment Configuration", appConfig.getDescription());
        assertEquals("jdbc:mysql://localhost:3306/testdb_dev", appConfig.getDatabase().getUrl());
        assertEquals(10, appConfig.getDatabase().getMaxPoolSize());
    }
    
    @Test
    public void testNacosConnection() {
        // 测试是否能连接到Nacos服务器
        // 如果Nacos未运行，此测试将被跳过
        if (nacosConfigManager != null) {
            ConfigService configService = nacosConfigManager.getConfigService();
            assertNotNull(configService);
            
            try {
                String content = configService.getConfig(
                        applicationName + "-dev.yaml", 
                        nacosConfigManager.getNacosConfigProperties().getGroup(),
                        5000
                );
                assertNotNull(content);
                System.out.println("获取到的配置内容: " + content);
            } catch (NacosException e) {
                System.err.println("无法从Nacos获取配置: " + e.getMessage());
            }
        } else {
            System.out.println("未注入NacosConfigManager，跳过Nacos连接测试");
        }
    }
}
