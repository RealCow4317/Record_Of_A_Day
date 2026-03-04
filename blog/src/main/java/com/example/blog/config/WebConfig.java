package com.example.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcePath;
        // OS 확인하여 경로 접두사 설정
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            resourcePath = "file:///" + uploadDir;
        } else {
            resourcePath = "file:" + uploadDir;
        }
        
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(resourcePath);
    }
}
