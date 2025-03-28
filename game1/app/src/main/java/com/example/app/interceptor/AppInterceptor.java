package com.example.app.interceptor;


import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.utils.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;

@Component
@Slf4j
public class AppInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if(request.getRequestURI().startsWith("/app/user/login")||request.getRequestURI().startsWith("/app/user/register")){
            return true;
        }// 放行登录和注册接口
        try {
            String jsonStr = request.getParameter("sign");
            if(jsonStr==null|| jsonStr.isEmpty())
            {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                log.info("未找到sign参数");
                response.getWriter().write(JSON.toJSONString(new Response(1002)));
                return false;
            }
            //解密
            String jsonStrDecode = new String(Base64.getDecoder().decode(jsonStr));
            Sign sign = JSON.parseObject(jsonStrDecode, Sign.class);
            // 验证签名是否过期
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            if (sign.getExpirationTime() < currentTime) {
                log.info("签名已过期: expirationTime={}, currentTime={}", sign.getExpirationTime(), currentTime);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new Response(1002)));
                return false;
            }
            return true;
        }
        catch (Exception e) {
            // 处理异常
            log.info("Session validation failed", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Response(4004)));
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