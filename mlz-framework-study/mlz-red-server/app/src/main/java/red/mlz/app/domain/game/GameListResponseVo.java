package red.mlz.app.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GameListResponseVo {
    private List<GameListVo> gameList;
    private String wp;
} 