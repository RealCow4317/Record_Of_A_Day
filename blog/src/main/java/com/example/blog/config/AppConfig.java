package com.example.blog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@MapperScan("com.example.blog.dao")
@EnableScheduling
public class AppConfig {
}