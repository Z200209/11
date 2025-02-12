package com.example.app.controller;

import com.example.app.domain.GameInfoVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/game/app")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/info")
    public GameInfoVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        Game game = gameService.getById(gameId);
        Type type = typeService.getById(game.getTypeId());
        String typeName = type.getTypeName();
        String typeImage = type.getImage();

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
        List<GameVO> gameList = new ArrayList<>();
           for (Game game : Game) {
               GameVO gameVO = new GameVO();
               gameVO.setGameId(game.getId())
                       .setGameName(game.getGameName())
                       .setImages((game.getImages().split("\\$"))[0])
                       .setTypeName(typeService.getById(game.getTypeId()).getTypeName());
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




