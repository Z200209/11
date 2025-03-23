package red.mlz.app.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class TypeDetailVO {
    private BigInteger typeId;
    private String typeName;
    private BigInteger parentId;
    private String image;
    private Integer createTime;
    private Integer updateTime;
} 