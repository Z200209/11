package red.mlz.module.module.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.module.module.user.entity.User;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user WHERE id=#{id} and is_ban = 0 and is_deleted = 0")
    User getById(@Param("id") BigInteger id);

    @Select("select * from user WHERE id in (${ids}) and is_ban = 0 and is_deleted = 0")
    List<User> getByIds(@Param("ids") String ids);

    @Select("select * from user WHERE id=#{id}")
    User extractById(@Param("id") BigInteger id);

    @Select("select * from user WHERE phone=#{phone} and country_code=#{countryCode} and is_ban = 0 and is_deleted = 0")
    User getByPhone(@Param("phone") String phone, @Param("countryCode") String countryCode);

    @Select("select * from user WHERE phone=#{phone} and country_code=#{countryCode}")
    User extractByPhone(@Param("phone") String phone, @Param("countryCode") String countryCode);

    @Select("select * from user WHERE email=#{email}")
    User extractByEmail(@Param("email") String email);

    @Select("select * from user WHERE username=#{username} and is_ban = 0 and is_deleted = 0")
    User getByUsername(@Param("username") String username);

    @Select("select * from user WHERE wechat_open_id=#{wechatOpenId}")
    User extractUserByWxOpenId(@Param("wechatOpenId") String wechatOpenId);

    int update(@Param("user") User user);

    int insert(@Param("user") User user);

    @Update("update user set is_deleted=1, update_time=#{time} where id=#{id} limit 1")
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);


    @Select("select * from user where username like concat('%',#{username},'%') and is_ban = 0 and is_deleted = 0")
    List<User> getUsersByUsername(@Param("username") String username);

    List<User> getUsersForConsole(@Param("begin") int begin, @Param("size") int size, @Param("orderBy") String orderBy,
                        @Param("username") String username, @Param("phone") String phone);

    int getUsersTotalForConsole(@Param("username") String username, @Param("phone") String phone);
}
