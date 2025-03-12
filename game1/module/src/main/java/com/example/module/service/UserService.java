package com.example.module.service;

import com.example.module.entity.User;
import com.example.module.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserById(BigInteger id) {
        return userMapper.getUserById(id);
    }
    public int insert(User user) {
        return userMapper.insert(user);
    }

    public int update(User user) {
        return userMapper.update(user);
    }
    public User getUserByPhone(String phone) {
        if (phone == null){
            throw new RuntimeException("手机号不能为空");
        }
        return userMapper.getUserByPhone(phone);
    }

    public User login(String phone, String password) {
        if (phone == null || password == null){
            throw new RuntimeException("手机号或密码不能为空");
        }
        if (userMapper.getUserByPhone(phone)==null){
            throw new RuntimeException("手机号不存在");
        }
        if (userMapper.login(phone,password)==null){
            throw new RuntimeException("密码错误");
        }
        return userMapper.login(phone,password);
    }

    public int register(String phone, String password,String name, String avatar)
    {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (phone == null || password == null || name == null || avatar == null){
            throw new RuntimeException("参数不能为空");
        }
        User user = new User().setPhone(phone).
                setPassword(password).
                setName(name).
                setAvatar(avatar)
                .setCreateTime(time)
                .setUpdateTime(time);
        if (userMapper.getUserByPhone(user.getPhone())!=null){
            throw new RuntimeException("手机号已存在");
        }
        System.out.println(user);
        int result = insert(user);
        if (result == 0){
            throw new RuntimeException("注册失败");
        }
        return result;

    }

    public int exit(BigInteger id,String phone, String password, String name, String avatar) {
        int time = (int) (System.currentTimeMillis() / 1000);

        if (id == null || phone == null || password == null || name == null || avatar == null){
            throw new RuntimeException("参数不能为空");
        }
        User user = new User().setName(name)
                .setAvatar(avatar)
                .setPhone(phone)
                .setPassword(password)
                .setId(id)
                .setUpdateTime(time);
        int result = update(user);
        if (result == 0){
            throw new RuntimeException("更新失败");
        }
        return result;
    }

}
