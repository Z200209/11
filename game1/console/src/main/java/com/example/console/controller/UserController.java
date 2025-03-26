package com.example.console.controller;

import com.alibaba.fastjson.JSON;
import com.example.console.annotations.VerifiedUser;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import com.example.module.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/console")
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public Response login(@RequestParam(name = "phone") String phone,
                                 @RequestParam(name = "password") String password,
                                 HttpServletResponse response) {
        try {
            // 参数验证
            password = password.trim();
            phone = phone.trim();
            if (phone.isEmpty() || password.isEmpty()) {
                return new Response<>(4005);
            }
            
            // 验证手机号是否存在
            User user = userService.getUserByPhone(phone);
            if (user == null) {
                return new Response<>(2014);
            }
            
            // 验证密码
            if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                return new Response<>(1010);
            }
            
            // 生成签名
            Sign sign = new Sign();
            sign.setId(user.getId());
            int time = (int) (System.currentTimeMillis() / 1000 + 3600 * 3); // 3小时有效期
            sign.setExpirationTime(time);
            String token = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes()
            );
            
            // 设置Cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setMaxAge(3 * 60 * 60);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            
            return new Response<>(1001);
        } catch (Exception e) {
            log.error("登录失败", e);
            return new Response<>(4004);
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @RequestMapping("/info")
    public Response getUserInfo(@VerifiedUser User loginUser) {
        try {
            // 验证用户是否登录
            if (loginUser == null) {
                return new Response<>(1002);
            }
            
            // 构建用户信息，不包含敏感数据
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", loginUser.getId());
            userInfo.put("phone", loginUser.getPhone());
            userInfo.put("name", loginUser.getName());
            userInfo.put("avatar", loginUser.getAvatar());
            
            return new Response<>(1001, userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return new Response<>(4004);
        }
    }
    
    /**
     * 退出登录
     */
    @RequestMapping("/logout")
    public Response logout(HttpServletResponse response) {
        try {
            // 清除Cookie
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            
            return new Response(1001);
        } catch (Exception e) {
            log.error("退出失败", e);
            return new Response<>(4004);
        }
    }
}
