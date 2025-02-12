package com.example.module.entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Game {
//entity body
    //
    private BigInteger id;
    //游戏名字
    private String gameName;
    //价格
    private Float price;
    //游戏介绍
    private String gameIntroduction;
    //发行日期
    private String gameDate;
    //发行商
    private String gamePublisher;
    //轮播图
    private String images;
    //
    private Integer createTime;
    //
    private Integer updateTime;
    //
    private Integer isDeleted;
    //关联类型表ID
    private BigInteger typeId;
}
