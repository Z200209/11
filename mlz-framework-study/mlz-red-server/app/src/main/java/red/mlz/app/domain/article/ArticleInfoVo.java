package red.mlz.app.domain.article;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.app.domain.BaseContentValueVo;

import java.util.List;

@Data
@Accessors(chain = true)
public class ArticleInfoVo {
    private String title;
    private String authorName;
    private String createTime;
    private List<BaseContentValueVo> content;
}
