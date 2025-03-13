package red.mlz.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseListVo {
    private List list;
    private int total;
    private int pageSize;
}
