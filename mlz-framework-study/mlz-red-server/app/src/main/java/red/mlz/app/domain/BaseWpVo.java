package red.mlz.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseWpVo {
    private Integer page;
}
