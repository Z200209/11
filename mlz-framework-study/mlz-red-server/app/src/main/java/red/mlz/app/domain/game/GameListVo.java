package red.mlz.app.domain.game;

import java.math.BigInteger;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.app.domain.BaseListItemVo;

@Data
@Accessors(chain = true)
public class GameListVo implements BaseListItemVo {
    private BigInteger gameId;
    private String gameName;
    private String typeName;
    private ImageVo image;
} 