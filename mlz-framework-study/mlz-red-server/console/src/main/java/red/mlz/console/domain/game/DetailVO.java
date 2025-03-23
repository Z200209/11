package red.mlz.console.domain.game;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DetailVO {
    private BigInteger gameId;
    private String gameName;
    private Float price;
    private String gameIntroduction;
    private String gameDate;
    private String gamePublisher;
    private String typeName;
    private String typeImage;
    private List<String> images;
    private String createTime;
    private String updateTime;
} 