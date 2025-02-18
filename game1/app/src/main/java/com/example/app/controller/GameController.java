package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.domain.GameInfoVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.Wp;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        if (game == null) {
            log.info("未找到游戏信息：{}", gameId);
            return null;
        }
        Type type = typeService.getById(game.getTypeId());
        if (type == null) {
            log.info("未找到游戏类型：" + game.getTypeId());
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



    public GameListVO gameList(Wp wp){
        List<GameVO> gameVOList = new ArrayList<>();
        Integer page = wp.getPage();
        Integer pageSize = wp.getPageSize();
        String keyword = wp.getKeyword();
        BigInteger typeId = wp.getTypeId();
        List<Game> gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
        for (Game game : gameList) {
            Type type = typeService.getById(game.getTypeId());
            if (type == null) {
                log.info("未找到游戏类型：" + game.getTypeId());
                continue;
            }
            String typeName = type.getTypeName();
            GameVO gameVO = new GameVO();
            gameVO.setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setImages((game.getImages().split("\\$"))[0])
                    .setTypeName(typeName);
            gameVOList.add(gameVO);
        }
        return new GameListVO()
                .setGameList(gameVOList)
                .setIsEnd(gameList.size() < pageSize);

    }

    @RequestMapping("/list")
    public GameListVO gameListBy(@RequestParam(name = "wp",required = false) String wp,
                                 @RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "typeId", required = false) BigInteger typeId) {
            if (wp == null) {
                Wp wp1 = new Wp();
                wp1.setKeyword(keyword)
                        .setTypeId(typeId)
                        .setPage(1)
                        .setPageSize(10);

                Wp wp2 = new Wp();
                wp2.setKeyword(keyword)
                    .setTypeId(typeId)
                     .setPage(2)
                        .setPageSize(10);
                String json = JSON.toJSONString(wp2);
                String encodedWp = Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
                GameListVO gameList= gameList(wp1);
                return new GameListVO()
                        .setGameList(gameList.getGameList())
                        .setIsEnd(gameList.getIsEnd())
                        .setWp(encodedWp);

            }
            else {
                byte[] bytes = Base64.getUrlDecoder().decode(wp.getBytes(StandardCharsets.UTF_8));
                String decodedWp = new String(bytes, StandardCharsets.UTF_8);
                String json = JSON.parseObject(decodedWp, String.class);
                Wp wp1 = JSON.parseObject(json, Wp.class);
                int page = wp1.getPage();
                if(page == 1){
                 return null;
                }
                else{
                 Wp wp2 = new Wp();
                 wp2.setKeyword(wp1.getKeyword())
                         .setTypeId(wp1.getTypeId())
                         .setPage(page + 1)
                         .setPageSize(10);
                 String encodedWp = Base64.getUrlEncoder().encodeToString(JSON.toJSONString(wp2).getBytes(StandardCharsets.UTF_8));
                    GameListVO gameList= gameList(wp1);
                    return new GameListVO()
                            .setGameList(gameList.getGameList())
                            .setIsEnd(gameList.getIsEnd())
                            .setWp(encodedWp);

                 }
             }

             }
    }




