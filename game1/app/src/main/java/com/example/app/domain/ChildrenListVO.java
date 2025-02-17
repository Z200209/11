package com.example.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class ChildrenListVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
}
