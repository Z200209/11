package com.example.app.controller;

import com.example.app.domain.DetailVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.ListVO;
import com.example.app.domain.TypeVO;
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
public class AppGameController {

    private static final Logger log = LoggerFactory.getLogger(AppGameController.class);
    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/info")
    public DetailVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        Game game = gameService.getById(gameId);
        DetailVO detailVO = new DetailVO();
        String TypeName = typeService.getById(game.getTypeId()).getTypeName();
        String TypeImage = typeService.getById(game.getTypeId()).getImage();
        return detailVO
                .setGameId(game.getId())
                .setTypeName(TypeName)
                .setTypeImage(TypeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameIntroduction(game.getGameIntroduction())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher())
                .setImages(Arrays.asList(game.getImages().split("\\$"))); // 将图片字符串按 "$" 拆分为列表
    }


    @RequestMapping("/list")
    public ListVO gameList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                           @RequestParam(name = "keyword", required=false)String keyword,
                           @RequestParam(name = "typeId", required=false)BigInteger typeId) {
        List<Game> Game = gameService.getAllGame(page, pageSize, keyword,typeId);
        List<GameVO> gameList = new ArrayList<>();
           for (Game game : Game) {
               GameVO gameVO = new GameVO();
               gameVO.setGameId(game.getId())
                       .setGameName(game.getGameName())
                       .setTypeName(game.getTypeName())
                       .setImages((game.getImages().split("\\$"))[0]);
               gameList.add(gameVO);
           }
        return new ListVO()
                .setGameList(gameList)
                .setIsEnd(gameList.size()<pageSize);

    }
    @RequestMapping("/type/list")
    public List<TypeVO> typeList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "keyword", required=false)String keyword) {
        List<Type> typeList = typeService.getAll(page, pageSize, keyword);
        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            typeVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage());
            typeVOList.add(typeVO);
        }
        return typeVOList;
    }


    @GetMapping("/test")
    public String test() {
        throw new RuntimeException("模拟异常");
    }


}




