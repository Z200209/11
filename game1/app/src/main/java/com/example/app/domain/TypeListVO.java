package com.example.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TypeListVO {
    private List<TypeVO> typeList;
    private Boolean isEnd;
}
