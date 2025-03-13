package com.example.console.controller;

import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;


@RequestMapping()
@RestController
public class UserController {
    @Autowired
    private UserService userService;

@RequestMapping("/user/console/login")
public String login(@RequestParam(name = "phone") String phone,
                    @RequestParam(name = "password") String password,
                    HttpServletResponse response) {
    password = password.trim();
    phone = phone.trim();
    if (phone.isEmpty() || password.isEmpty()) {
        return "手机号或密码不能为空";
    }
     User user =  userService.getUserByPhone(phone);
    if (user == null) {
        return "手机号不存在";
    }
    if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
        return "密码错误";
    }
    Sign sign = new Sign();
    sign.setId(userService.login(phone, password).getId());
    int time = (int) (System.currentTimeMillis() / 1000 + 3600 * 3);
    sign.setExpirationTime(time);
    String token = Base64.getUrlEncoder().encodeToString(JSON.toJSONString(sign).getBytes());
    Cookie cookie = new Cookie("auth_token", token);
    cookie.setMaxAge( 3 * 60 * 60);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    return "登录成功";
}

}
