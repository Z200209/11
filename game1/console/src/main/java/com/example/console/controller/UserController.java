package com.example.console.controller;

import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@RequestMapping()
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/console/login")
    public String login(String phone, String password) {
        if (phone == null || password == null){
            throw new RuntimeException("手机号或密码不能为空");
        }
        if(userService.getUserByPhone(phone) == null){
            throw new RuntimeException("手机号不存在");

        }
        if (userService.login(phone, password) == null){
            throw new RuntimeException("密码错误");
        }
        Sign sign = new Sign();
        sign.setId(userService.login(phone,password).getId());
        int time = (int) (System.currentTimeMillis() / 1000);
        sign.setExpirationTime(time+3600*3);
        String encodesign = Base64.getEncoder().encodeToString(JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8));
        return encodesign;
    }
}
