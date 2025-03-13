package red.mlz.console.domain.article;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class ArticleListVo {
    private BigInteger articleId;
    private String title;
    private String coverImage;
    private String createTime;
}
