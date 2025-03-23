package red.mlz.console.domain.game;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TypeTreeVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
    private List<TypeTreeVO> childrenList;
} 