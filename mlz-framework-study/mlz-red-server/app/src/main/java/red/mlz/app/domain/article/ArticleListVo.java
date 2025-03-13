package red.mlz.app.domain.article;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class ArticleListVo {
    private String title;
    private String image;
    private String authorName;
    private BigInteger articleId;
}
