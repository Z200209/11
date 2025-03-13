package red.mlz.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseTotalListVo {
    private List list;
    private String wp;
    private Boolean isEnd;
    private Integer total;
}
