package red.mlz.console.domain.game;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ListVO {
    private List<GameVO> gameList;
    private Integer total;
    private Integer pageSize;
} 