package com.example.accesstracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous method execution
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${access-tracker.async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${access-tracker.async.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${access-tracker.async.queue-capacity:25}")
    private int queueCapacity;

    @Value("${access-tracker.async.thread-name-prefix:access-tracker-async-}")
    private String threadNamePrefix;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
