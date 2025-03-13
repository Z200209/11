package red.mlz.app.domain.article;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArticleWpVo {
    private Integer page;
    private String key;
}
