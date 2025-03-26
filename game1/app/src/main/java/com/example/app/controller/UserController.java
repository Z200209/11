package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.annotations.VerifiedUser;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/app")
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public Response login(@RequestParam(name = "phone") String phone,
                         @RequestParam(name = "password") String password) {
        try {
            // 参数验证
            password = password.trim();
            phone = phone.trim();
            if (phone.isEmpty() || password.isEmpty()) {
                return new Response<>(4005);
            }
            
            // 验证手机号是否存在
            if (userService.getUserByPhone(phone) == null) {
                return new Response<>(2014);
            }
            
            // 登录验证
            User user = userService.login(phone, password);
            if (user == null) {
                return new Response<>(1010);
            }
            
            // 生成签名
            Sign sign = new Sign();
            sign.setId(user.getId());
            int time = (int) (System.currentTimeMillis() / 1000);
            sign.setExpirationTime(time + 3600 * 3); // 3小时有效期
            String encodedSign = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8)
            );
            
            return new Response<>(1001, encodedSign);
        } catch (Exception e) {
            log.error("登录失败", e);
            return new Response<>(4004);
        }
    }

    /**
     * 用户注册
     */
    @RequestMapping("/register")
    public Response register(@RequestParam(name = "phone") String phone,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "avatar") String avatar) {
        try {
            // 参数验证
            phone = phone.trim();
            password = password.trim();
            if (phone.isEmpty() || password.isEmpty() || name == null || avatar == null) {
                return new Response<>(4005);
            }
            
            // 验证手机号是否已存在
            if (userService.getUserByPhone(phone) != null) {
                return new Response<>(2015);
            }
            
            // 注册用户
            int result = userService.register(phone, password, name, avatar);
            if (result == 1) {
                return new Response<>(1001, "注册成功");
            } else {
                return new Response<>(4004);
            }
        } catch (Exception e) {
            log.error("注册失败", e);
            return new Response<>(4004);
        }
    }

    /**
     * 更新用户信息
     */
    @RequestMapping("/update")
    public Response update(@VerifiedUser User loginUser,
                          @RequestParam(name = "phone", required = false) String phone,
                          @RequestParam(name = "password", required = false) String password,
                          @RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "avatar", required = false) String avatar) {
        try {
            // 验证用户是否登录
            if (loginUser == null) {
                return new Response<>(1002);
            }
            
            // 参数验证
            if (phone != null) {
                phone = phone.trim();
            }
            if (password != null) {
                password = password.trim();
            }
            
            // 更新用户信息
            if (phone != null && !phone.isEmpty()) {
                loginUser.setPhone(phone);
            }
            if (password != null && !password.isEmpty()) {
                loginUser.setPassword(password);
            }
            if (name != null) {
                loginUser.setName(name);
            }
            if (avatar != null) {
                loginUser.setAvatar(avatar);
            }
            
            // 提交更新
            int result = userService.updateInfo(
                    loginUser.getId(), loginUser.getPhone(), loginUser.getPassword(), loginUser.getName(), loginUser.getAvatar()
            );
            
            if (result == 0) {
                return new Response<>(4004);
            }
            
            return new Response(1001);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return new Response<>(4004);
        }
    }
    
    /**
     * 获取用户信息
     */
    @RequestMapping("/info")
    public Response getUserInfo(@VerifiedUser User loginUser) {
        try {
            // 验证用户是否登录
            if (loginUser == null) {
                return new Response<>(1002);
            }

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
}
