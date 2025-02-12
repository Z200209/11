package com.example.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class TypeDetailVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
    private String createTime;
    private String updateTime;
}
