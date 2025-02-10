package com.example.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class ListVO {
    private List<GameVO> gameList;
    private Integer total;
    private Integer pageSize;

}
