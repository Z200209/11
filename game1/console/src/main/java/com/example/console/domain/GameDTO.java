package com.example.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Accessors(chain = true)
public class GameDTO {
    private BigInteger typeId;
    private String gameName;
    private Float price;
    private String gameIntroduction;
    private String gameDate;
    private String gamePublisher;
    private String images;
}
