package red.mlz.app.domain.game;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GameInfoVo {
    private BigInteger gameId;
    private String gameName;
    private String typeName;
    private String typeImage;
    private Float price;
    private String gameIntroduction;
    private String gameDate;
    private String gamePublisher;
    private List<String> images;
} 