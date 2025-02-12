package com.example.module.entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Type {
//entity body
    //
    private BigInteger id;
    //类型名称
    private String typeName;
    //类型图片
    private String image;
    //创建时间
    private Integer createTime;
    //更新时间
    private Integer updateTime;
    //软删除标记
    private Integer isDeleted;
}
