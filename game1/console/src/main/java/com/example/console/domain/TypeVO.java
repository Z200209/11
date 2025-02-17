package com.example.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class TypeVO {
    private BigInteger typeId;
    private BigInteger parentId;
    private String typeName;
    private String image;
    private List<ChildrenListVO> childrenList;
}
