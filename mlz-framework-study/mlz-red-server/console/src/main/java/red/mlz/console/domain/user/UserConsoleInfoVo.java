package red.mlz.console.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class UserConsoleInfoVo {
    private BigInteger userId;
    private String userName;
    private String userGender;
    private String userPhone;
    private String userAvatar;
    private Integer userLevel;
}
