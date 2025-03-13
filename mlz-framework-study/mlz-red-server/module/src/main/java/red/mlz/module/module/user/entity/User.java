package red.mlz.module.module.user.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.module.utils.BaseUtils;

import java.math.BigInteger;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ancientnine
 * @since 2021-11-28
 */
@Data
@Accessors(chain = true)
public class User{


    /**
     * 用户id
     */
    private BigInteger id;

    private String countryCode;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 注册邮箱号
     */
    private String email;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户头像url
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String personalProfile;

    /**
     * 用户背景封面
     */
    private String coverImage;

    /**
     * 性别，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日 1999-01-01
     */
    private String birthday;

    /**
     * 微信
     */
    private String wechatOpenId;

    private String wechatUnionId;

    /**
     * 微信号
     */
    private String wechatNo;

    private String country;

    private String province;

    private String city;

    /**
     * 记录用户加入时间
     */
    private Integer registerTime;

    /**
     * 注册ip
     */
    private String registerIp;

    /**
     * 最后登录时间
     */
    private Integer lastLoginTime;

    /**
     * 最后登录ip
     */
    private String lastLoginIp;

    /**
     * 是否禁用1-是0-否
     */
    private Integer isBan;

    /**
     * json
     */
    private String extra;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 修改时间
     */
    private Integer updateTime = BaseUtils.currentSeconds();

    /**
     * 是否删除 1-是 0-否
     */
    private Integer isDeleted;


}
