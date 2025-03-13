package red.mlz.module.module.article.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArticleContentDto {
    private String type;
    private String content;
}
