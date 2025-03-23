package com.example.console.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 允许的请求源（从配置文件中获取）
     */
    @Value("${cors.allowed-origins:https://*.example.com}")
    private String[] allowedOrigins;

    /**
     * 添加跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的请求源（从配置文件中读取）
                .allowedOriginPatterns(allowedOrigins)
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept")
                // 是否允许发送Cookie
                .allowCredentials(true)
                // 暴露的响应头
                .exposedHeaders("Authorization")
                // 预检请求的有效期，单位为秒
                .maxAge(3600);
    }
} 