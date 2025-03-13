package red.mlz.app.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageVo {
    private String src;
    private float ar;
} 