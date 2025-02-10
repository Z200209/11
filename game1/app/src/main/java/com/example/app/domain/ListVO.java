package com.example.app.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ListVO {
   List<GameVO> gameList;
   private Boolean isEnd;
}
