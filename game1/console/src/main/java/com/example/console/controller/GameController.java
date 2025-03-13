package com.example.console.controller;

import com.alibaba.fastjson.JSON;
import com.example.console.domain.*;
import com.example.module.entity.Game;
import com.example.module.entity.Sign;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import com.example.module.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/game/console")
@Slf4j
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private UserService userService;

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
                           @RequestParam(name = "typeId", required=false) BigInteger typeId,
                           HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String sign = null;
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("auth_token")) {
                sign = cookie.getValue();
                break;
            }
        }
        if (sign == null) {
            return null;
        }
        byte[] bytes = Base64.getUrlDecoder().decode(sign);
        String json = new String(bytes, StandardCharsets.UTF_8);
        Sign reviceSign = JSON.parseObject(json, Sign.class);
        if (reviceSign.getExpirationTime() < (int) (System.currentTimeMillis() / 1000)) {
            return null;
        }
        if (userService.getUserById(reviceSign.getId()) == null) {
            return null;
        }
        int pageSize = 10;
        List<Game> gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
        Integer total = gameService.getTotalCount(keyword);
        Set<BigInteger> typeIdSet = new HashSet<>();
        for (Game game : gameList) {
            BigInteger tid = game.getTypeId();
            if (tid != null) {
                typeIdSet.add(tid);
            }
        }
        List<Type> types = new ArrayList<>();
        if (!typeIdSet.isEmpty()) {
            types = typeService.getTypeByIds(typeIdSet);
        }
        Map<BigInteger, String> typeMap = new HashMap<>();
        for (Type type : types) {
            typeMap.put(type.getId(), type.getTypeName());
        }
        if (total == null) {
            log.info("查询数据错误");
        }
        List<GameVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
            String typeName = typeMap.get(game.getTypeId());
            if (typeName == null) {
                log.info("未找到游戏类型名称：{}", game.getTypeId());
                continue;
            }
            GameVO gameVO = new GameVO()
                    .setTypeId(game.getTypeId())
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(typeName)
                    .setImage(game.getImages().split("\\$")[0]);
            gameVOList.add(gameVO);
        }
        return new ListVO()
                .setGameList(gameVOList)
                .setTotal(total)
                .setPageSize(pageSize);
    }

}













