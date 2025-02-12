package com.example.app.controller;

import com.example.app.domain.DetailVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.ListVO;
import com.example.app.domain.TypeVO;

import com.example.module.entity.Game;
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






    @GetMapping("/test")
    public String test() {
        throw new RuntimeException("模拟异常");
    }


}




