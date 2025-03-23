package red.mlz.app.controller.user;


import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import red.mlz.app.annotations.VerifiedUser;
import red.mlz.app.domain.user.UserInfoVo;
import red.mlz.app.domain.user.UserLoginInfoVo;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.module.user.service.UserDefine;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.IpUtils;
import red.mlz.module.utils.Response;
import red.mlz.module.utils.SignUtils;




@RestController
@Slf4j
public class UserController {

    @Autowired
    private BaseUserService baseUserService;


    @RequestMapping("/user/login/app")
    public Response loginApp(@VerifiedUser User loginUser,
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password) {
        if (!BaseUtils.isEmpty(loginUser)) {
            return new Response(4004);
        }
        //合法用户直接登录
        boolean result = baseUserService.login(phone, password);
        if (!result) {
            return new Response(1010);
        }
        User user = baseUserService.getByPhone(phone);

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();  // 获取request对象
        baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());       // 更新用户登录信息

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtils.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }

    @RequestMapping("/user/register/app")
    public Response registerApp(@VerifiedUser User loginUser,
                                @RequestParam(name = "phone") String phone,
                                @RequestParam(name = "gender") Integer gender,
                                @RequestParam(name = "avatar", required = false) String avatar,
                                @RequestParam(name = "name") String name,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "country", required = false) String country,
                                @RequestParam(name = "province", required = false) String province,
                                @RequestParam(name = "city", required = false) String city) {
        if (!BaseUtils.isEmpty(loginUser)) {
            return new Response(4004);
        }

        //考虑用户已经注册了
        //即phone存在
        //直接按照登录处理，返回sign
        User user = baseUserService.extractByPhone(phone,"86");
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        BigInteger newUserId;
        if(!BaseUtils.isEmpty(user)){
            //如果用户被禁止登录
            if(user.getIsDeleted().equals(1) || user.getIsBan().equals(1)){
                return new Response(1010);
            }
            newUserId = user.getId();
            baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());
        }else {
            //注册新用户
            if (!UserDefine.isGender(gender)) {
                return new Response(2014);
            }
            try {
                newUserId = baseUserService.registerUser(name, phone, gender, avatar, password,country, province, city, IpUtils.getIpAddress(request));
            } catch (Exception exception) {
                return new Response(4004);
            }

        }
        user = baseUserService.getById(newUserId);

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtils.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }
}
