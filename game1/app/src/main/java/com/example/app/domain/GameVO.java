package com.example.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GameVO {
   private BigInteger gameId;
   private String gameName;
   private String images;
   private String typeName;
}
