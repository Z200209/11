package com.example.app.controller;

import com.example.app.domain.GameInfoVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.GameListVO;
import com.example.module.entity.GameDTO;
import com.example.module.service.GameService;
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

    private static final Logger log = LoggerFactory.getLogger(TypeController.class);
    @Autowired
    private GameService gameService;

    @RequestMapping("/info")
    public GameInfoVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        GameDTO game = gameService.getById(gameId);
        GameInfoVO detailVO = new GameInfoVO();
        return detailVO
                .setGameId(game.getId())
                .setTypeName(game.getTypeName())
                .setTypeImage(game.getTypeImage())
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
        List<GameDTO> Game = gameService.getAllGame(page, pageSize, keyword,typeId);

        List<GameVO> gameList = new ArrayList<>();
           for (GameDTO game : Game) {
               GameVO gameVO = new GameVO();
               gameVO.setGameId(game.getId())
                       .setGameName(game.getGameName())
                       .setTypeName(game.getTypeName())
                       .setImages((game.getImages().split("\\$"))[0]);
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




