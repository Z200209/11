package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@RequestMapping()
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/app/login")
    public String login(@RequestParam(name = "phone") String phone,@RequestParam(name = "password") String password) {
        password = password.trim();
        phone = phone.trim();
        if (phone.isEmpty() || password.isEmpty())
        {
            return "手机号或密码不能为空";
        }
        if(userService.getUserByPhone(phone)==null){
            return "手机号不存在";
        }
        if (userService.login(phone,password)==null){
            return "密码错误";
        }
        Sign sign = new Sign();
        sign.setId(userService.login(phone,password).getId());
        int time = (int) (System.currentTimeMillis() / 1000);
        sign.setExpirationTime(time+3600*3);
        String encodesign = Base64.getEncoder().encodeToString(JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8));
        return encodesign;
    }

    @RequestMapping("/user/app/register")
    public String register(@RequestParam(name = "phone") String phone,
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "avatar") String avatar) {
        if (phone == null || password == null|| name == null|| avatar == null){
            return "数据不能为空";
        }
        if(userService.getUserByPhone(phone) != null){
            return "手机号已存在";
        }
        if(userService.register(phone,password,name,avatar)==1){
            return "注册成功";
        }else{
            return "注册失败";
        }
    }

    @RequestMapping("/user/app/update")
    public String update(@RequestParam(name = "phone",required =false)String phone,
                         @RequestParam(name = "password",required =false) String password,
                         @RequestParam(name = "name",required =false) String name,
                         @RequestParam(name = "avatar",required =false) String avatar,
                         @RequestParam(name = "sign") String sign)  {
        if(sign==null){
            throw new RuntimeException("用户未登录");
        }
        byte[] bytes = Base64.getDecoder().decode(sign);
        String json = new String(bytes, StandardCharsets.UTF_8);
        Sign reviceSign = JSON.parseObject(json, Sign.class);
        if (userService.getUserById(reviceSign.getId())==null){
            return "用户不存在";
        }
        if (reviceSign.getExpirationTime()<(int) (System.currentTimeMillis() / 1000)){
            return "登录过期";
        }

        User user = userService.getUserById(reviceSign.getId());
        if (phone != null){
            user.setPhone(phone);
        }
        if (password != null){
            user.setPassword(password);
        }
        if (name != null){
            user.setName(name);
        }
        if (avatar != null){
            user.setAvatar(avatar);
        }
        int result = userService.updateInfo(user.getId(),user.getPhone(),user.getPassword(),user.getName(),user.getAvatar());
        if (result == 0){
            return "更新失败";
        }
        return "更新成功";

    }
}
