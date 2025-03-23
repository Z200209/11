package com.example.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class IntroductionListVO {
    private List<Block> Blocks;

    @Data
    @Accessors(chain = true)
    public static class Block {
        private Integer order;
        private IntroductionType type;
        private String content;
    }
}
