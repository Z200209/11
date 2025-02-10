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
public class ConsoleGameController {

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
            @RequestParam(name = "typeId" ,required = false) BigInteger typeId,
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
            log.info(e.getMessage());
            return "失败";
        }

    }

    @RequestMapping("/delete")
    public String deleteGame(@RequestParam(name = "gameId") BigInteger gameId) {
        int result = gameService.delete(gameId);
        return result == 1 ? "成功" : "失败";
    }
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }
    @RequestMapping("/info")
    public DetailVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
            Game game = gameService.getById(gameId);
            String formattedCreateTime = formatDate(game.getCreateTime());
            String formattedUpdateTime = formatDate(game.getUpdateTime());
            String TypeName = typeService.getById(game.getTypeId()).getTypeName();
            DetailVO detailVO = new DetailVO();
            return detailVO
                    .setTypeName(TypeName)
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
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "keyword", required=false) String keyword,
                               @RequestParam(name = "typeId", required=false) BigInteger typeId) {
            List<Game> Game = gameService.getAllGame(page, pageSize, keyword, typeId);
            Integer total = gameService.getTotalCount(keyword);
            List <GameVO> gameList = new ArrayList<>();
            for (Game game : Game) {
                GameVO gameVO = new GameVO()
                        .setTypeId(game.getTypeId())
                        .setTypeName(game.getTypeName())
                        .setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setImage(game.getImages().split("\\$")[0]);
                gameList.add(gameVO);

            }
            return new ListVO()
                    .setGameList(gameList)
                    .setTotal(total)
                    .setPageSize(pageSize);
    }
    @RequestMapping("/type/list")
    public TypeListVO typeList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "keyword", required=false) String keyword) {
        List<Type> typeList = typeService.getAll(page, pageSize, keyword);
        Integer total = typeService.getTotalCount(keyword);
        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            typeVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage());
            typeVOList.add(typeVO);
        }
        return new TypeListVO()
                .setTypeList(typeVOList)
                .setTotal(total)
                .setPageSize(pageSize);
    }
    @RequestMapping("/type/create")
    public String createType(@RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image) {
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return "失败";
        }
        try {
           BigInteger typeId = typeService.edit(null, typeName, image);
            return "成功 ID：" + typeId ;
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            return "失败";
        }
    }

    @RequestMapping("/type/update")
    public String updateType(@RequestParam(name = "typeId") BigInteger typeId,
                             @RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image) {
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return "失败";
        }
        try {
            typeService.edit(typeId, typeName, image);
            return "成功 ID:" + typeId;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "失败";
        }
    }
    @GetMapping("/type/delete")
    public String deleteType(@RequestParam(name = "typeId") BigInteger typeId) {
        int result = typeService.delete(typeId);
        gameService.updateTypeIdByOldId(typeId, null);
        return result == 1 ? "成功" : "失败";
    }
}









