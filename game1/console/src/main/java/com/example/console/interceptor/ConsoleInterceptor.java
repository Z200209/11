package com.example.console.interceptor;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.utils.Response;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class  ConsoleInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if(request.getRequestURI().startsWith("/console/user/login")){
            return true;
        }// 放行登录和注册接口
        try {
            //从Cookie中获取token
            Cookie[] cookies = request.getCookies();
            String token = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("auth_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            
            // 验证token
            if (token == null || token.isEmpty()) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                log.info("未找到auth_token Cookie");
                response.getWriter().write(JSON.toJSONString(new Response(1002)));
                return false;
            }
            // 解析token
            String jsonStr = new String(Base64.getUrlDecoder().decode(token));
            Sign sign = JSON.parseObject(jsonStr, Sign.class);

            // 验证token是否过期
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            if (sign.getExpirationTime() < currentTime) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new Response(1002)));
                log.info("token已过期: expirationTime={}, currentTime={}", sign.getExpirationTime(), currentTime);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            // 处理异常
            log.error("Session validation failed", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Response<>(4004)));
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("postHandle: " + request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("afterCompletion: " + request.getRequestURI());
    }

}