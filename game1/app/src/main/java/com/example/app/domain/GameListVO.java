package com.example.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GameListVO {
   List<GameVO> gameList;
   private Boolean isEnd;
   private String wp;
}
