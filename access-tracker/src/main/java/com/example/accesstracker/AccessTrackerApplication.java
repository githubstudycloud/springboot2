package com.example.accesstracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Enable asynchronous method execution
@MapperScan("com.example.accesstracker.dao") // Scan for MyBatis mappers
public class AccessTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessTrackerApplication.class, args);
    }
}
