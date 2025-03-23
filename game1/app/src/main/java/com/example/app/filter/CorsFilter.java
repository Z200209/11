package com.example.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 跨域过滤器
 * 优先级设置为最高，确保在其他过滤器之前执行
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    /**
     * 允许的请求源（从配置文件中获取）
     */
    @Value("${cors.allowed-origins:https://*.example.com}")
    private String[] configuredAllowedOrigins;

    // 允许的请求头
    private final String allowedHeaders = "Authorization, Content-Type, X-Requested-With, Accept, sign";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        
        // 检查请求源是否在允许列表中
        boolean isAllowedOrigin = false;
        if (StringUtils.hasText(origin)) {
            // 将配置的模式转换为域名前缀进行匹配
            List<String> allowedOriginPrefixes = convertPatternsToPrefixes(configuredAllowedOrigins);
            
            for (String allowedOrigin : allowedOriginPrefixes) {
                if (origin.startsWith(allowedOrigin)) {
                    isAllowedOrigin = true;
                    // 设置跨域响应头，使用实际的origin
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    break;
                }
            }
        }
        
        // 如果不是允许的源，则不设置跨域头
        if (isAllowedOrigin) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            
            // 对于OPTIONS请求直接返回OK
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        // 继续过滤器链
        chain.doFilter(req, res);
    }


    private List<String> convertPatternsToPrefixes(String[] patterns) {
        return Arrays.stream(patterns)
                .map(pattern -> pattern.replace("*", ""))
                .toList();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化方法，不需要特殊处理
    }

    @Override
    public void destroy() {
        // 销毁方法，不需要特殊处理
    }
} 