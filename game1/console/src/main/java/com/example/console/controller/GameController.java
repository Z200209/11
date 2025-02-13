package com.example.console.controller;
import com.example.console.domain.*;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/game/console")
@Slf4j
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/create")
    public String createGame(
            @RequestParam(name = "typeId",required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images)

    {

        gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();

        if (gameName.isEmpty()){
            log.info("游戏名称不能为空字符串");
        return "失败";
        }
        if (price < 0){
            log.info("游戏价格不能为负数");
            return "失败";
        }
        if (gameIntroduction.isEmpty()){
            log.info("游戏介绍不能为空字符串");
            return "失败";
        }
        try {
          BigInteger insertId = gameService.edit(null, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
          return "成功, ID: " + insertId;
        }
        catch (RuntimeException e)  {
            log.info(e.getMessage());
            return "失败";
        }

    }


    @RequestMapping("/update")
    public String updateGame(
            @RequestParam(name = "typeId" ) BigInteger typeId,
            @RequestParam(name = "gameId") BigInteger gameId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images)
    {   gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();

        if (gameName.isEmpty()) {
            log.info("游戏名称不能为空字符串");
            return "失败";
        }
        if (price < 0){
            log.info("游戏价格不能为负数");
            return "失败";
        }
        if (gameIntroduction.isEmpty()) {
            log.info("游戏介绍不能为空字符串");
            return "失败";
        }
        try {
            gameService.edit(gameId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
            return "成功, ID: " + gameId;
        }
        catch (RuntimeException e)  {
            log.info(e.getLocalizedMessage());
            return "失败";
        }

    }

    @RequestMapping("/delete")
    public String deleteGame(@RequestParam(name = "gameId") BigInteger gameId) {
       try
       {
           int result = gameService.delete(gameId);
           return result == 1 ? "成功" : "失败";
        }
        catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return "失败";
        }
    }
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }
    @RequestMapping("/info")
    public DetailVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
            Game game = gameService.getById(gameId);
            if (game == null){
                log.info("未找到游戏信息：{}", gameId);
                return null;
            }
            String formattedCreateTime = formatDate(game.getCreateTime());
            String formattedUpdateTime = formatDate(game.getUpdateTime());
            BigInteger typeId = game.getTypeId();
            Type type = typeService.getById(typeId);
             if (type == null){
                 log.info("未找到游戏类型：{}", typeId);
                 return null;
             }
             String typeName = type.getTypeName();
             if (typeName == null){
                 log.info("未找到游戏类型名称：{}", typeId);
             }
             String typeImage = type.getImage();
             if (typeImage == null){
                 log.info("未找到游戏类型图片：{}", typeId);
             }

            DetailVO detailVO = new DetailVO();
            return detailVO
                    .setTypeName(typeName)
                    .setTypeImage(typeImage)
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setPrice(game.getPrice())
                    .setGameIntroduction(game.getGameIntroduction())
                    .setGameDate(game.getGameDate())
                    .setGamePublisher(game.getGamePublisher())
                    .setImages(Arrays.asList(game.getImages().split("\\$")))
                    .setCreateTime(formattedCreateTime)
                    .setUpdateTime(formattedUpdateTime);
        }

    @RequestMapping("/list")
    public ListVO gameList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "keyword", required=false) String keyword,
                           @RequestParam(name = "typeId", required=false) BigInteger typeId) {
            int pageSize = 10;
            List<Game> Game = gameService.getAllGame(page, pageSize, keyword, typeId);
            if (Game.isEmpty()){
                log.info("未找到游戏信息：");
                return null;
            }
            Integer total = gameService.getTotalCount(keyword);
            if (total == null){
                log.info("查询数据错误");
            }

            List <GameVO> gameList = new ArrayList<>();
            for (Game game : Game) {
                Type type = typeService.getById(game.getTypeId());
                if (type == null){
                    log.info("未找到游戏类型："+ game.getTypeId());
                    continue;
                }
                String typeName = type.getTypeName();
                if (typeName == null) {
                    log.info("未找到游戏类型名称："+ type.getId());
                }
                GameVO gameVO = new GameVO()
                        .setTypeId(game.getTypeId())
                        .setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setTypeName(typeName)
                        .setImage(game.getImages().split("\\$")[0]);
                gameList.add(gameVO);
            }
            return new ListVO()
                    .setGameList(gameList)
                    .setTotal(total)
                    .setPageSize(pageSize);
    }

}









