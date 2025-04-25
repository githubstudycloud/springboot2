# Spring Boot 父子结构项目（使用Nacos配置中心）

这个项目演示了如何使用Spring Boot和Spring Cloud Alibaba构建一个使用Nacos作为配置中心和服务发现的应用。

## 项目结构

```
springboot-parent/
├── pom.xml                         # 父项目POM文件
└── spring-test/                    # 测试应用模块
    ├── pom.xml                     # 测试应用POM文件
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/example/test/
        │   │       ├── config/      # 配置类
        │   │       ├── controller/  # REST控制器
        │   │       ├── service/     # 服务类
        │   │       └── TestApplication.java  # 主应用类
        │   └── resources/
        │       ├── application.yml  # 应用配置
        │       └── bootstrap.yml    # 引导配置（Nacos配置）
        └── test/
            └── java/
                └── com/example/test/
                    └── NacosConfigTest.java  # 测试类
```

## 准备工作

1. 下载和安装Nacos服务器：
   - 从[Nacos GitHub](https://github.com/alibaba/nacos/releases)下载最新版本
   - 解压并启动：
     ```bash
     # 单机模式启动
     sh startup.sh -m standalone
     ```
   - 访问管理界面：http://localhost:8848/nacos (默认用户名/密码: nacos/nacos)

2. 在Nacos中创建配置：
   - 登录Nacos控制台
   - 选择"配置管理" -> "配置列表" -> "+"
   - 创建以下配置：
     - Data ID: `spring-test.yaml`
     - Data ID: `spring-test-dev.yaml`
     - Data ID: `spring-test-prod.yaml`
   - 使用`nacos-config-example`目录中的配置文件内容

## 运行项目

1. 启动Nacos服务器
2. 启动测试应用：
   ```bash
   cd springboot-parent/spring-test
   mvn spring-boot:run
   ```

3. 测试配置获取：
   - 访问 http://localhost:8080/api/config 
   - 访问 http://localhost:8080/api/nacos-info

## 环境切换

修改`bootstrap.yml`中的`spring.profiles.active`属性来切换环境：

```yaml
spring:
  profiles:
    active: dev  # 改为prod使用生产环境配置
```

## 动态配置更新

Nacos支持配置的动态更新。当在Nacos控制台修改配置后，应用会自动收到更新并刷新配置，无需重启应用或手动触发刷新。

## 集群部署

该项目支持集群部署，多个应用实例可以连接到相同的Nacos集群获取配置，实现配置的统一管理。

要配置Nacos集群，请参考[Nacos官方文档](https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html)。
