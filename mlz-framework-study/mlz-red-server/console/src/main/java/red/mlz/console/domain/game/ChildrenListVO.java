package red.mlz.console.domain.game;

import java.math.BigInteger;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChildrenListVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
} 