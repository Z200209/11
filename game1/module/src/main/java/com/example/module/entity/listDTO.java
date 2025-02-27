package com.example.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Accessors(chain = true)
public class listDTO {
    private BigInteger id;
    private String gameName;
    private String images;
    private BigInteger typeId;
    private String typeName;
}
