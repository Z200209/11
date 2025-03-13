package red.mlz.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseValueVo {
    private String k;
    private String v;
}
