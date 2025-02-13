package com.example.app.controller;

import com.example.app.domain.GameInfoVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/game/app")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/info")
    public GameInfoVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        Game game = gameService.getById(gameId);
        if (game == null){
            log.info("未找到游戏信息：{}", gameId);
            return null;
        }
        Type type = typeService.getById(game.getTypeId());
        if (type == null){
            log.info("未找到游戏类型："+ game.getTypeId());
            return null;
        }
        String typeName = type.getTypeName();
        if (typeName == null) {
            log.info("未找到游戏类型名称：{}", type.getId());
        }
        String typeImage = type.getImage();
        if (typeImage == null) {
            log.info("未找到游戏类型图片：{}", type.getId());
        }

        GameInfoVO detailVO = new GameInfoVO();
        return detailVO
                .setGameId(game.getId())
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameIntroduction(game.getGameIntroduction())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher())
                .setImages(Arrays.asList(game.getImages().split("\\$"))); // 将图片字符串按 "$" 拆分为列表
    }


    @RequestMapping("/list")
    public GameListVO gameList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                               @RequestParam(name = "keyword", required=false)String keyword,
                               @RequestParam(name = "typeId", required=false)BigInteger typeId) {
        int pageSize = 10;
        List<Game> Game = gameService.getAllGame(page, pageSize, keyword,typeId);
        if (Game == null){
            log.info("未找到游戏信息：");
            return null;
        }
        List<GameVO> gameList = new ArrayList<>();
           for (Game game : Game) {
               Type type = typeService.getById(game.getTypeId());
               if (type == null){
                   log.info("未找到游戏类型："+ game.getTypeId());
                   continue;
               }
               String typeName = type.getTypeName();
               GameVO gameVO = new GameVO();
               gameVO.setGameId(game.getId())
                       .setGameName(game.getGameName())
                       .setImages((game.getImages().split("\\$"))[0])
                       .setTypeName(typeName);
               gameList.add(gameVO);
           }
        return new GameListVO()
                .setGameList(gameList)
                .setIsEnd(gameList.size()<pageSize);

    }



    @GetMapping("/test")
    public String test() {
        throw new RuntimeException("模拟异常");
    }


}




