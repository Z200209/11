package red.mlz.console.controller.user;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import red.mlz.console.annotations.VerifiedUser;
import red.mlz.console.domain.user.UserInfoVo;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.IpUtils;
import red.mlz.module.utils.Response;
import red.mlz.module.utils.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
@Slf4j
public class  UserController {
    @Autowired
    BaseUserService baseUserService;


    @RequestMapping("/user/login/web")
    public Response loginWeb(@VerifiedUser User loginUser,
                             HttpSession httpSession,
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "remember") boolean remember) {
        if (!BaseUtils.isEmpty(loginUser)) {
            return new Response(4004);
        }

        boolean result;
        if (remember) {
            result = baseUserService.login(phone, password);
        } else {
            result = baseUserService.login(phone, "86", password, false, false, 0);
        }
        if (!result) {
            return new Response(1010);
        }

        User user = baseUserService.getByPhone(phone);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserGender(user.getGender());
        userInfo.setUserName(user.getUsername());
        userInfo.setUserPhone(user.getPhone());
        userInfo.setUserAvatar(user.getAvatar());

        // 写session
        httpSession.setAttribute(SpringUtils.getProperty("application.session.key"), JSON.toJSONString(user));

        return new Response(1001, userInfo);
    }

}
