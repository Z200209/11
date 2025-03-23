package red.mlz.console.domain.game;

import java.math.BigInteger;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TypeDetailVO {
    private BigInteger typeId;
    private String typeName;
    private BigInteger parentId;
    private String image;
    private String createTime;
    private String updateTime;
} 