package red.mlz.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseContentValueVo {
    private String type;
    private String content;
}
