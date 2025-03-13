package red.mlz.app.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GameWpVo {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private BigInteger typeId;
} 