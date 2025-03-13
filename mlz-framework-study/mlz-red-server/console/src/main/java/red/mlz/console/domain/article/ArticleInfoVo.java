package red.mlz.console.domain.article;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.console.domain.BaseContentValueVo;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class ArticleInfoVo {
    private BigInteger articleId;
    private BigInteger userId;
    private String title;
    private String coverImage;
    private String createTime;
    private List<String> tags;
    private List<BaseContentValueVo> content;
    private Integer weight;
}
